import java.util.*;

/**
 * Sudoku solver using Depth-Limited Search (DLS) algorithm.
 * This class extends SudokuGraph and implements the DLS algorithm
 * to solve Sudoku puzzles, recording all possible solutions.
 *
 * Based on the research paper:
 * "Comparison Analysis of Breadth First Search and Depth Limited Search Algorithms in Sudoku Game"
 * by Reza Bustami et al.
 *
 * Modifications:
 * - Applied heuristics to prioritize nodes with fewer constraints.
 * - Adjusted the algorithm to work effectively on larger grids.
 */
public class SudokuDLSSolver extends SudokuGraph {

    private int depthLimit; // Maximum depth for the DLS algorithm
    private int nodesExpanded; // Counter for the number of nodes expanded

    // Constructor that initializes the solver with a given grid size and depth limit
    public SudokuDLSSolver(int gridSize, int depthLimit) {
        super(gridSize);
        this.depthLimit = depthLimit;
        this.nodesExpanded = 0; // Initialize the counter
    }

    @Override
    public List<int[]> solve() {
        // Store all valid solutions found
        List<int[]> solutions = new ArrayList<>();
        // Start the recursive DLS from depth 0
        int[] initialState = puzzleArray.clone();
        depthLimitedSearch(initialState, 0, solutions);
        return solutions;
    }

    // Recursive method for Depth-Limited Search
    private boolean depthLimitedSearch(int[] state, int depth, List<int[]> solutions) {
        // Increment the node expansion counter
        nodesExpanded++;

        // Check if the state is a solution
        if (isSolved(state)) {
            if (isValidSolution(state)) {
                // Add a copy of the solution to the list
                solutions.add(state.clone());
            }
            return false; // Continue searching for other solutions
        }

        // Check if the depth limit has been reached
        if (depth >= depthLimit) {
            return false; // Cut off the search at this depth
        }

        // Find the next empty cell using Least Remaining Value heuristic
        int nextCell = getNextEmptyCellWithLRV(state);
        if (nextCell == -1) {
            return false; // No empty cells but puzzle is not solved, backtrack
        }

        // Get valid choices for the next cell
        Set<Integer> choices = getValidChoicesForCell(state, nextCell);

        // Try all valid choices for the next cell
        for (int choice : choices) {
            if (isValidChoice(state, nextCell, choice)) {
                // Place the choice in the state
                state[nextCell] = choice;
                // Recurse to the next depth level
                depthLimitedSearch(state, depth + 1, solutions);
                // Backtrack: remove the choice
                state[nextCell] = 0;
            }
        }
        return false; // No valid choices led to a solution at this depth
    }

    // Getter for the number of nodes expanded
    public int getNodesExpanded() {
        return nodesExpanded;
    }

    /**
     * Uses the Least Remaining Value heuristic to select the next cell.
     * Chooses the empty cell with the fewest legal values.
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
