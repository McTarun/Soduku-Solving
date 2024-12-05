import java.io.PrintWriter;
import java.util.*;

/**
 * Represents a Sudoku puzzle as a graph data structure.
 * This abstract class provides the core functionality for solving Sudoku puzzles
 * using graph-based algorithms like BFS and DLS.
 * The puzzle is represented as a graph where:
 * - Each cell is a vertex
 * - Edges connect cells that cannot contain the same value (rows, columns, and sections)
 */
public abstract class SudokuGraph {

    // Core data structure fields with detailed explanations
    protected final int gridSize;        // Size of the entire grid (e.g., 9 for 9x9 puzzle)
    protected final int sectionSize;     // Size of each subsection/box (e.g., 3 for 9x9 puzzle)
    protected final Map<Integer, Set<Integer>> graph;  // Maps each cell to its constrained neighbors (same row/col/box)
    protected final Map<Integer, Integer> puzzle;      // Current state: cell position -> value (0 = empty)
    protected final Set<Integer> validChoices;         // Valid values (1 to gridSize) that can be placed in cells

    /**
     * Initializes a new Sudoku puzzle of the specified size.
     * @param gridSize The size of the grid (e.g., 9 for 9x9 puzzle)
     */
    public SudokuGraph(int gridSize) {
        // Store the overall grid size (e.g., 9 for 9x9 puzzle)
        this.gridSize = gridSize;
        
        // Calculate the size of each section/box (e.g., 3 for 9x9 puzzle)
        this.sectionSize = (int) Math.sqrt(gridSize);
        
        // Initialize the constraint graph as empty
        this.graph = new HashMap<>();
        
        // Initialize the puzzle state as empty
        this.puzzle = new HashMap<>();
        
        // Create set of valid values (1 to gridSize)
        this.validChoices = new HashSet<>();
        for (int i = 1; i <= gridSize; i++) {
            validChoices.add(i);
        }
    }

    /**
     * Parses the input puzzle from a list of strings.
     * Each string represents one row, where:
     * - '.' represents an empty cell
     * - digits 1-9 represent themselves
     * - letters a-g represent values 10-16 (for larger puzzles)
     * 
     * @param lines List of strings representing puzzle rows
     * @throws IllegalArgumentException if row length is invalid
     */
    public void parseInput(List<String> lines) {
        // Iterate through each row of the puzzle grid
        for (int row = 0; row < gridSize; row++) {
            // Get the string representing the current row
            String line = lines.get(row);
            
            // Validate that the row length matches the expected grid size
            // This ensures the puzzle is properly formatted as an n×n grid
            if (line.length() != gridSize) {
                throw new IllegalArgumentException("Invalid row length at row " + (row + 1));
            }

            // Process each character (cell) in the current row
            for (int col = 0; col < gridSize; col++) {
                // Convert 2D coordinates (row,col) to 1D array index
                // Example: For 9×9 grid, (2,3) becomes 2*9 + 3 = 21
                int cell = row * gridSize + col;
                
                // Get the character representing the cell's value
                char value = line.charAt(col);
                
                if (value == '.') {
                    // Empty cell - store as 0 in the puzzle map
                    puzzle.put(cell, 0);
                } else {
                    // Convert character to numeric value using conditional operator:
                    // If it's a digit ('1'-'9'): subtract ASCII '0' to get numeric value
                    // If it's a letter ('a'-'g'): convert to values 10-16
                    // Example: 'a' - 'a' + 10 = 10, 'b' - 'a' + 10 = 11, etc.
                    puzzle.put(cell, Character.isDigit(value) ? value - '0' : value - 'a' + 10);
                }
            }
        }
        
        // After parsing all cells, build the constraint graph
        // This creates the adjacency lists representing which cells
        // cannot contain the same value (rows, columns, and sections)
        buildGraph();
    }

    /**
     * Builds the graph representation of the Sudoku puzzle.
     * Creates edges between cells that cannot contain the same value:
     * - All cells in the same row
     * - All cells in the same column
     * - All cells in the same section (box)
     */

