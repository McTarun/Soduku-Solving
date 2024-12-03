import java.io.PrintWriter;
import java.util.*;

/**
 * Represents a Sudoku puzzle as a graph data structure.
 * This class provides the core functionality for solving Sudoku puzzles
 * using graph-based algorithms like BFS and DLS.
 *
 * Based on the research paper:
 * "Comparison Analysis of Breadth First Search and Depth Limited Search Algorithms in Sudoku Game"
 * by Reza Bustami et al.
 */
public abstract class SudokuGraph {

    // Core data structure fields
    protected final int gridSize;        // Size of the grid (e.g., 4, 9, 16, 25)
    protected final int sectionSize;     // Size of each subsection (e.g., 2, 3, 4, 5)
    protected final Map<Integer, Set<Integer>> graph;  // Cell constraints
    protected int[] puzzleArray;         // Puzzle state as an array for efficiency
    protected final Set<Integer> validChoices;         // Valid values for cells

    /**
     * Initializes a new Sudoku puzzle of the specified size.
     *
     * @param gridSize The size of the grid (must be a perfect square)
     */
    public SudokuGraph(int gridSize) {
        this.gridSize = gridSize;
        this.sectionSize = (int) Math.sqrt(gridSize);
        this.graph = new HashMap<>();
        this.puzzleArray = new int[gridSize * gridSize];
        this.validChoices = new HashSet<>();
        for (int i = 1; i <= gridSize; i++) {
            validChoices.add(i);
        }
    }

    /**
     * Parses the input puzzle from a list of strings.
     * Supports grids larger than 9x9 by using letters for values above 9.
     *
     * @param lines List of strings representing puzzle rows
     * @throws IllegalArgumentException if row length is invalid
     */
    public void parseInput(List<String> lines) {
        // Ensure the number of lines matches the grid size
        if (lines.size() != gridSize) {
            throw new IllegalArgumentException("Invalid number of rows in puzzle input.");
        }

        // Iterate through each row of the puzzle grid
        for (int row = 0; row < gridSize; row++) {
            String line = lines.get(row).trim();
            if (line.length() != gridSize) {
                throw new IllegalArgumentException("Invalid row length at row " + (row + 1));
            }

            // Process each character (cell) in the current row
            for (int col = 0; col < gridSize; col++) {
                int cell = row * gridSize + col;
                char ch = line.charAt(col);
                if (ch == '.' || ch == '0') {
                    puzzleArray[cell] = 0;
                } else if (Character.isDigit(ch)) {
                    puzzleArray[cell] = ch - '0';
                } else if (Character.isLetter(ch)) {
                    // Convert letters to numbers: A=10, B=11, etc.
                    puzzleArray[cell] = Character.toUpperCase(ch) - 'A' + 10;
                } else {
                    throw new IllegalArgumentException("Invalid character at row " + (row + 1) + ", col " + (col + 1));
                }
            }
        }

        // Build the constraint graph
        buildGraph();
    }

    /**
     * Builds the graph representation of the Sudoku puzzle.
     * Creates edges between cells that cannot contain the same value.
     */
    private void buildGraph() {
        // Process each cell in the grid
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int cell = row * gridSize + col;
                graph.putIfAbsent(cell, new HashSet<>());

                // Add all cells in the same row
                for (int k = 0; k < gridSize; k++) {
                    if (k != col) {
                        graph.get(cell).add(row * gridSize + k);
                    }
                }

                // Add all cells in the same column
                for (int k = 0; k < gridSize; k++) {
                    if (k != row) {
                        graph.get(cell).add(k * gridSize + col);
                    }
                }

                // Add all cells in the same section
                int startRow = (row / sectionSize) * sectionSize;
                int startCol = (col / sectionSize) * sectionSize;
                for (int i = 0; i < sectionSize; i++) {
                    for (int j = 0; j < sectionSize; j++) {
                        int neighbor = (startRow + i) * gridSize + (startCol + j);
                        if (neighbor != cell) {
                            graph.get(cell).add(neighbor);
                        }
                    }
                }
            }
        }
    }

    /**
     * Abstract method to be implemented by concrete solver classes.
     *
     * @return List of all valid solutions found
     */
    public abstract List<int[]> solve();

    /**
     * Checks if the current puzzle state represents a complete solution.
     *
     * @param state Current puzzle state
     * @return true if all cells are filled with non-zero values
     */
    protected boolean isSolved(int[] state) {
        for (int value : state) {
            if (value == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the next empty cell in the puzzle.
     *
     * @param state Current puzzle state
     * @return Position of next empty cell, or -1 if none found
     */
    protected int getNextEmptyCell(int[] state) {
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Validates if a value can be placed in a cell without violating Sudoku rules.
     *
     * @param state Current puzzle state
     * @param cell  Cell position to check
     * @param choice Value to validate
     * @return true if the choice is valid for the given cell
     */
    protected boolean isValidChoice(int[] state, int cell, int choice) {
        for (Integer neighbor : graph.get(cell)) {
            int neighborValue = state[neighbor];
            if (neighborValue == choice) {
                return false;
            }
        }
        return true;
    }

    public int[] getPuzzle() {
        return puzzleArray;
    }

    /**
     * Validates a complete solution against all Sudoku rules.
     *
     * @param state Completed puzzle solution to validate
     * @return true if solution is valid
     */
    public boolean isValidSolution(int[] state) {
        // Check each row
        for (int row = 0; row < gridSize; row++) {
            Set<Integer> seenRow = new HashSet<>();
            for (int col = 0; col < gridSize; col++) {
                int value = state[row * gridSize + col];
                if (value == 0 || !seenRow.add(value)) {
                    return false;
                }
            }
        }

        // Check each column
        for (int col = 0; col < gridSize; col++) {
            Set<Integer> seenCol = new HashSet<>();
            for (int row = 0; row < gridSize; row++) {
                int value = state[row * gridSize + col];
                if (value == 0 || !seenCol.add(value)) {
                    return false;
                }
            }
        }

        // Check each section
        for (int startRow = 0; startRow < gridSize; startRow += sectionSize) {
            for (int startCol = 0; startCol < gridSize; startCol += sectionSize) {
                Set<Integer> seenSection = new HashSet<>();
                for (int i = 0; i < sectionSize; i++) {
                    for (int j = 0; j < sectionSize; j++) {
                        int value = state[(startRow + i) * gridSize + (startCol + j)];
                        if (value == 0 || !seenSection.add(value)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Prints the puzzle solution in a formatted grid.
     *
     * @param writer   Output destination
     * @param state    Solution to print
     */
    public void printSolution(PrintWriter writer, int[] state) {
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int value = state[row * gridSize + col];
                if (value == 0) {
                    writer.print(".");
                } else if (value <= 9) {
                    writer.print(value);
                } else {
                    writer.print((char) ('A' + value - 10));
                }
            }
            writer.println();
        }
        writer.println();
    }
}
