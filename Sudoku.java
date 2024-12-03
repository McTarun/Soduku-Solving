import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main Sudoku solver class that implements BFS (with iterative deepening) and DLS algorithms
 * using a graph data structure and supports variable grid sizes.
 *
 * Based on the research paper:
 * "Comparison Analysis of Breadth First Search and Depth Limited Search Algorithms in Sudoku Game"
 * by Reza Bustami et al.
 * Source: https://www.researchgate.net/publication/358642884
 *
 * Modifications:
 * - Implemented Iterative Deepening BFS (IDBFS) to manage memory usage on large grids.
 * - Applied heuristics to reduce branching factor in both BFS and DLS.
 * - Modified the program to generate puzzles based on user input and overwrite puzzle.txt.
 * - Included detailed comments explaining adjustments made and why.
 */
public class Sudoku {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        // Prompt the user for grid size and difficulty
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter grid size (n for an n x n grid, where n is a perfect square): ");
        int gridSize = scanner.nextInt();

        // Check if gridSize is a perfect square
        int sqrt = (int) Math.sqrt(gridSize);
        if (sqrt * sqrt != gridSize) {
            System.out.println("Error: Grid size must be a perfect square.");
            return;
        }

        System.out.print("Enter difficulty level (e = Easy, m = Medium, h = Hard, a = All): ");
        char difficulty = scanner.next().toLowerCase().charAt(0);
        scanner.close();

        // Generate puzzles based on user input
        List<PuzzleData> puzzles = new ArrayList<>();

        if (difficulty == 'e' || difficulty == 'a') {
            puzzles.add(generatePuzzle("Easy", gridSize, 0.40));
        }
        if (difficulty == 'm' || difficulty == 'a') {
            puzzles.add(generatePuzzle("Medium", gridSize, 0.37));
        }
        if (difficulty == 'h' || difficulty == 'a') {
            puzzles.add(generatePuzzle("Hard", gridSize, 0.33));
        }

        // Overwrite puzzle.txt with the generated puzzles
        writePuzzlesToFile("puzzle.txt", puzzles);

