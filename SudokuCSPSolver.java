import java.util.*;

/**
 * A class representing a Sudoku solver using the CSP approach with backtracking and constraint satisfaction heuristics.
 *Summary: This CSP (Constraint Satisfaction Problem) implementation is significantly more efficient than traditional DFS/BFS approaches due to the following reasons:

    1. Intelligent Variable Selection (MRV - Minimum Remaining Values): The algorithm selects cells that are most constrained (i.e., have the fewest possible values) first. This reduces the chances of exploring unpromising branches, leading to faster convergence.

    2. Value Ordering (LCV - Least Constraining Value): When assigning values, the algorithm chooses the value that imposes the fewest constraints on neighboring cells. This ensures that future choices are as flexible as possible, thereby minimizing the likelihood of dead ends.

    3. Forward Checking with Constraint Propagation: The algorithm uses forward checking and constraint propagation to eliminate invalid options before making assignments. This dramatically reduces the search space by avoiding states that would inevitably lead to conflicts.

    Unlike traditional DFS/BFS approaches that blindly try every possible value, this CSP implementation leverages the inherent structure of the problem to make informed choices, resulting in much greater efficiency and scalability.
 */
public class SudokuCSPSolver extends SudokuGraph {

    private int nodesExpanded;

    /**
     * Constructor to create a new Sudoku CSP Solver of the given size.
     * @param gridSize The size of the Sudoku grid (e.g., 9 for a 9x9 puzzle)
     */
    public SudokuCSPSolver(int gridSize) {
        super(gridSize);
        this.nodesExpanded = 0;
    }

    /**
     * Solves the puzzle using CSP with backtracking and returns all possible solutions.
     * @return A list of all possible solutions found.
     */
    @Override
    public List<Map<Integer, Integer>> solve() {
        List<Map<Integer, Integer>> solutions = new ArrayList<>();
        backtrack(solutions);
        return solutions; // Return all solutions found
    }

    /**
     * Backtracking algorithm to solve the Sudoku puzzle.
     * @param solutions The list to store valid solutions found
     * @return True if a solution was found, otherwise false
     */
    private boolean backtrack(List<Map<Integer, Integer>> solutions) {
        // Use MRV heuristic to select the most constrained cell
        int[] cell = getNextCell();
        
        // If no empty cells remain, we've found a valid solution
        if (cell == null) {
            // Create a deep copy of the current puzzle state to preserve the solution
            solutions.add(new HashMap<>(puzzle));
            // Return false to continue searching for more solutions
            return false;
        }
        
        // Convert 2D coordinates to 1D index for puzzle map
        int row = cell[0];
        int col = cell[1];
        int cellIndex = row * gridSize + col;

        // Track the number of decision points in the search tree
        nodesExpanded++;

        // Get values sorted by LCV heuristic to try least constraining values first
        List<Integer> values = getSortedValues(row, col);
        for (int value : values) {
            // Forward checking: only try values that don't violate constraints
            if (isValidChoice(puzzle, cellIndex, value)) {
                // Make the assignment
                puzzle.put(cellIndex, value);
                // Recursively solve the rest of the puzzle
                backtrack(solutions);
                // Undo the assignment to try other values (backtracking)
                puzzle.put(cellIndex, 0);
            }
        }
        return false;
    }

    /**
     * Gets the next cell to fill based on the Minimum Remaining Values (MRV) heuristic.
     * @return The row and column indices of the next cell, or null if no empty cell is left
     */
    private int[] getNextCell() {
        // Initialize variables for MRV heuristic
        int minOptions = Integer.MAX_VALUE;
        int[] bestCell = null;

        // Iterate through all cells to find the one with fewest legal values
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int cellIndex = row * gridSize + col;
                // Only consider empty cells
                if (puzzle.get(cellIndex) == 0) {
                    // Count number of legal values for this cell
                    int options = getPossibleValues(row, col).size();
                    // Update if this cell has fewer options than previous best
                    if (options < minOptions) {
                        minOptions = options;
                        bestCell = new int[]{row, col};
                    }
                }
            }
        }
        return bestCell;
    }

    /**
     * Gets the sorted values for a cell based on the Least Constraining Value (LCV) heuristic.
     * @param row The row of the cell
     * @param col The column of the cell
     * @return A list of sorted possible values
     */
    private List<Integer> getSortedValues(int row, int col) {
        int cellIndex = row * gridSize + col;
        // Get all legal values for this cell
        List<Integer> values = getPossibleValues(row, col);
        // Sort values based on how many future assignments they would restrict
        values.sort(Comparator.comparingInt(value -> countConstraints(cellIndex, value)));
        return values;
    }

    /**
     * Gets the possible values for a given cell.
     * @param row The row of the cell
     * @param col The column of the cell
     * @return A list of possible values
     */
    private List<Integer> getPossibleValues(int row, int col) {
        boolean[] used = new boolean[gridSize + 1];
        int cellIndex = row * gridSize + col;

        for (int neighbor : graph.get(cellIndex)) {
            int neighborValue = puzzle.get(neighbor);
            if (neighborValue != 0) {
                used[neighborValue] = true;
            }
        }

        List<Integer> values = new ArrayList<>();
        for (int i = 1; i <= gridSize; i++) {
            if (!used[i]) {
                values.add(i);
            }
        }
        return values;
    }

    /**
     * Counts how many cells a value would constrain if placed in the given cell.
     * @param cellIndex The index of the cell
     * @param value The value to place in the cell
     * @return The number of cells that would be constrained by this value
     */
    private int countConstraints(int cellIndex, int value) {
        int count = 0;
        for (int neighbor : graph.get(cellIndex)) {
            if (puzzle.get(neighbor) == 0 && isValidChoice(puzzle, neighbor, value)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of nodes expanded during the search.
     * @return The number of nodes expanded
     */
    public int getNodesExpanded() {
        return nodesExpanded;
    }
}
