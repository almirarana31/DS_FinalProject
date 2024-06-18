import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class SudokuGUI_HashMap {
    private static int SIZE;
    private static HashMap<Integer, JTextField> cells;
    private static HashMap<String, Integer> board;
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

        switch (choice) { // allow user to choose size of board
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

        inputSize = SIZE * SIZE; // Set input size based on board dimensions

        cells = new HashMap<>(); // initialize text fields
        board = new HashMap<>(); // initialize board

        // Initialize the board with zeros
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board.put(i + "," + j, 0);
            }
        }

        // Create a JFrame for the GUI
        JFrame frame = new JFrame("Sudoku Solver HashMap");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        // Create a JPanel for the Sudoku grid
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(SIZE, SIZE));

        // create and add text fields to the panel
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cells.put(i * SIZE + j, cell);
                panel.add(cell);
            }
        }

        //Timer label
        timerLabel = new JLabel("Elapsed time: 0 nanoseconds");
        frame.add(timerLabel, BorderLayout.NORTH);

        // create a solve button
        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operationCount = 0; // reset the operation counter
                filledCellsCount = 0; // reset the filled cells counter
                parseInput(); // parse the input from the text fields
                if (hasDuplicates()) { // if board has duplicates, show error message
                    JOptionPane.showMessageDialog(frame, "The board has duplicates in rows, columns, or subgrids and cannot be solved.");
                } else { // otherwise, start timer and solver
                    startTimer();
                    if (solve(0, 0)) { // solve the sudoku puzzle
                        updateBoard();
                        stopTimer();
                        // update the gui with solution
                        JOptionPane.showMessageDialog(frame, "Solution found!\nOperations performed: " + operationCount +
                                "\nInput size: " + inputSize + "\nFilled cells: " + filledCellsCount + "\nEmpty cells: " + (inputSize - filledCellsCount));
                    } else { // if solver does not find solution
                        stopTimer();
                        JOptionPane.showMessageDialog(frame, "No solution exists!");
                    }
                }
            }
        });

        //create a clear button to clear board of numbers
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBoard(); // clear gui and the board
                JOptionPane.showMessageDialog(frame, "Board cleared!");
            }
        });

        // add the panel, solve button, and clear button at specified locations
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
                timerLabel.setText("Elapsed time: " + elapsedTime + " nanoseconds");
            }
        });
        timer.start();
    }
    // function to stop timer and return elapsed time in nanoseconds
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

    // parse the input from the text fields and store it in the board
    private static void parseInput() {
        // loop through each row
        for (int row = 0; row < SIZE; row++) {
            // loop through each column
            for (int col = 0; col < SIZE; col++) {
                JTextField cell = cells.get(row * SIZE + col);
                // get text from current cell
                String text = cell.getText();
                if (text.isEmpty()) {
                    // if cell empty input 0
                    board.put(row + "," + col, 0);
                } else {
                    try {
                        // parse text into integer
                        int value = Integer.parseInt(text);
                        // check if value within legal range
                        if (value < 1 || value > SIZE) {
                            throw new NumberFormatException();
                        }
                        // if value legal store into board
                        board.put(row + "," + col, value);
                        filledCellsCount++; // increment the filled cells counter
                    } catch (NumberFormatException e) { // error handling message for illegal input
                        JOptionPane.showMessageDialog(null, "Please enter numbers between 1 and " + SIZE + " only.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        // if input invalid, set board to 0
                        board.put(row + "," + col, 0);
                    }
                }
            }
        }
    }
    // update gui with solution
    private static void updateBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int value = board.get(i + "," + j);
                JTextField cell = cells.get(i * SIZE + j);
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                } else {
                    cell.setText("");
                }
            }
        }
    }

    // clear gui and the board
    private static void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JTextField cell = cells.get(i * SIZE + j);
                cell.setText("");
                board.put(i + "," + j, 0);
            }
        }
    }

    // recursive function to solve the puzzle (Hashmap)
    private static boolean solve(int row, int col) {
        if (filledCellsCount < min_clues) { //check if enough clues in the table beforehand
            JOptionPane.showMessageDialog(null, "Not enough clues to solve the puzzle.");
            return false;
        }

        if (row == SIZE) {
            row = 0;
            if (++col == SIZE) {
                return true; // if end of the board reached, stop
            }
        }

        if (board.get(row + "," + col) != 0) {
            return solve(row + 1, col); //if current cell filled, move on
        }

        // try numbers from 1 to SIZE in current cell
        for (int val = 1; val <= SIZE; ++val) { // O(n)
            if (legal(row, col, val)) { // O(n^2)
                board.put(row + "," + col, val); // O(1)
                operationCount++; // O(1)
                if (solve(row + 1, col)) { // O(n^2)
                    return true; // If the puzzle is solvable with the current value, return true T(n-1) -- recursive
                }
            }
        }

        board.put(row + "," + col, 0);
        operationCount++; // increment operation counter
        return false; // if no value can be placed then return false
    }

    // Check if a value can be placed in a cell
    private static boolean legal(int row, int col, int val) {
        for (int k = 0; k < SIZE; ++k) { // O(n)
            operationCount++; // O(1)
            if (val == board.get(row + "," + k)) { // O(n^2)
                return false; // O(1)
            }
        }

        for (int k = 0; k < SIZE; ++k) { // O(n)
            operationCount++; // O(1)
            if (val == board.get(k + "," + col)) { // O(n^2)
                return false; // O(1)
            }
        }

        int boxSize = (int) Math.sqrt(SIZE); // O(1)
        int boxRowOffset = (row / boxSize) * boxSize; // O(1)
        int boxColOffset = (col / boxSize) * boxSize; // O(1)
        for (int k = 0; k < boxSize; ++k) { // O(sqrt(n))
            for (int m = 0; m < boxSize; ++m) { // O(sqrt(n))
                operationCount++; // O(1)
                if (val == board.get((boxRowOffset + k) + "," + (boxColOffset + m))) { // O(n^2)
                    return false; // O(1)
                }
            }
        }

        return true; // O(1)
    }

    private static boolean hasDuplicates() {
        // check rows for duplicates
        for (int i = 0; i < SIZE; i++) {
            boolean[] seen = new boolean[SIZE + 1];
            for (int j = 0; j < SIZE; j++) {
                int value = board.get(i + "," + j);
                if (value != 0) {
                    if (seen[value]) {
                        return true;
                    }
                    seen[value] = true;
                }
            }
        }

        // check columns for duplicates
        for (int j = 0; j < SIZE; j++) {
            boolean[] seen = new boolean[SIZE + 1];
            for (int i = 0; i < SIZE; i++) {
                int value = board.get(i + "," + j);
                if (value != 0) {
                    if (seen[value]) {
                        return true;
                    }
                    seen[value] = true;
                }
            }
        }
        // check subgrids for duplicates
        int boxSize = (int) Math.sqrt(SIZE);
        for (int startRow = 0; startRow < SIZE; startRow += boxSize) {
            for (int startCol = 0; startCol < SIZE ;startCol += boxSize) {
                boolean[] seen = new boolean[SIZE + 1];
                for (int i = startRow; i < startRow + boxSize; i++) {
                    for (int j = startCol; j < startCol + boxSize; j++) {
                        int value = board.get(i + "," + j);
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
