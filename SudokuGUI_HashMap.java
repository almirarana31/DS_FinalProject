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
                return;
        }

        inputSize = SIZE * SIZE;

        cells = new HashMap<>();
        board = new HashMap<>();

        // Initialize the board with zeros
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board.put(i + "," + j, 0);
            }
        }

        JFrame frame = new JFrame("Sudoku Solver HashMap");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(SIZE, SIZE));

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JTextField cell = new JTextField();
                cell.setHorizontalAlignment(JTextField.CENTER);
                cells.put(i * SIZE + j, cell);
                panel.add(cell);
            }
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
                    JOptionPane.showMessageDialog(frame, "The board has duplicates in rows, columns, or subgrids and cannot be solved.");
                } else {
                    startTimer();
                    if (solve(0, 0)) {
                        updateBoard();
                        stopTimer();
                        JOptionPane.showMessageDialog(frame, "Solution found!\nOperations performed: " + operationCount +
                                "\nInput size: " + inputSize + "\nFilled cells: " + filledCellsCount + "\nEmpty cells: " + (inputSize - filledCellsCount));
                    } else {
                        stopTimer();
                        JOptionPane.showMessageDialog(frame, "No solution exists!");
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

    private static void parseInput() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JTextField cell = cells.get(i * SIZE + j);
                String text = cell.getText();
                if (text.isEmpty()) {
                    board.put(i + "," + j, 0);
                } else {
                    try {
                        int value = Integer.parseInt(text);
                        if (value < 1 || value > SIZE) {
                            throw new NumberFormatException();
                        }
                        board.put(i + "," + j, value);
                        filledCellsCount++;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Please enter numbers between 1 and " + SIZE + " only.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        board.put(i + "," + j, 0);
                    }
                }
            }
        }
    }

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
                    return true; // O(1)
                }
            }
        }

        board.put(row + "," + col, 0);
        operationCount++; // increment operation counter
        return false; // if no value can be placed then return false
    }

    private static boolean legal(int row, int col, int val) {
        for (int k = 0; k < SIZE; ++k) {
            if (val == board.get(row + "," + k)) {
                return false;
            }
        }

        for (int k = 0; k < SIZE; ++k) {
            if (val == board.get(k + "," + col)) {
                return false;
            }
        }

        int boxSize = (int) Math.sqrt(SIZE);
        int boxRowOffset = (row / boxSize) * boxSize;
        int boxColOffset = (col / boxSize) * boxSize;
        for (int k = 0; k < boxSize; ++k) {
            for (int m = 0; m < boxSize; ++m) {
                if (val == board.get((boxRowOffset + k) + "," + (boxColOffset + m))) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean hasDuplicates() {
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
