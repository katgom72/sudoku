
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import java.util.Stack;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



public class SudokuGameScreen extends JFrame {
    private Stack<Move> moveStack = new Stack<>(); 
    private static final int SIZE = 9;
    private int[][] sudokuBoard;
    private JButton[][] sudokuButtons;
    private boolean[][] originalValues;
    private static final Color BORDER_COLOR = ColorPalette.ICON_BORDER_COLOR;
    Color originalColor = ColorPalette.SUDOKU_SQUARE_COLOR;
    private JButton lastHighlightedButton = null;
    private int[] numberCount = new int[SIZE + 1]; 
    private JButton[] numberButtons = new JButton[SIZE]; 
    private boolean notesModeActive = false;
    private int errorCount = 0;
    private JLabel errorLabel;

    private ImageIcon notesIconActive;
    private ImageIcon notesIconInactive;

    private long startTime;
    private Timer timer;
    private JLabel timerLabel;

    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;
    private static final int VERY_HARD = 4;
    private String difficultyLevelText;


    public SudokuGameScreen() {
        setSize(432, 768);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, getWidth(), getHeight());
        add(layeredPane);

        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/background3.png"));
            JPanel backgroundPanel = new BackgroundPanel(backgroundImage);
            backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
            layeredPane.add(backgroundPanel, Integer.valueOf(0)); 
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }


        sudokuBoard = generateSudoku(1);
        sudokuButtons = new JButton[SIZE][SIZE];
        originalValues = new boolean[SIZE][SIZE];

        JPanel sudokuPanel = new JPanel(new GridLayout(SIZE, SIZE));
        sudokuPanel.setBounds(16, 170, 400, 400);
        sudokuPanel.setOpaque(false);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sudokuButtons[i][j] = new JButton();
                sudokuButtons[i][j].setBackground(originalColor);
                sudokuButtons[i][j].setOpaque(true);
                sudokuButtons[i][j].setContentAreaFilled(true);
                sudokuButtons[i][j].setFont(new Font("Arial", Font.BOLD, 24)); 


                int row = i;
                int col = j;
                int value = sudokuBoard[i][j];

                if (value != 0) {
                    sudokuButtons[i][j].setText(String.valueOf(value));
                    sudokuButtons[i][j].setFocusable(false);
                    originalValues[row][col] = true;
                } else {
                    sudokuButtons[i][j].setFocusable(true);
                }

                sudokuButtons[i][j].addActionListener(e -> handleButtonClick(row, col));

                int top = (i % 3 == 0) ? 3 : 1;
                int left = (j % 3 == 0) ? 3 : 1;
                int bottom = (i == SIZE - 1) ? 3 : 1;
                int right = (j == SIZE - 1) ? 3 : 1;

                sudokuButtons[i][j].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, BORDER_COLOR));
                sudokuPanel.add(sudokuButtons[i][j]);
            }
        }
        layeredPane.add(sudokuPanel, Integer.valueOf(1));

        // Panel przycisków numerycznych
        JPanel numberPanel = new JPanel(new GridLayout(1, 9, 5, 5));
        numberPanel.setBounds(16, 650, 400, 40);

        for (int i = 1; i <= SIZE; i++) {
            final int number = i; 
            numberButtons[i - 1] = new JButton(String.valueOf(i));
            JButton numberButton = numberButtons[i - 1];
            numberButton.setForeground(Color.BLACK);
            numberButton.setFont(new Font("Arial", Font.BOLD, 40));
            numberButton.setContentAreaFilled(false);
            numberButton.setOpaque(false);
            numberButton.setBorder(BorderFactory.createEmptyBorder());
            numberButton.addActionListener(e -> handleNumberButtonClick(number));
            numberPanel.add(numberButton);
        }
        numberPanel.setBackground(ColorPalette.BACKGROUND_COLOR);
        layeredPane.add(numberPanel, Integer.valueOf(1));
        countInitialNumbers();


        // Przycisk cofania 
        try {
            BufferedImage undoIcon = ImageIO.read(new File("resources/undo.png"));
            JButton undoButton = new JButton(new ImageIcon(undoIcon));
            undoButton.setBounds(60, 580, 50, 50);
            undoButton.setContentAreaFilled(false);
            undoButton.setBorderPainted(false);
            undoButton.setOpaque(false);
            undoButton.addActionListener(e -> undoLastMove());



            layeredPane.add(undoButton, Integer.valueOf(2));

            layeredPane.revalidate(); 
            layeredPane.repaint(); 
            
        } catch (IOException e) {
            e.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Undo image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Przycisk wymazywania
        try {
            BufferedImage clearIcon = ImageIO.read(new File("resources/clear.png")); 
            JButton clearButton = new JButton(new ImageIcon(clearIcon));
            clearButton.setBounds(190, 580, 50, 50); 
            clearButton.setContentAreaFilled(false);
            clearButton.setBorderPainted(false);
            clearButton.setOpaque(false);

            clearButton.addActionListener(e -> clearHighlightedCell());



            layeredPane.add(clearButton, Integer.valueOf(2)); 

            layeredPane.revalidate(); 
            layeredPane.repaint(); 

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Clear image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Przycisk notatek
        try {
            notesIconInactive = new ImageIcon(ImageIO.read(new File("resources/notes.png")));
            notesIconActive = new ImageIcon(ImageIO.read(new File("resources/notes_active.png"))); 
            JButton notesButton = new JButton(notesIconInactive);
            notesButton.setBounds(330, 580, 50, 50); 
            notesButton.setContentAreaFilled(false);
            notesButton.setBorderPainted(false);
            notesButton.setOpaque(false);

            notesButton.addActionListener(e -> {
                notesModeActive = !notesModeActive; 

                if (notesModeActive) {
                    notesButton.setIcon(notesIconActive); 
                } else {
                    notesButton.setIcon(notesIconInactive); 
                }
            });

            layeredPane.add(notesButton, Integer.valueOf(2)); 

            layeredPane.revalidate();
            layeredPane.repaint();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Notes image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Przycisku cofania do menu
        try {
            BufferedImage backIcon = ImageIO.read(new File("resources/back.png"));
            JButton backButton = new JButton(new ImageIcon(backIcon));
            backButton.setBounds(30, 50, 60, 40);
            backButton.setContentAreaFilled(false);
            backButton.setBorderPainted(false);
            backButton.setOpaque(false);

            backButton.addActionListener(e -> {
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    SudokuMenu menuScreen = new SudokuMenu(); 
                    menuScreen.setVisible(true); 
                });
            });


            layeredPane.add(backButton, Integer.valueOf(2)); 

            layeredPane.revalidate(); 
            layeredPane.repaint(); 

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Back image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Przycisk ustwień
        try {
            BufferedImage settingsIcon = ImageIO.read(new File("resources/settings.png")); 
            JButton settingsButton = new JButton(new ImageIcon(settingsIcon));
            settingsButton.setBounds(350, 50, 60, 40); 
            settingsButton.setContentAreaFilled(false);
            settingsButton.setBorderPainted(false);
            settingsButton.setOpaque(false);

            settingsButton.addActionListener(e -> openSettingsDialog());



            layeredPane.add(settingsButton, Integer.valueOf(2));

            layeredPane.revalidate(); 
            layeredPane.repaint(); 

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Settings image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        errorLabel = new JLabel("Błędy: 0"); 
        errorLabel.setFont(new Font("Arial", Font.BOLD, 18)); 
        errorLabel.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        errorLabel.setBounds(185, 135, 400, 40); 
        layeredPane.add(errorLabel, Integer.valueOf(1)); 

        timerLabel = new JLabel("Czas: 0s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(ColorPalette.TEXT_DARK_GREEN);
        timerLabel.setBounds(312, 135, 200, 40);
        layeredPane.add(timerLabel, Integer.valueOf(1)); 

        startTime = System.currentTimeMillis();
        timer = new Timer(1000, e -> updateTimer());
        timer.start();


    }
    private void openSettingsDialog() {
        // Tworzenie okna dialogowego
        JDialog dialog = new JDialog();
        dialog.setTitle("Ustawienia");
    
        // Ustawienie tła
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.BACKGROUND_COLOR);
    
        // Ustawienie układu FlowLayout w pionie
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Przesunięcie w pionie i poziomie
    
        // Tworzenie przycisków
        JButton button1 = new JButton("Zacznij od nowa");
        JButton button2 = new JButton("Nowa gra");
        JButton button3 = new JButton("Zmień kolorystykę");
    
        // Ustawienie stałego rozmiaru przycisków
        button1.setPreferredSize(new Dimension(300, 60));
        button2.setPreferredSize(new Dimension(300, 60));
        button3.setPreferredSize(new Dimension(300, 60));
    
        // Ustawienie stylu przycisków
        button1.setFont(new Font("Arial", Font.BOLD, 20));
        button1.setFocusPainted(false);
        button1.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button1.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button1.setBorder(BorderFactory.createEmptyBorder());
    
        // Zaokrąglone tło
        button1.setContentAreaFilled(false);
        button1.setOpaque(false);
        button1.setUI(new RoundedButtonUI());


        // Ustawienie stylu przycisków
        button2.setFont(new Font("Arial", Font.BOLD, 20));
        button2.setFocusPainted(false);
        button2.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button2.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button2.setBorder(BorderFactory.createEmptyBorder());
    
        // Zaokrąglone tło
        button2.setContentAreaFilled(false);
        button2.setOpaque(false);
        button2.setUI(new RoundedButtonUI());


        // Ustawienie stylu przycisków
        button3.setFont(new Font("Arial", Font.BOLD, 20));
        button3.setFocusPainted(false);
        button3.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button3.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button3.setBorder(BorderFactory.createEmptyBorder());
    
        // Zaokrąglone tło
        button3.setContentAreaFilled(false);
        button3.setOpaque(false);
        button3.setUI(new RoundedButtonUI());

        // Podświetlenie przy kliknięciu
        button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button1.setBackground(ColorPalette.TEXT_LIGHT_GREEN);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button1.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
            }
        });
        // Podświetlenie przy kliknięciu
        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button2.setBackground(ColorPalette.TEXT_LIGHT_GREEN);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button2.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
            }
        });
        // Podświetlenie przy kliknięciu
        button3.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button3.setBackground(ColorPalette.TEXT_LIGHT_GREEN);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button3.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
            }
        });
    
        // Dodanie przycisków do panelu
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
    
        // Dodanie panelu do okna dialogowego
        dialog.add(panel);
    
        // Ustawienie rozmiaru okna dialogowego
        dialog.setSize(370, 250);  // Zwiększono rozmiar okna dla lepszego widoku
    
        // Ustawienie zamknięcia okna
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
        // Wyświetlenie okna na środku ekranu
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    

    
    private void updateTimer() { 
        long elapsedTime = System.currentTimeMillis() - startTime; 
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
    
        timerLabel.setText(String.format("Czas: %02d:%02d", minutes, seconds));
    }
    
    private void countInitialNumbers() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int value = sudokuBoard[i][j];
                if (value != 0) {
                    numberCount[value]++;
                }
            }
        }
        updateNumberButtonStates();
    }

    private class Move {
        int row, col, previousValue;

        public Move(int row, int col, int previousValue) {
            this.row = row;
            this.col = col;
            this.previousValue = previousValue;
        }
    }
    private void undoLastMove() {
        if (!moveStack.isEmpty()) {
            Move lastMove = moveStack.pop();
            JButton button = sudokuButtons[lastMove.row][lastMove.col];
            
            
            String currentText = button.getText();
            int currentValue = currentText.isEmpty() ? 0 : Integer.parseInt(currentText);
    
            if (currentValue != 0) {
                numberCount[currentValue]--;
            }
    
            if (lastMove.previousValue == 0) {
                button.setText("");
            } else {
                button.setText(String.valueOf(lastMove.previousValue));
                numberCount[lastMove.previousValue]++; 
            }
    
            updateNumberButtonStates();
        }
    }
    private void clearHighlightedCell() {
        if (lastHighlightedButton != null) {
            int row = lastHighlightedButton.getY() / 40;
            int col = lastHighlightedButton.getX() / 40;
    
            if (!originalValues[row][col]) {
                String currentText = lastHighlightedButton.getText();
                int currentValue = currentText.isEmpty() ? 0 : Integer.parseInt(currentText);
    
                if (currentValue != 0) {
                    numberCount[currentValue]--; 
                }
    
                moveStack.push(new Move(row, col, currentValue));
    
                lastHighlightedButton.setText("");
    
                updateNumberButtonStates();
            }
        }
    }
    


    private void handleButtonClick(int row, int col) {
        resetButtonColors(); 
        
        String cellValue = sudokuButtons[row][col].getText();
        
        if (!cellValue.isEmpty()) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (sudokuButtons[i][j].getText().equals(cellValue)) {
                        sudokuButtons[i][j].setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
                    }
                }
            }
        }
    
        sudokuButtons[row][col].setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
        for (int k = 0; k < SIZE; k++) {
            if (k != col) {
                sudokuButtons[row][k].setBackground(ColorPalette.HIGHLIGHT_COLOR);
            }
            if (k != row) {
                sudokuButtons[k][col].setBackground(ColorPalette.HIGHLIGHT_COLOR);
            }
        }
    
        lastHighlightedButton = sudokuButtons[row][col];
    }
    

    private void handleNumberButtonClick(int number) {
        if (lastHighlightedButton != null) {
            int row = lastHighlightedButton.getY() / 40;
            int col = lastHighlightedButton.getX() / 40;
    
            if (!originalValues[row][col]) {
                String currentText = lastHighlightedButton.getText();
                int currentValue = currentText.isEmpty() ? 0 : Integer.parseInt(currentText);
    
                moveStack.push(new Move(row, col, currentValue));
    
                if (currentValue != 0) {
                    numberCount[currentValue]--;
                }
    
                lastHighlightedButton.setText(String.valueOf(number));
                lastHighlightedButton.setForeground(ColorPalette.TEXT_DARK_GREEN);
    
                numberCount[number]++;
    
                if (!isValid(sudokuBoard, row, col, number)) {
                    errorCount++; 
                    errorLabel.setText("Błędy: " + errorCount); 
                }
    
                updateNumberButtonStates();
            }
        }
    }
    


    private void updateNumberButtonStates() {
        for (int i = 1; i <= SIZE; i++) {
            if (numberCount[i] >= 9) {
                numberButtons[i - 1].setEnabled(false); 
            } else {
                numberButtons[i - 1].setEnabled(true);  
            }
        }
    }

    
    private void resetButtonColors() {
        if (lastHighlightedButton != null) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    sudokuButtons[i][j].setBackground(originalColor);
                }
            }
        }
    }

    private int[][] generateSudoku(int difficultyLevel) {
        int[][] sudoku = new int[SIZE][SIZE];
        fillSudoku(sudoku, 0, 0);
        removeCells(sudoku, 81- getNumberOfCellsToKeep(difficultyLevel));
        switch (difficultyLevel) {
            case EASY:
                difficultyLevelText = "Łatwy";
                break;
            case MEDIUM:
                difficultyLevelText = "Średni";
                break;
            case HARD:
                difficultyLevelText = "Trudny";
                break;
            case VERY_HARD:
                difficultyLevelText = "Bardzo trudny";
                break;
            default:
                difficultyLevelText = "Łatwy"; 
        }
        return sudoku; 
    }

    private boolean fillSudoku(int[][] sudoku, int row, int col) {
        if (row == SIZE - 1 && col == SIZE) return true;
        if (col == SIZE) { row++; col = 0; }
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Collections.shuffle(numbers);
        for (int num : numbers) {
            if (isValid(sudoku, row, col, num)) {
                sudoku[row][col] = num;
                if (fillSudoku(sudoku, row, col + 1)) return true;
                sudoku[row][col] = 0;
            }
        }
        return false;
    }

    private boolean isValid(int[][] sudoku, int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (sudoku[row][x] == num || sudoku[x][col] == num || sudoku[row - row % 3 + x / 3][col - col % 3 + x % 3] == num) {
                return false;
            }
        }
        return true;
    }

    private void removeCells(int[][] sudoku, int cellsToRemove) {
        Random random = new Random();
        while (cellsToRemove > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            if (sudoku[row][col] != 0) {
                sudoku[row][col] = 0;
                cellsToRemove--;
            }
        }
    }

    private int getNumberOfCellsToKeep(int difficultyLevel) {
        switch (difficultyLevel) {
            case EASY:
                return getRandomCount(36, 46);
            case MEDIUM:
                return getRandomCount(32, 35);
            case HARD:
                return getRandomCount(28, 31);
            case VERY_HARD:
                return getRandomCount(17, 27);
            default:
                return getRandomCount(36, 46); 
        }
    }
    private int getRandomCount(int minCells, int maxCells) {
        Random random = new Random();
        return random.nextInt(maxCells - minCells + 1) + minCells;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuGameScreen screen = new SudokuGameScreen();
            screen.setVisible(true);
        });
    }
    

    private class BackgroundPanel extends JPanel {
        private final Image background;

        public BackgroundPanel(Image background) {
            this.background = background;
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(ColorPalette.TEXT_DARK_GREEN);
            g.drawString(difficultyLevelText, 21, 161);

        }
    }
}
