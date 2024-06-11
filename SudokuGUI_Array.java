import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SudokuGUI_Array {
    // Static variables to store the size of the Sudoku board, the 2D array of text fields, the 2D array of the board, and counters for operations and filled cells
    private static int SIZE;
    private static JTextField[][] cells;
    private static int[][] board;
    private static int operationCount;
    private static int inputSize;
    private static int filledCellsCount;
    private static int min_clues;
    private static Timer timer;
    private static long startTime;
    private static JLabel timerLabel;

    public static void main(String[] args) {
        Object[] options = {"3x3", "9x9"};
        int choice = JOptionPane.showOptionDialog(null,
                "Select the size of the Sudoku board:",
                "Sudoku Board Size",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        switch (choice) {
            case 0:
                SIZE = 3;
                min_clues = 4;
                break;
            case 1:
                SIZE = 9;
                min_clues = 17;
                break;
            default:
                return; // Exit if no option is chosen
        }

        inputSize = SIZE * SIZE; // Set input size based on board dimensions

        cells = new JTextField[SIZE][SIZE]; // Initialize the 2D array of text fields
        board = new int[SIZE][SIZE]; // Initialize the 2D array of the board

        // Create a JFrame for the GUI
        JFrame frame = new JFrame("Sudoku Solver Array");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        // Create a JPanel for the Sudoku grid
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(SIZE, SIZE));

        // Create and add text fields to the panel
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = new JTextField();
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                panel.add(cells[i][j]);
            }
        }

        //Timer label
        timerLabel = new JLabel("Elapsed time: 0 seconds");
        frame.add(timerLabel, BorderLayout.NORTH);

        // Create a "Solve" button
        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operationCount = 0; // Reset the operation counter
                filledCellsCount = 0; // Reset the filled cells counter
                parseInput(); // Parse the input from the text fields
                if (hasDuplicates()) {
                    JOptionPane.showMessageDialog(frame, "The board has duplicates in rows, columns, or subgrids and cannot be solved.");
                } else {
                    startTimer();
                    if (solve(0, 0, board)) { // Solve the Sudoku puzzle
                        updateBoard();
                        stopTimer();// Update the GUI with the solution
                        JOptionPane.showMessageDialog(frame, "Solution found!\nOperations performed: " + operationCount +
                                "\nInput size: " + inputSize + "\nFilled cells: " + filledCellsCount + "\nEmpty cells: " + (inputSize - filledCellsCount));
                    } else {
                        stopTimer();
                        JOptionPane.showMessageDialog(frame, "No solution exists!");
                    }
                }
            }
        });

        // Create a "Clear" button
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBoard(); // Clear the GUI and the board
                JOptionPane.showMessageDialog(frame, "Board cleared!");
            }
        });

        // Add the panel, "Solve" button, and "Clear" button to the frame
        frame.add(panel, BorderLayout.CENTER);
        frame.add(solveButton, BorderLayout.EAST);
        frame.add(clearButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void startTimer() {
        startTime = System.nanoTime();
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsedTime = getElapsedTime();
                timerLabel.setText("Elapsed time: " + elapsedTime + " nanoseconds");
            }
        });
        timer.start();
    }

    private static void stopTimer() {
        if (timer!= null) {
            timer.stop();
            long elapsedTime = getElapsedTime();
            timerLabel.setText("Elapsed time: " + elapsedTime + " nanoseconds");
        }
    }

    private static long getElapsedTime() {
        return (System.nanoTime() - startTime);
    }
    // Parse the input from the text fields and store it in the board array
    private static void parseInput() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String text = cells[i][j].getText();
                if (text.isEmpty()) {
                    board[i][j] = 0;
                } else {
                    try {
                        int value = Integer.parseInt(text);
                        if (value < 1 || value > SIZE) {
                            throw new NumberFormatException();
                        }
                        board[i][j] = value;
                        filledCellsCount++; // Increment the filled cells counter
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Please enter numbers between 1 and " + SIZE + " only.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        board[i][j] = 0;
                    }
                }
            }
        }
    }

    // Update the GUI with the solution
    private static void updateBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != 0) {
                    cells[i][j].setText(String.valueOf(board[i][j]));
                } else {
                    cells[i][j].setText("");
                }
            }
        }
    }

    // Clear the GUI and the board
    private static void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].setText("");
                board[i][j] = 0;
            }
        }
    }

    // Recursive function to solve the Sudoku puzzle (Array)
    private static boolean solve(int i, int j, int[][] cells) {
        if (filledCellsCount < min_clues) {
            JOptionPane.showMessageDialog(null, "Not enough clues to solve the puzzle.");
            return false;
        }
        if (i == SIZE) {
            i = 0;
            if (++j == SIZE) {
                return true; // If we've reached the end of the board, return true
            }
        }
        if (cells[i][j] != 0) {
            return solve(i + 1, j, cells); // If the current cell is already filled, move to the next one
        }

        // Try numbers from 1 to SIZE in the current cell
        for (int val = 1; val <= SIZE; ++val) { // O(n)
            if (legal(i, j, val, cells)) { // O(n^2)
                cells[i][j] = val; // O(1)
                operationCount++; // Increment the operation counter, O(1)
                if (solve(i + 1, j, cells)) { // O(n^2)
                    return true; // If the puzzle is solvable with the current value, return true T(n-1) -- recursive
                }
            }
        }
        cells[i][j] = 0;
        operationCount++; // Increment the operation counter
        return false; // If no value can be placed in the current cell, return false
    }

    // Check if a value can be placed in a cell
    private static boolean legal(int i, int j, int val, int[][] cells) {
        // Check the row
        for (int k = 0; k < SIZE; ++k) { // O(n)
            operationCount++; // O(1)
            if (val == cells[k][j]) { // O(n)
                return false; // If the value is already in the row, return false, O(1)
            }
        }

        // Check the column
        for (int k = 0; k < SIZE; ++k) { // O(n)
            operationCount++; // O(1)
            if (val == cells[i][k]) { // O(n)
                return false; // If the value is already in the column, return false, O(1)
            }
        }

        // Check the box
        int boxSize = (int) Math.sqrt(SIZE); // O(1)
        int boxRowOffset = (i / boxSize) * boxSize; // O(1)
        int boxColOffset = (j / boxSize) * boxSize; // O(1)
        for (int k = 0; k < boxSize; ++k) { // O(sqrt(n))
            for (int m = 0; m < boxSize; ++m) { // O(sqrt(n))
                operationCount++; // O(1)
                if (val == cells[boxRowOffset + k][boxColOffset + m]) { // O(n)
                    return false; // If the value is already in the box, return false, O(1)
                }
            }
        }

        return true; // If the value is not in the row, column, or box, return true, O(1)
    }

    private static boolean hasDuplicates() {
        // Check rows for duplicates
        for (int i = 0; i < SIZE; i++) {
            boolean[] seen = new boolean[SIZE + 1];
            for (int j = 0; j < SIZE; j++) {
                int value = board[i][j];
                if (value != 0) {
                    if (seen[value]) {
                        return true;
                    }
                    seen[value] = true;
                }
            }
        }

        // Check columns for duplicates
        for (int j = 0; j < SIZE; j++) {
            boolean[] seen = new boolean[SIZE + 1];
            for (int i = 0; i < SIZE; i++) {
                int value = board[i][j];
                if (value != 0) {
                    if (seen[value]) {
                        return true;
                    }
                    seen[value] = true;
                }
            }
        }

        // Check boxes for duplicates
        int boxSize = (int) Math.sqrt(SIZE);
        for (int startRow = 0; startRow < SIZE; startRow += boxSize) {
            for (int startCol = 0; startCol < SIZE; startCol += boxSize) {
                boolean[] seen = new boolean[SIZE + 1];
                for (int i = startRow; i < startRow + boxSize; i++) {
                    for (int j = startCol; j < startCol + boxSize; j++) {
                        int value = board[i][j];
                        if (value != 0) {
                            if (seen[value]) {
                                return true;
                            }
                            seen[value] = true;
                        }
                    }
                }
            }
        }

        return false;
    }
}