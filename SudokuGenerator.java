import java.util.*;

/**
 * Sudoku puzzle generator supporting variable grid sizes.
 * Generates puzzles of sizes 4x4, 9x9, 16x16, and 25x25,
 * and allows specifying the number of cells to remove for difficulty levels.
 *
 * This code is based on an example from GeeksforGeeks and modified by [Your Name].
 *
 * Original Source:
 * https://www.geeksforgeeks.org/program-sudoku-generator/
 *
 * Modifications:
 * - Added method to get the puzzle as a list of strings for writing to a file.
 * - Adjusted the removeKDigits method to ensure the correct number of hints.
 */
public class SudokuGenerator {

    int[][] mat;
    int N; // number of columns/rows
    int SRN; // square root of N
    int K; // No. of missing digits

    // Constructor
    public SudokuGenerator(int N, int K) {
        this.N = N;
        this.K = K;

        // Compute square root of N
        Double SRNd = Math.sqrt(N);
        SRN = SRNd.intValue();

        mat = new int[N][N];
    }

    // Sudoku Generator
    public void fillValues() {
        // Fill the diagonal of SRN x SRN matrices
        fillDiagonal();

        // Fill remaining blocks
        fillRemaining(0, SRN);

        // Remove Randomly K digits to make game
        removeKDigits();
    }

    // Fill the diagonal SRN number of SRN x SRN matrices
    void fillDiagonal() {
        for (int i = 0; i < N; i = i + SRN) {
            // for diagonal box, start coordinates->i==j
            fillBox(i, i);
        }
    }

    // Returns false if given block contains num.
    boolean unUsedInBox(int rowStart, int colStart, int num) {
        for (int i = 0; i < SRN; i++)
            for (int j = 0; j < SRN; j++)
                if (mat[rowStart + i][colStart + j] == num)
                    return false;

        return true;
    }

    // Fill a SRN x SRN matrix.
    void fillBox(int row, int col) {
        int num;
        for (int i = 0; i < SRN; i++) {
            for (int j = 0; j < SRN; j++) {
                do {
                    num = randomGenerator(N);
                } while (!unUsedInBox(row, col, num));

                mat[row + i][col + j] = num;
            }
        }
    }

    // Random generator
    int randomGenerator(int num) {
        return (int) Math.floor((Math.random() * num + 1));
    }

    // Check if safe to put in cell
    boolean CheckIfSafe(int i, int j, int num) {
        return (unUsedInRow(i, num) &&
                unUsedInCol(j, num) &&
                unUsedInBox(i - i % SRN, j - j % SRN, num));
    }

    // check in the row for existence
    boolean unUsedInRow(int i, int num) {
        for (int j = 0; j < N; j++)
            if (mat[i][j] == num)
                return false;
        return true;
    }

    // check in the column for existence
    boolean unUsedInCol(int j, int num) {
        for (int i = 0; i < N; i++)
            if (mat[i][j] == num)
                return false;
        return true;
    }

    // A recursive function to fill remaining matrix
    boolean fillRemaining(int i, int j) {
        if (j >= N && i < N - 1) {
            i = i + 1;
            j = 0;
        }
        if (i >= N && j >= N)
            return true;

        if (i < SRN) {
            if (j < SRN)
                j = SRN;
        } else if (i < N - SRN) {
            if (j == (i / SRN) * SRN)
                j = j + SRN;
        } else {
            if (j == N - SRN) {
                i = i + 1;
                j = 0;
                if (i >= N)
                    return true;
            }
        }

        for (int num = 1; num <= N; num++) {
            if (CheckIfSafe(i, j, num)) {
                mat[i][j] = num;
                if (fillRemaining(i, j + 1))
                    return true;

                mat[i][j] = 0;
            }
        }
        return false;
    }

    // Remove the K no. of digits to complete game
    public void removeKDigits() {
        int count = K;
        while (count != 0) {
            int cellId = randomGenerator(N * N) - 1;

            // extract coordinates i and j
            int i = (cellId / N);
            int j = cellId % N;

            if (mat[i][j] != 0) {
                count--;
                mat[i][j] = 0;
            }
        }
    }

    // Get the puzzle as a list of strings
    public List<String> getPuzzleAsStringList() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < N; j++) {
                if (mat[i][j] == 0) {
                    line.append(".");
                } else if (mat[i][j] <= 9) {
                    line.append(mat[i][j]);
                } else {
                    // Convert numbers >9 to letters
                    line.append((char) ('A' + mat[i][j] - 10));
                }
            }
            lines.add(line.toString());
        }
        return lines;
    }

    // Print sudoku
    public void printSudoku() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (mat[i][j] == 0) {
                    System.out.print(".");
                } else if (mat[i][j] <= 9) {
                    System.out.print(mat[i][j]);
                } else {
                    // Convert numbers >9 to letters
                    System.out.print((char) ('A' + mat[i][j] - 10));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // This main method can be used for testing the generator separately
        int N = 9; // Change this value for different grid sizes
        int totalCells = N * N;
        double hintPercentage = 0.37; // For medium difficulty
        int K = totalCells - (int) Math.floor(hintPercentage * totalCells);

        SudokuGenerator sudoku = new SudokuGenerator(N, K);
        sudoku.fillValues();
        sudoku.printSudoku();
    }
}