        // Solve each puzzle using BFS and DLS
        for (PuzzleData puzzleData : puzzles) {
            System.out.println("Solving Puzzle: " + puzzleData.title);

            // Initialize the BFS solver
            SudokuBFSSolver bfsSolver = new SudokuBFSSolver(puzzleData.gridSize);
            bfsSolver.parseInput(puzzleData.lines);

            // Initialize the DLS solver with depth limit based on empty cells
            int depthLimit = (int) Arrays.stream(bfsSolver.getPuzzle()).filter(v -> v == 0).count();
            SudokuDLSSolver dlsSolver = new SudokuDLSSolver(puzzleData.gridSize, depthLimit);
            dlsSolver.parseInput(puzzleData.lines);

            // Create a thread pool to handle parallel solving
            ExecutorService executor = Executors.newFixedThreadPool(2);

            // BFS solving task
            Callable<Void> bfsTask = () -> {
                System.out.println("Solving using BFS (Iterative Deepening)...");
                long startTime = System.nanoTime();
                List<int[]> bfsSolutions = bfsSolver.solve();
                long endTime = System.nanoTime();
                double duration = (endTime - startTime) / 1e9; // Convert to seconds
                int validSolution = bfsSolutions.size();

                // Write valid solutions to an output file
                String bfsOutputFile = "bfs_solutions_" + puzzleData.title.replaceAll("\\s+", "_") + ".txt";
                try (PrintWriter writer = new PrintWriter(new FileOutputStream(bfsOutputFile))) {
                    writer.println("Solutions via BFS for " + puzzleData.title + ":");
                    for (int[] solution : bfsSolutions) {
                        bfsSolver.printSolution(writer, solution);
                    }
                    writer.println("Execution Time: " + duration + " seconds");
                    writer.println("Nodes Expanded: " + bfsSolver.getNodesExpanded());
                }
                System.out.println("Solutions found via BFS: " + validSolution);
                System.out.println("BFS Execution Time: " + duration + " seconds");
                System.out.println("BFS Nodes Expanded: " + bfsSolver.getNodesExpanded());
                return null;
            };

            // DLS solving task
            Callable<Void> dlsTask = () -> {
                System.out.println("Solving using DLS...");
                long startTime = System.nanoTime();
                List<int[]> dlsSolutions = dlsSolver.solve();
                long endTime = System.nanoTime();
                double duration = (endTime - startTime) / 1e9; // Convert to seconds
                int validSolution = dlsSolutions.size();

                // Write valid solutions to an output file
                String dlsOutputFile = "dls_solutions_" + puzzleData.title.replaceAll("\\s+", "_") + ".txt";
                try (PrintWriter writer = new PrintWriter(new FileOutputStream(dlsOutputFile))) {
                    writer.println("Solutions via DLS for " + puzzleData.title + ":");
                    for (int[] solution : dlsSolutions) {
                        dlsSolver.printSolution(writer, solution);
                    }
                    writer.println("Execution Time: " + duration + " seconds");
                    writer.println("Nodes Expanded: " + dlsSolver.getNodesExpanded());
                }
                System.out.println("Solutions found via DLS: " + validSolution);
                System.out.println("DLS Execution Time: " + duration + " seconds");
                System.out.println("DLS Nodes Expanded: " + dlsSolver.getNodesExpanded());
                return null;
            };

            // Submit tasks to executor
            Future<Void> bfsFuture = executor.submit(bfsTask);
            Future<Void> dlsFuture = executor.submit(dlsTask);

            // Wait for completion
            try {
                bfsFuture.get();
                dlsFuture.get();
            } catch (ExecutionException e) {
                handleExecutionException(e);
            }

            executor.shutdown();
            System.out.println("Finished solving " + puzzleData.title);
            System.out.println("-----------------------------------------");
        }
    }

    /**
     * Generates a Sudoku puzzle with the given difficulty and grid size.
     *
     * @param difficultyName The name of the difficulty level.
     * @param gridSize       The size of the grid (n x n).
     * @param hintPercentage The percentage of cells to be filled as hints.
     * @return A PuzzleData object containing the generated puzzle.
     */
    private static PuzzleData generatePuzzle(String difficultyName, int gridSize, double hintPercentage) {
        int totalCells = gridSize * gridSize;
        int hints = (int) Math.floor(hintPercentage * totalCells);

        // Generate a complete Sudoku puzzle
        SudokuGenerator generator = new SudokuGenerator(gridSize, totalCells - hints);
        generator.fillValues();

        // Get the puzzle as a list of strings
        List<String> puzzleLines = generator.getPuzzleAsStringList();

        // Create PuzzleData object
        PuzzleData puzzleData = new PuzzleData();
        puzzleData.title = difficultyName + " " + gridSize + "x" + gridSize + " Puzzle";
        puzzleData.gridSize = gridSize;
        puzzleData.lines = puzzleLines;

        return puzzleData;
    }

    /**
     * Writes the list of puzzles to the specified file.
     *
     * @param fileName The name of the file to write to.
     * @param puzzles  The list of puzzles to write.
     * @throws IOException If an I/O error occurs.
     */
    private static void writePuzzlesToFile(String fileName, List<PuzzleData> puzzles) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {
            for (PuzzleData puzzle : puzzles) {
                writer.println("# " + puzzle.title);
                writer.println(puzzle.gridSize + "x" + puzzle.gridSize);
                for (String line : puzzle.lines) {
                    writer.println(line);
                }
                writer.println();
            }
        }
    }

    /**
     * Helper class to store puzzle data.
     */
    private static class PuzzleData {
        String title;
        int gridSize;
        List<String> lines = new ArrayList<>();
    }

    /**
     * Handles exceptions during task execution.
     *
     * @param e The ExecutionException to handle.
     */
    private static void handleExecutionException(ExecutionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof OutOfMemoryError) {
            System.err.println("Solver ran out of memory: " + cause.getMessage());
        } else {
            cause.printStackTrace();
        }
    }
}
