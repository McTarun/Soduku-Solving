import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main Sudoku solver class that implements a multithreaded solution using BFS and DLS approaches.
 * The solver can handle variable-sized Sudoku grids as long as they are perfect squares (e.g., 4x4, 9x9, 16x16).
 */
public class Sudoku{

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        // Input and output streams
        String fileName="puzzle.txt";
        if (args.length >= 1) {
            fileName=args[0];
        }    
        FileInputStream file_in = new FileInputStream(fileName);
        Scanner scanner = new Scanner(file_in);

        // Create a list to store each line of the puzzle input
        // This allows us to process the entire puzzle at once after reading
        List<String> lines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        
        // Determine the size of the Sudoku grid from the input
        // For a 9x9 puzzle, gridSize would be 9
        int gridSize = inferGridSize(lines);

        // Ensure the grid size is valid (must be a perfect square)
        // Valid sizes are 4x4 (2²), 9x9 (3²), 16x16 (4²), etc.
        if (!isValidGridSize(gridSize)) {
            System.out.println("Error: Invalid grid size. Only square grids with sizes that are perfect squares are supported.");
            return;
        }

        // Initialize the solver with the appropriate grid size
        // SudokuBFSSolver implements the BFS (Breadth-First Search) strategy
        SudokuGraph graph = new SudokuBFSSolver(gridSize);
        graph.parseInput(lines);

        // Create a thread pool with 2 threads to handle parallel solving
        // This allows BFS and DLS to run concurrently (though DLS implementation is currently commented out)
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Define the BFS solving task
        Callable<Void> bfsTask = () -> {
            System.out.println("Solving using BFS...");
            // Solve returns all possible solutions found
            List<Map<Integer, Integer>> bfsSolutions = graph.solve();
            int validSolution = 0;

            // Write valid solutions to an output file
            // Solutions are validated before being written to ensure correctness
            try (PrintWriter writer = new PrintWriter(new FileOutputStream("bfs_solutions.txt"))) {
                writer.println("Solutions via BFS:");
                for (Map<Integer, Integer> solution : bfsSolutions) {
                    if (graph.isValidSolution(solution)) {
                        validSolution++;
                        graph.printSolution(writer, solution);
                    }
                }
            }
            System.out.println("Solutions found via BFS: " + validSolution);
            return null;
        };

        // Submit tasks to executor
        Future<Void> bfsFuture = executor.submit(bfsTask);
  
        // Wait for completion
        bfsFuture.get();

        executor.shutdown();
        scanner.close();
    }

    /**
     * Determines the size of the Sudoku grid based on input dimensions.
     * Assumes the grid is square (same number of rows and columns).
     * @param lines List of input lines representing the puzzle
     * @return The size of one dimension of the grid
     */
    private static int inferGridSize(List<String> lines) {
        return lines.size();
    }

    /**
     * Validates that the grid size is a perfect square.
     * @param gridSize The size to validate
     * @return true if the size is valid (perfect square), false otherwise
     */
    private static boolean isValidGridSize(int gridSize) {
        int sqrt = (int) Math.sqrt(gridSize);
        return sqrt * sqrt == gridSize;
    }

    /**
     * Calculates the maximum depth for DLS (Depth-Limited Search) based on empty cells.
     * This method appears to be unused in the current implementation but could be
     * useful for implementing the DLS solving strategy.
     * @param graph The Sudoku puzzle graph
     * @return The number of empty cells in the puzzle
     */
    private static int calculateDepthLimit(SudokuGraph graph) {
        return (int) graph.getPuzzle().values().stream().filter(v -> v == 0).count();
    }
}
