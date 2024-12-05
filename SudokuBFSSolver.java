import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

// Class that implements a Breadth-First Search approach to solving Sudoku puzzles
// Improvement: Keeps track of expanded nodes and uses a graph to guide exploration, reducing invalid states
public class SudokuBFSSolver extends SudokuGraph {

    private int nodesExpanded; // Counter for the number of nodes expanded

    // Constructor that initializes the solver with a given grid size
    // Improvement: makes the grid size flexible, allowing the same graph structure to work for different Sudoku sizes (6x6, 12x12)
    public SudokuBFSSolver(int gridSize) {
        super(gridSize);
        this.nodesExpanded = 0; // Initialize the counter
    }

    // Getter for the number of nodes expanded
    public int getNodesExpanded() {
        return nodesExpanded;
    }

    @Override
    public List<Map<Integer, Integer>> solve() {
        // Store all valid solutions found
        List<Map<Integer, Integer>> solutions = new ArrayList<>();
        // Queue for BFS traversal - stores partial puzzle states to explore
        Queue<Map<Integer, Integer>> queue = new LinkedList<>();
        // Add initial puzzle state to the queue
        queue.add(new HashMap<>(puzzle));

        while (!queue.isEmpty()) {
            // Get the next puzzle state to examine
            Map<Integer, Integer> currentState = queue.poll();
            nodesExpanded++; // Increment the counter when a node is expanded

            // If current state is a complete solution, add to solutions list if valid
            if (isSolved(currentState)) {
                if (isValidSolution(currentState)) {
                    solutions.add(new HashMap<>(currentState));
                }
                continue;
            }

            // Find the next empty cell to fill
            int nextCell = getNextEmptyCell(currentState);
            if (nextCell == -1) {
                continue; // No empty cells left; proceed to next state
            }

            // Improvement: prevent invalid states by applying Sudoku rules directly in the graph, cutting down on extra node expansions
            // Try each valid number
            for (int choice : validChoices) {
                // Check if the number can be legally placed in the cell
                if (isValidChoice(currentState, nextCell, choice)) {
                    // Create new state with the number placed
                    Map<Integer, Integer> newState = new HashMap<>(currentState);
                    newState.put(nextCell, choice);
                    queue.add(newState);  // Add new state to queue for further exploration

                }
            }
        }

        // Improvement: tracks the number of nodes expanded to measure performance and compare with DLS
        return solutions; // Return all solutions found
    }
}
