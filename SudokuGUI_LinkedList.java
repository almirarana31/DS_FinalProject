import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

// linked list implementation
public class SudokuGUI_LinkedList {
    // Static variables to store the size of the Sudoku board, the linked list of text fields, the linked list of the board, and counters for operations and filled cells
    private static int SIZE;
    private static LinkedList<JTextField> cells;
    private static LinkedList<Integer> board;
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

        switch (choice) { // allow user to choose the size of board
            case 0:
                SIZE = 3; // 3x3 board
                min_clues = 4; // minimum clues will be set to 4
                break;
            case 1:
                SIZE = 9; // 9x9 board
                min_clues = 17; // minimum clues will be set to 17
                break;
            default:
                return; // Exit if no option is chosen
        }

        inputSize = SIZE * SIZE; // set input size based on board dimension

        cells = new LinkedList<>(); // initialize linkedlist of cells
        board = new LinkedList<>(); // initialize linkedlist of board

        for (int i = 0; i < SIZE * SIZE; i++) { // initialize board with 0s
            board.add(0);
        }
        //create jframe for the GUI
        JFrame frame = new JFrame("Sudoku Solver LinkedList");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        // create a jpanel for the sudoku grid
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(SIZE, SIZE));

        //create and add text fields to the panel
        for (int i = 0; i < SIZE * SIZE; i++) {
            JTextField cell = new JTextField();
            cell.setHorizontalAlignment(JTextField.CENTER);
            cells.add(cell);
            panel.add(cell);
        }

        // Timer label
        timerLabel = new JLabel("Elapsed time: 0 seconds");
        frame.add(timerLabel, BorderLayout.NORTH);

        // create a solve button
        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operationCount = 0; // Reset the operation counter
                filledCellsCount = 0; // Reset the filled cells counter
                parseInput(); // Parse the input from the text fields
                if (hasDuplicates()) { // if board has duplicates, show error message
                    JOptionPane.showMessageDialog(null, "The board has duplicates in rows, columns, or subgrids and cannot be solved.");
                } else { // otherwise, start timer and solver
                    startTimer();
                    if (solve(0)) {
                        updateBoard();
                        stopTimer();
                        // Update the GUI with the solution
                        JOptionPane.showMessageDialog(frame, "Solution found!\nOperations performed: " + operationCount +
                                "\nInput size: " + inputSize + "\nFilled cells: " + filledCellsCount + "\nEmpty cells: " + (inputSize - filledCellsCount));
                    } else { // if solver does not find solution
                        stopTimer();
                        JOptionPane.showMessageDialog(null, "No solution exists.");
                    }
                }
            }
        });

        //create a clear button to clear board of numbers
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

    // timer function to count in nanoseconds
    private static void startTimer() {
        startTime = System.nanoTime();
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsedTime = getElapsedTime();
                timerLabel.setText("Elapsed time: " + elapsedTime / 1_000_000_000 + " seconds");
            }
        });
        timer.start();
    }

    // function to stop timer and return elapsed time in nanoseconds
    private static void stopTimer() {
        if (timer != null) {
            timer.stop();
            long elapsedTime = getElapsedTime();
            timerLabel.setText("Elapsed time: " + elapsedTime + " nanoseconds");
        }
    }

    private static long getElapsedTime() {
        return (System.nanoTime() - startTime);
    }

    // Parse the input from the text fields and store it in the board
    private static void parseInput() {
        // loop through each row and column
        for (int i = 0; i < SIZE * SIZE; i++) {
            //get text from current cell
            String text = cells.get(i).getText();
            if (text.isEmpty()) { // if cell is empty, insert 0
                board.set(i, 0);
            } else {
                try {
                    // parse text into integer
                    int value = Integer.parseInt(text);
                    // check if value is within legal range
                    if (value < 1 || value > SIZE) {
                        throw new NumberFormatException();
                    }
                    // if value is legal, store into board
                    board.set(i, value);
                    filledCellsCount++; // Increment the filled cells counter
                } catch (NumberFormatException e) { // error handling message if input is invalid
                    JOptionPane.showMessageDialog(null, "Please enter numbers between 1 and " + SIZE + " only.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    // if input is invalid, set board value to 0
                    board.set(i, 0);
                }
            }
        }
    }
    // Update the GUI with the solution
    private static void updateBoard() {
        for (int i = 0; i < SIZE * SIZE; i++) {
            int value = board.get(i);
            if (value != 0) {
                cells.get(i).setText(String.valueOf(value));
            } else {
                cells.get(i).setText("");
            }
        }
    }
    // Clear the GUI and the board
    private static void clearBoard() {
        for (int i = 0; i < SIZE * SIZE; i++) {
            cells.get(i).setText("");
            board.set(i, 0);
        }
    }

    // Recursive function to solve the puzzle (LinkedList)
    private static boolean solve(int i) {
        if (filledCellsCount < min_clues) { // check if enough clues within board
            JOptionPane.showMessageDialog(null, "Not enough clues to solve the puzzle.");
            return false;
        }
        if (i == SIZE * SIZE) { // if end of board reached, stop
            return true;
        }
        if (board.get(i) != 0) {
            return solve(i + 1); // move to the next cell
        }

        for (int val = 1; val <= SIZE; ++val) { // O(n)
            if (legal(i, val)) { // O(n^2)
                board.set(i, val); // O(1)
                operationCount++; // O(1)
                if (solve(i + 1)) { // O(n^2)
                    return true; // If the puzzle is solvable with the current value, return true T(n-1) -- recursive
                }
            }
        }
        board.set(i, 0);
        operationCount++; // increment op count
        return false; // return false if no value can be placed in the current cell
    }


    private static boolean legal(int i, int val) {
        // Check if a value can be placed in a cell
        int row = i / SIZE; // Calculate the row index of the cell
        int col = i % SIZE; // Calculate the column index of the cell

        // Check if 'val' already exists in the current row
        for (int k = 0; k < SIZE; ++k) {
            operationCount++; // Increment operation count
            if (val == board.get(row * SIZE + k)) {
                return false; // 'val' is already in the row
            }
        }

        // Check if 'val' already exists in the current column
        for (int k = 0; k < SIZE; ++k) {
            operationCount++; // Increment operation count
            if (val == board.get(k * SIZE + col)) {
                return false; // 'val' is already in the column
            }
        }

        // Calculate the size of the subgrid (for a 9x9 board, it's 3x3)
        int boxSize = (int) Math.sqrt(SIZE);
        int boxRowOffset = (row / boxSize) * boxSize; // Calculate the starting row index of the subgrid
        int boxColOffset = (col / boxSize) * boxSize; // Calculate the starting column index of the subgrid

        // Check if 'val' already exists in the current subgrid
        for (int k = 0; k < boxSize; ++k) {
            for (int m = 0; m < boxSize; ++m) {
                operationCount++; // Increment operation count
                if (val == board.get((boxRowOffset + k) * SIZE + (boxColOffset + m))) {
                    return false; // 'val' is already in the subgrid
                }
            }
        }

        // 'val' can be legally placed in the cell
        return true;
    }



    private static boolean hasDuplicates() {
        // Check rows for duplicates
        for (int i = 0; i < SIZE; i++) {
            LinkedList<Integer> seen = new LinkedList<>();
            for (int j = 0; j < SIZE; j++) {
                int value = board.get(i * SIZE + j);
                if (value != 0) {
                    if (seen.contains(value)) {
                        return true;
                    }
                    seen.add(value);
                }
            }
        }

        // Check columns for duplicates
        for (int j = 0; j < SIZE; j++) {
            LinkedList<Integer> seen = new LinkedList<>();
            for (int i = 0; i < SIZE; i++) {
                int value = board.get(i * SIZE + j);
                if (value != 0) {
                    if (seen.contains(value)) {
                        return true;
                    }
                    seen.add(value);
                }
            }
        }

        // Check subgrids for duplicates
        int boxSize = (int) Math.sqrt(SIZE);
        for (int startRow = 0; startRow < SIZE; startRow += boxSize) {
            for (int startCol = 0; startCol < SIZE; startCol += boxSize) {
                LinkedList<Integer> seen = new LinkedList<>();
                for (int i = 0; i < boxSize; i++) {
                    for (int j = 0; j < boxSize; j++) {
                        int value = board.get((startRow + i) * SIZE + (startCol + j));
                        if (value != 0) {
                            if (seen.contains(value)) {
                                return true;
                            }
                            seen.add(value);
                        }
                    }
                }
            }
        }

        return false;
    }
}
