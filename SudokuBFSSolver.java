import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

// Class that implements a Breadth-First Search approach to solving Sudoku puzzles
public class SudokuBFSSolver extends SudokuGraph {

    // Constructor that initializes the solver with a given grid size
    public SudokuBFSSolver(int gridSize) {
        super(gridSize);
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
            // If current state is a complete solution, add to solutions list
            if (isSolved(currentState)) {
                solutions.add(new HashMap<>(currentState));
                continue;
            }

            // Find the next empty cell to fill
            int nextCell = getNextEmptyCell(currentState);
            // Try each valid number (1-9 for standard Sudoku)
            for (int choice : validChoices) {
                // Check if the number can be legally placed in the cell
                if (isValidChoice(currentState, nextCell, choice)) {
                    // Create new state with the number placed
                    Map<Integer, Integer> newState = new HashMap<>(currentState);
                    newState.put(nextCell, choice);
                    // Add new state to queue for further exploration
                    queue.add(newState);
                }
            }
        }

        // Return all solutions found (could be multiple for invalid puzzles)
        return solutions;
    }
}
