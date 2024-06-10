import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class SudokuGUI_LinkedList {
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

        inputSize = SIZE * SIZE;
        cells = new LinkedList<>();
        board = new LinkedList<>();

        for (int i = 0; i < SIZE * SIZE; i++) {
            board.add(0);
        }

        JFrame frame = new JFrame("Sudoku Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(SIZE, SIZE));

        for (int i = 0; i < SIZE * SIZE; i++) {
            JTextField cell = new JTextField();
            cell.setHorizontalAlignment(JTextField.CENTER);
            cells.add(cell);
            panel.add(cell);
        }

        //Timer label
        timerLabel = new JLabel("Elapsed time: 0 seconds");
        frame.add(timerLabel, BorderLayout.NORTH);

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operationCount = 0;
                filledCellsCount = 0;
                parseInput();
                if (hasDuplicates()) {
                    JOptionPane.showMessageDialog(null, "The board has duplicates in rows, columns, or subgrids and cannot be solved.");
                } else {
                    startTimer();
                    if (solve(0, board)) {
                        updateBoard();
                        stopTimer();
                        JOptionPane.showMessageDialog(frame, "Solution found!\nOperations performed: " + operationCount +
                                "\nInput size: " + inputSize + "\nFilled cells: " + filledCellsCount + "\nEmpty cells: " + (inputSize - filledCellsCount));
                    } else {
                        stopTimer();
                        JOptionPane.showMessageDialog(null, "No solution exists.");
                    }
                }
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBoard();
                JOptionPane.showMessageDialog(frame, "Board cleared!");
            }
        });

        frame.add(panel, BorderLayout.CENTER);
        frame.add(solveButton, BorderLayout.EAST);
        frame.add(clearButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsedTime = getElapsedTime();
                timerLabel.setText("Elapsed time: " + elapsedTime + " milliseconds");
            }
        });
        timer.start();
    }

    private static void stopTimer() {
        if (timer != null) {
            timer.stop();
            long elapsedTime = getElapsedTime();
            timerLabel.setText("Elapsed time: " + elapsedTime + " milliseconds");
        }
    }
    private static long getElapsedTime() {
        return (System.currentTimeMillis() - startTime);
    }
    // Parse the input from the text fields and store it in the board array

    private static void parseInput() {
        for (int i = 0; i < SIZE * SIZE; i++) {
            String text = cells.get(i).getText();
            if (text.isEmpty()) {
                board.set(i, 0);
            } else {
                try {
                    int value = Integer.parseInt(text);
                    if (value < 1 || value > SIZE) {
                        throw new NumberFormatException();
                    }
                    board.add(i, value);
                    filledCellsCount++;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Please enter numbers between 1 and " + SIZE + " only.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    board.add(i, 0);
                }
            }
        }
    }

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

    private static void clearBoard() {
        for (int i = 0; i < SIZE; i++) {
            cells.get(i).setText("");
            board.set(i, 0);
        }
    }

    //recursive function to solve the puzzle (LinkedList)
    private static boolean solve(int i, LinkedList<Integer> board) {
        if (filledCellsCount < min_clues) { // check if enough clues within board
            JOptionPane.showMessageDialog(null, "Not enough clues to solve the puzzle.");
            return false;
        }
        if (i == SIZE * SIZE) { // if end of board reached, stop
            return true;
        }
        if (board.get(i) != 0) {
            return solve(i + 1, board); // if end of board reached, stop
        }

        for (int val = 1; val <= SIZE; ++val) { // O(n)
            if (legal(i, val, board)) { // O(n^2)
                board.set(i, val); // O(1)
                operationCount++; // O(1)
                if (solve(i + 1, board)) { // O(n^2)
                    return true; // O(1)
                }
            }
        }
        board.set(i, 0);
        operationCount++; // increment op count
        return false; // return false if no value can be placed in the current cell
    }

    private static boolean legal(int i, int val, LinkedList<Integer> board) {
        int row = i / SIZE;
        int col = i % SIZE;

        for (int k = 0; k < SIZE; ++k) {
            if (val == board.get(row * SIZE + k)) {
                return false;
            }
        }

        for (int k = 0; k < SIZE; ++k) {
            if (val == board.get(k * SIZE + col)) {
                return false;
            }
        }

        int boxSize = (int) Math.sqrt(SIZE);
        int boxRowOffset = (row / boxSize) * boxSize;
        int boxColOffset = (col / boxSize) * boxSize;
        for (int k = 0; k < boxSize; ++k) {
            for (int m = 0; m < boxSize; ++m) {
                if (val == board.get(boxRowOffset + k) * SIZE + (boxColOffset + m)) {
                    return false;
                }
            }
        }
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
                boolean[] seen = new boolean[SIZE + 1];
                for (int i = startRow; i < startRow + boxSize; i++) {
                    for (int j = startCol; j < startCol + boxSize; j++) {
                        int value = board.get(i * SIZE + j);
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