    // Improvement: represents the Sudoku grid as a graph to enforce row, column, and box rules, making it much easier to avoid invalid states in BFS and DLS
    private void buildGraph() {
        // Process each cell in the grid
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                // Convert 2D position to 1D index
                int cell = row * gridSize + col;
                
                // Ensure the cell has a constraint set in the graph
                graph.putIfAbsent(cell, new HashSet<>());
                
                // Add all cells in the same row as constraints
                // Add row, column, and section constraints
                for (int k = 0; k < gridSize; k++) {
                    graph.get(cell).add(row * gridSize + k);  // Add all cells in current row
                    graph.get(cell).add(k * gridSize + col);  // Add all cells in current column
                }
                
                // Calculate the starting position of the current section/box
                int startRow = (row / sectionSize) * sectionSize;
                int startCol = (col / sectionSize) * sectionSize;
                
                // Add all cells in the same section/box as constraints
                for (int i = 0; i < sectionSize; i++) {
                    for (int j = 0; j < sectionSize; j++) {
                        graph.get(cell).add((startRow + i) * gridSize + startCol + j);
                    }
                }
                
                // Remove self from constraints (cell doesn't constrain itself)
                graph.get(cell).remove(cell);
            }
        }
    }

    /**
     * Abstract method to be implemented by concrete solver classes.
     * @return List of all valid solutions found
     */
    public abstract List<Map<Integer, Integer>> solve();

    /**
     * Checks if the current puzzle state represents a complete solution.
     * @param state Current puzzle state
     * @return true if all cells are filled with non-zero values
     */
    protected boolean isSolved(Map<Integer, Integer> state) {
        // Check each cell in the current state
        for (Integer value : state.values()) {
            // If any cell is empty (0 or negative), puzzle isn't solved
            if (value <= 0) {
                return false;
            }
        }
        // All cells have valid positive values
        return true;    
    }

    /**
     * Finds the next empty cell in the puzzle.
     * @param state Current puzzle state
     * @return Position of next empty cell, or -1 if none found
     */
    protected int getNextEmptyCell(Map<Integer, Integer> state) {
        // Search through all cells in the current state
        for (Map.Entry<Integer, Integer> entry : state.entrySet()) {
            // Return the first cell found with value 0 (empty)
            if (entry.getValue() == 0) {
                return entry.getKey();
            }
        }
        // No empty cells found
        return -1;    
    }

    /**
     * Validates if a value can be placed in a cell without violating Sudoku rules.
     * Checks against all neighboring cells (same row, column, or section).
     * 
     * @param state Current puzzle state
     * @param cell Cell position to check
     * @param choice Value to validate
     * @return true if the choice is valid for the given cell
     */
    protected boolean isValidChoice(Map<Integer, Integer> state, int cell, int choice) {
        for (Integer neighbor : graph.get(cell)) {
            Integer neighborState = state.get(neighbor);
            if (neighborState != null && neighborState == choice) {
                return false;
            }
        }
        return true;
    }

    public Map<Integer, Integer> getPuzzle() {
        return puzzle;
    }

    /**
     * Validates a complete solution against all Sudoku rules:
     * - No duplicates in any row
     * - No duplicates in any column
     * - No duplicates in any section
     * - No empty cells
     * 
     * @param solution Completed puzzle solution to validate
     * @return true if solution is valid
     */
    public boolean isValidSolution(Map<Integer, Integer> solution) {
        // Check each row for validity
        for (int row = 0; row < gridSize; row++) {
            Set<Integer> seenRow = new HashSet<>();  // Track values seen in this row
            for (int col = 0; col < gridSize; col++) {
                int value = solution.get(row * gridSize + col);
                // Fail if cell is empty or value already seen in this row
                if (value == 0 || !seenRow.add(value)) {
                    return false;
                }
            }
        }
    
        // Check each column for validity
        for (int col = 0; col < gridSize; col++) {
            Set<Integer> seenCol = new HashSet<>();  // Track values seen in this column
            for (int row = 0; row < gridSize; row++) {
                int value = solution.get(row * gridSize + col);
                // Fail if cell is empty or value already seen in this column
                if (value == 0 || !seenCol.add(value)) {
                    return false;
                }
            }
        }
    
        // Check each section/box for validity
        for (int startRow = 0; startRow < gridSize; startRow += sectionSize) {
            for (int startCol = 0; startCol < gridSize; startCol += sectionSize) {
                Set<Integer> seenSubgrid = new HashSet<>();  // Track values seen in this section
                for (int i = 0; i < sectionSize; i++) {
                    for (int j = 0; j < sectionSize; j++) {
                        int value = solution.get((startRow + i) * gridSize + (startCol + j));
                        // Fail if cell is empty or value already seen in this section
                        if (value == 0 || !seenSubgrid.add(value)) {
                            return false;
                        }
                    }
                }
            }
        }
        // All checks passed
        return true;
    }
    

    /**
     * Prints the puzzle solution in a formatted grid.
     * - Empty cells as '.'
     * - Values 1-9 as digits
     * - Values 10+ as lowercase letters (a-g)
     * 
     * @param writer Output destination
     * @param solution Solution to print
     */
    public void printSolution(PrintWriter writer, Map<Integer, Integer> solution) {
        // Print each row of the solution
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int value = solution.get(row * gridSize + col);
                if (value == 0) {
                    writer.print(".");  // Empty cell
                } else if (value <= 9) {
                    writer.print(value);  // Single digit value
                } else {
                    // Convert values 10+ to letters (a=10, b=11, etc.)
                    writer.print((char) ('a' + value - 10));
                }
            }
            writer.println();  // End of row
        }
        writer.println();  // Blank line after puzzle
    }
    
}
