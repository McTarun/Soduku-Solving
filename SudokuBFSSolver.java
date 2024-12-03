import java.util.*;

/**
 * Sudoku solver using Iterative Deepening Breadth-First Search (IDBFS) algorithm.
 * This class extends SudokuGraph and implements the BFS algorithm with iterative deepening
 * to solve Sudoku puzzles, recording all possible solutions.
 *
 * Based on the research paper:
 * "Comparison Analysis of Breadth First Search and Depth Limited Search Algorithms in Sudoku Game"
 * by Reza Bustami et al.
 *
 * Modifications:
 * - Implemented Iterative Deepening BFS to limit memory usage.
 * - Applied heuristics to prioritize nodes with fewer constraints.
 * - Adjusted the algorithm to work effectively on larger grids.
 */
public class SudokuBFSSolver extends SudokuGraph {

    private int nodesExpanded; // Counter for the number of nodes expanded

    // Constructor that initializes the solver with a given grid size
    public SudokuBFSSolver(int gridSize) {
        super(gridSize);
        this.nodesExpanded = 0; // Initialize the counter
    }

    // Getter for the number of nodes expanded
    public int getNodesExpanded() {
        return nodesExpanded;
    }

    @Override
    public List<int[]> solve() {
        List<int[]> solutions = new ArrayList<>();
        int maxDepth = gridSize * gridSize - countFilledCells(puzzleArray);
        for (int depth = 0; depth <= maxDepth; depth++) {
            if (iterativeDeepeningBFS(depth, solutions)) {
                break; // Stop if solutions are found
            }
        }
        return solutions;
    }

    /**
     * Performs Iterative Deepening BFS up to a given depth.
     *
     * @param maxDepth   The maximum depth to search.
     * @param solutions  The list to store found solutions.
     * @return true if at least one solution is found; false otherwise.
     */
    private boolean iterativeDeepeningBFS(int maxDepth, List<int[]> solutions) {
        Queue<Node> queue = new LinkedList<>();
        Set<String> visitedStates = new HashSet<>();

        // Start from the initial state
        Node initialNode = new Node(puzzleArray.clone(), 0);
        queue.add(initialNode);
        visitedStates.add(compressState(initialNode.state));

        boolean foundSolution = false;

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            nodesExpanded++;

            // Check if the current state is a solution
            if (isSolved(currentNode.state)) {
                if (isValidSolution(currentNode.state)) {
                    solutions.add(currentNode.state.clone());
                    foundSolution = true;
                }
                continue;
            }

            // Check depth limit
            if (currentNode.depth >= maxDepth) {
                continue;
            }

            // Find the next empty cell
            int nextCell = getNextEmptyCellWithLRV(currentNode.state);
            if (nextCell == -1) {
                continue;
            }

            // Get valid choices for the next cell
            Set<Integer> choices = getValidChoicesForCell(currentNode.state, nextCell);

            // Try each valid choice
            for (int choice : choices) {
                int[] newState = currentNode.state.clone();
                newState[nextCell] = choice;

                // Compress the state for duplicate detection
                String compressedState = compressState(newState);

                if (!visitedStates.contains(compressedState)) {
                    queue.add(new Node(newState, currentNode.depth + 1));
                    visitedStates.add(compressedState);
                }
            }
        }

        return foundSolution;
    }

    /**
     * Counts the number of filled cells in the puzzle.
     *
     * @param state The puzzle state.
     * @return The number of filled cells.
     */
    private int countFilledCells(int[] state) {
        int count = 0;
        for (int value : state) {
            if (value != 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Compresses the puzzle state into a string for duplicate detection.
     *
     * @param state The puzzle state array.
     * @return A string representing the compressed state.
     */
    private String compressState(int[] state) {
        StringBuilder sb = new StringBuilder();
        for (int value : state) {
            sb.append(value).append(',');
        }
        return sb.toString();
    }

    /**
     * Node class to represent a state in the search tree with its depth.
     */
    private static class Node {
        int[] state;
        int depth;

        Node(int[] state, int depth) {
            this.state = state;
            this.depth = depth;
        }
    }

    /**
     * Uses the Least Remaining Value heuristic to select the next cell.
     *
     * @param state The current puzzle state.
     * @return The index of the next cell to fill.
     */
    protected int getNextEmptyCellWithLRV(int[] state) {
        int minOptions = Integer.MAX_VALUE;
        int bestCell = -1;
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) {
                Set<Integer> options = getValidChoicesForCell(state, i);
                int optionCount = options.size();
                if (optionCount < minOptions) {
                    minOptions = optionCount;
                    bestCell = i;
                    if (minOptions == 1) {
                        break; // Optimal cell found
                    }
                }
            }
        }
        return bestCell;
    }

    /**
     * Gets the valid choices for a specific cell.
     *
     * @param state The current puzzle state.
     * @param cell  The index of the cell.
     * @return A set of valid choices for the cell.
     */
    protected Set<Integer> getValidChoicesForCell(int[] state, int cell) {
        Set<Integer> invalidChoices = new HashSet<>();
        for (Integer neighbor : graph.get(cell)) {
            int neighborValue = state[neighbor];
            if (neighborValue != 0) {
                invalidChoices.add(neighborValue);
            }
        }
        Set<Integer> validChoicesForCell = new HashSet<>(validChoices);
        validChoicesForCell.removeAll(invalidChoices);
        return validChoicesForCell;
    }
}
