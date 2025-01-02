import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import ui.RoundedButtonUI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import java.util.Stack;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



public class SudokuGameScreen extends JFrame {
    private String username;
    private int difficulty;
    private boolean unfinished;
    private Stack<Move> moveStack = new Stack<>(); 
    private Stack<Move> moveStack2 = new Stack<>(); 

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
    private boolean hintModeActive = false;

    @SuppressWarnings("unchecked")
    private List<Integer>[][] notes = new List[SIZE][SIZE];
    private int[][] currentSudokuState = new int[SIZE][SIZE]; 

    private int errorCount = 0;
    private JLabel errorLabel;

    private ImageIcon notesIconActive;
    private ImageIcon notesIconInactive;

    private ImageIcon hintIconActive;
    private ImageIcon hintIconInactive;
    private int hintCount = 0; // Licznik wykorzystanych podpowiedzi



    private long startTime;
    private Timer timer;
    private JLabel timerLabel;
    private long time_2=0;

    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;
    private static final int VERY_HARD = 4;
    private String difficultyLevelText;

    private int[][] SolveSudoku = new int[SIZE][SIZE];
    private int[][] initialSudoku = new int[SIZE][SIZE];
    private int initialFilledCount ;


    public SudokuGameScreen(String username, int difficulty, boolean unfinished) {
        this.username = username;
        this.difficulty = difficulty;
        this.unfinished = unfinished;

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

        if(!unfinished){
            sudokuBoard = generateSudokuWithDialog(difficulty);
        }else{
            try (FileReader reader = new FileReader("game_state.json")) {
                JSONTokener tokener = new JSONTokener(reader);
                JSONArray usersArray = new JSONArray(tokener);
                
                if(unfinished){
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject user = usersArray.getJSONObject(i);
                        String storedUsername = user.getString("username");
                        String difficultyLevelJson = user.getString("difficultyLevel");
                        int initialFilledCountJson = user.getInt("initialFilledCount");
                        int errorCountJson = user.getInt("errorCount");
                        int solveTimeJson = user.getInt("solveTime");
    
            
                        if (storedUsername.equals(username)) {
                            JSONArray sudokuInitialArray = user.getJSONArray("initialDiagram");
                            JSONArray sudokuSolveArrayuser = user.getJSONArray("solvedDiagram");
                            int[][] loadedSudokuBoard = new int[SIZE][SIZE];
                            int[][] SolveSudokuBoard = new int[SIZE][SIZE];

            
                            for (int row = 0; row < SIZE; row++) {
                                JSONArray rowInitialArray = sudokuInitialArray.getJSONArray(row);
                                JSONArray rowSolveArray = sudokuSolveArrayuser.getJSONArray(row);
                                for (int col = 0; col < SIZE; col++) {
                                    int initialValue = rowInitialArray.getInt(col);
                                    int solveValue = rowSolveArray.getInt(col);

                                    loadedSudokuBoard[row][col] = initialValue;
                                    SolveSudokuBoard[row][col] = solveValue;

                                }
                            }
            
                            sudokuBoard = loadedSudokuBoard;
                            difficultyLevelText= difficultyLevelJson;
                            initialFilledCount= initialFilledCountJson;
                            errorCount= errorCountJson;
                            time_2=solveTimeJson;
                            initialSudoku=loadedSudokuBoard;
                            SolveSudoku= SolveSudokuBoard;
                            
                            break;
            
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
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

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                notes[i][j] = new java.util.ArrayList<>();
            }
        }
        

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
            undoButton.setBounds(30, 580, 50, 50);
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
            clearButton.setBounds(137, 580, 50, 50); 
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
            notesButton.setBounds(244, 580, 50, 50); 
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

        // Przycisk podpowiedzi
        try {
            hintIconInactive = new ImageIcon(ImageIO.read(new File("resources/hint.png")));
            hintIconActive = new ImageIcon(ImageIO.read(new File("resources/hint_active.png"))); 
            JButton hintButton = new JButton(hintIconInactive);
            hintButton.setBounds(351, 580, 50, 50); 
            hintButton.setContentAreaFilled(false);
            hintButton.setBorderPainted(false);
            hintButton.setOpaque(false);

            hintButton.addActionListener(e -> {
                hintModeActive = !hintModeActive; 

                if (hintModeActive) {
                    hintButton.setIcon(hintIconActive); 
                } else {
                    hintButton.setIcon(hintIconInactive); 
                }
            });

            layeredPane.add(hintButton, Integer.valueOf(2)); 

            layeredPane.revalidate();
            layeredPane.repaint();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Hint image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
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
                removeExistingEntry(username);
                long solveTime = System.currentTimeMillis() - startTime+time_2;
                getCurrentSudokuState();
                
                saveGame(username, errorCount,(int) solveTime, SolveSudoku, difficultyLevelText, initialFilledCount, initialSudoku,currentSudokuState,notes,hintCount);
                    
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    SudokuMenu menuScreen = new SudokuMenu(username); 
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

        if(unfinished){
            loadUnfinishedGame2(username);
            errorLabel.setText("Błędy: " + errorCount);
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (notes[row][col] != null && !notes[row][col].isEmpty()){
                        displayNotesInCell(row, col);                        
                    }
                    
                }
            }
        }

        startTime = System.currentTimeMillis();
        timer = new Timer(1000, e -> updateTimer());
        timer.start();
        
        


    }
    // zapis stanu gry diagramu
    private int[][] getCurrentSudokuState() {
        removeExistingEntry(username);
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (isNotesActiveInCell(row, col)) {
                    currentSudokuState[row][col] = 10;  // Ustawiamy 10, jeśli komórka zawiera notatki
                } else {
                    String text = sudokuButtons[row][col].getText();
                    currentSudokuState[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
                }
            }
        }
        return currentSudokuState;
    }
    
    public void loadUnfinishedGame2(String username) {
        try (FileReader reader = new FileReader("game_state.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray usersArray = new JSONArray(tokener);
            
            if(unfinished){
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = usersArray.getJSONObject(i);
                    String storedUsername = user.getString("username");
                    JSONArray notesArray = user.getJSONArray("notes");

                    @SuppressWarnings("unchecked")
                    List<Integer>[][] loadedNotes = new List[SIZE][SIZE];
        
                    if (storedUsername.equals(username)) {
                        JSONArray sudokuStateArray = user.getJSONArray("currentSudokuState");
                        JSONArray sudokuInitialArray = user.getJSONArray("initialDiagram");
        
                        for (int row = 0; row < SIZE; row++) {
                            JSONArray rowStateArray = sudokuStateArray.getJSONArray(row);
                            JSONArray rowInitialArray = sudokuInitialArray.getJSONArray(row);
                            for (int col = 0; col < SIZE; col++) {
                                int stateValue = rowStateArray.getInt(col);
                                int initialValue = rowInitialArray.getInt(col);
                                if (stateValue != initialValue){
                                    if(stateValue!=10){
                                        if(stateValue!=0){
                                            placeNumberInCell(row, col, stateValue,true);
                                        }
                                    }
                                }
                            }
                        }
                        for (int row = 0; row < SIZE; row++) {
                            for (int col = 0; col < SIZE; col++) {
                                loadedNotes[row][col] = new ArrayList<>(); 
                            }
                        }

                        for (int row = 0; row < SIZE; row++) {
                            JSONArray rowNotesArray = notesArray.getJSONArray(row); 
                            
                            for (int col = 0; col < SIZE; col++) {
                                JSONArray noteArray = rowNotesArray.getJSONArray(col); 
                                
                                for (int j = 0; j < noteArray.length(); j++) {
                                    int note = noteArray.getInt(j); 
                                    loadedNotes[row][col].add(note);
                                }
                            }
                        }
                        notes = loadedNotes;
                        if (user.has("hintCount")) {
                            hintCount = user.getInt("hintCount");
                        } else {
                            hintCount = 0; // Domyślna wartość, jeśli brak pola w danych
                        }
                        break;
        
                    }
                }
            }
    
     
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    private void openSettingsDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ustawienia");
    
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.BACKGROUND_COLOR);
    
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
    
        JButton button1 = new JButton("Zacznij od nowa");
        JButton button2 = new JButton("Nowa gra");
        JButton button3 = new JButton("Zmniejsz poziom");
        JButton button4 = new JButton("Zgłoś błąd");

    
        button1.setPreferredSize(new Dimension(300, 60));
        button2.setPreferredSize(new Dimension(300, 60));
        button3.setPreferredSize(new Dimension(300, 60));
        button4.setPreferredSize(new Dimension(300, 60));

    
        button1.setFont(new Font("Arial", Font.BOLD, 20));
        button1.setFocusPainted(false);
        button1.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button1.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button1.setBorder(BorderFactory.createEmptyBorder());
    
        button1.setContentAreaFilled(false);
        button1.setOpaque(false);
        button1.setUI(new RoundedButtonUI());

        button2.setFont(new Font("Arial", Font.BOLD, 20));
        button2.setFocusPainted(false);
        button2.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button2.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button2.setBorder(BorderFactory.createEmptyBorder());
    
        button2.setContentAreaFilled(false);
        button2.setOpaque(false);
        button2.setUI(new RoundedButtonUI());


        button3.setFont(new Font("Arial", Font.BOLD, 20));
        button3.setFocusPainted(false);
        button3.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button3.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button3.setBorder(BorderFactory.createEmptyBorder());
    
        button3.setContentAreaFilled(false);
        button3.setOpaque(false);
        button3.setUI(new RoundedButtonUI());

        button4.setFont(new Font("Arial", Font.BOLD, 20));
        button4.setFocusPainted(false);
        button4.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button4.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button4.setBorder(BorderFactory.createEmptyBorder());
    
        button4.setContentAreaFilled(false);
        button4.setOpaque(false);
        button4.setUI(new RoundedButtonUI());

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
        button4.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button1.setBackground(ColorPalette.TEXT_LIGHT_GREEN);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button1.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
            }
        });
        button4.addActionListener(e -> {
            dialog.dispose();
            SwingUtilities.invokeLater(() -> {
                AddOpinions screen = new AddOpinions(username,0); 
                screen.setVisible(true); 
            });
        
        });
        button2.addActionListener(e -> {
            removeExistingEntry(username);
            dialog.dispose();
            dispose(); 
                SwingUtilities.invokeLater(() -> {
                    SudokuGameScreen gameScreen = new SudokuGameScreen(username,difficulty, false); 
                    gameScreen.setVisible(true); 
                });
        
        });
        button3.addActionListener(e -> {
            removeExistingEntry(username);
            if(difficulty>1){
                difficulty=difficulty-1;
            }
            dialog.dispose();
            dispose(); 
                SwingUtilities.invokeLater(() -> {
                    SudokuGameScreen gameScreen = new SudokuGameScreen(username,difficulty, false); 
                    gameScreen.setVisible(true); 
                });
        
        });
        button1.addActionListener(e -> {
            dialog.dispose();
            resetGameToInitialState();
        
        });
        
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
    
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        panel.add(button4);
    
        dialog.add(panel);
    
        dialog.setSize(370, 320); 
    
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    

    
    private void updateTimer() {
        if(!unfinished){
            time_2=0;
        } 
        long elapsedTime = System.currentTimeMillis() - startTime+time_2; 
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
        int row, col, previousValue, isNotes;
        private List<Integer> [][] notes;
        
        
        public Move(int row, int col, int previousValue, int isNotes, List<Integer>[][] notes) {
            this.row = row;
            this.col = col;
            this.previousValue = previousValue;
            this.isNotes =isNotes;
            this.notes = notes;
        }

    }
    private void undoLastMove() {
        if (!moveStack.isEmpty()) {
            Move lastMove = moveStack.pop();

            JButton button = sudokuButtons[lastMove.row][lastMove.col];

            if(isNotesActiveInCell(lastMove.row,lastMove.col)){
                clearNotesInCell(lastMove.row,lastMove.col);
            }

            String currentText = button.getText();
            int currentValue = currentText.isEmpty() ? 0 : Integer.parseInt(currentText);

            clearNotesInCell(lastMove.row,lastMove.col);



            if (currentValue != 0) {
                numberCount[currentValue]--;
            }
    
            if (lastMove.previousValue == 0) {

                if (lastMove.isNotes == 0) {

                    button.setText("");
                    for (int r = 0; r < SIZE; r++) {
                        for (int c = 0; c < SIZE; c++) {
                            if (lastMove.notes[r][c] != null && !lastMove.notes[r][c].isEmpty()) {
                                clearNotesInCell(r,c);
                                notes[r][c] = lastMove.notes[r][c] == null ? null : new ArrayList<>(lastMove.notes[r][c]);
                                displayNotesInCell(r, c);
                            }                            

                        }
                    }
                } else if (lastMove.isNotes == 1) {
                    for (int r = 0; r < SIZE; r++) {
                        for (int c = 0; c < SIZE; c++) {
                            if (lastMove.notes[r][c] != null && !lastMove.notes[r][c].isEmpty()) {
                                clearNotesInCell(r,c);
                                notes[r][c] = lastMove.notes[r][c] == null ? null : new ArrayList<>(lastMove.notes[r][c]);
                                displayNotesInCell(r, c);
                            }                            

                        }
                    }
                }
            } else {
                for (int r = 0; r < SIZE; r++) {
                    for (int c = 0; c < SIZE; c++) {
                        if (lastMove.notes[r][c] != null && !lastMove.notes[r][c].isEmpty()) {
                            clearNotesInCell(r,c);
                            notes[r][c] = lastMove.notes[r][c] == null ? null : new ArrayList<>(lastMove.notes[r][c]);
                            displayNotesInCell(r, c);
                        }                            

                    }
                }
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
            @SuppressWarnings("unchecked")
            List<Integer>[][] notes1 = new List[SIZE][SIZE];
            for (int r = 0; r < SIZE; r++) {
                for (int c = 0; c < SIZE; c++) {
                    notes1[r][c] = notes[r][c] == null ? null : new ArrayList<>(notes[r][c]);
                }
            }

    
            if (!originalValues[row][col]) {
                if (isNotesActiveInCell(row, col)) {
                    moveStack.push(new Move(row, col, 0,1,notes1));
                    clearNotesInCell(row, col); 
                }
    
                String currentText = lastHighlightedButton.getText();
                int currentValue = currentText.isEmpty() ? 0 : Integer.parseInt(currentText);
    
                if (currentValue != 0) {
                    numberCount[currentValue]--; 
                }
    
                moveStack.push(new Move(row, col, currentValue,0,notes1));
    
                lastHighlightedButton.setText("");
    
                updateNumberButtonStates();
            }
        }
    }    
    private void openHintDialog() {
        JDialog dialog = new JDialog();
    
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.BACKGROUND_COLOR);
    
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
        
        JLabel messageLabel = new JLabel("Wykorzystałeś już wszystkie ");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        messageLabel.setForeground(ColorPalette.TEXT_DARK_GREEN);
        JLabel messageLabel2 = new JLabel("3 podpowiedzi");
        messageLabel2.setFont(new Font("Arial", Font.BOLD, 20));
        messageLabel2.setForeground(ColorPalette.TEXT_DARK_GREEN);

        JButton button1 = new JButton("Zamknij");

        button1.setPreferredSize(new Dimension(300, 60));
       
    
        button1.setFont(new Font("Arial", Font.BOLD, 20));
        button1.setFocusPainted(false);
        button1.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button1.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button1.setBorder(BorderFactory.createEmptyBorder());
    
        button1.setContentAreaFilled(false);
        button1.setOpaque(false);
        button1.setUI(new RoundedButtonUI());

        

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
        
        
        
        button1.addActionListener(e -> {
            dialog.dispose();        
        });
        panel.add(messageLabel);
        panel.add(messageLabel2);
        panel.add(button1);
    
        dialog.add(panel);
    
        dialog.setSize(370, 180); 
    
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    private void handleButtonClick(int row, int col) {
        resetButtonColors(); 
        
        String cellValue = sudokuButtons[row][col].getText();

        sudokuButtons[row][col].setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
        for (int k = 0; k < SIZE; k++) {
            if (k != col) {
                sudokuButtons[row][k].setBackground(ColorPalette.HIGHLIGHT_COLOR);
            }
            if (k != row) {
                sudokuButtons[k][col].setBackground(ColorPalette.HIGHLIGHT_COLOR);
            }
        }
        
        if (!cellValue.isEmpty()) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (sudokuButtons[i][j].getText().equals(cellValue) && notes[i][j].isEmpty()) {
                        sudokuButtons[i][j].setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
                    }
                    for (int value : notes[i][j]) {
                        if (String.valueOf(value).equals(cellValue)) {

                            sudokuButtons[i][j].setBackground(ColorPalette.HIGHLIGHT_NOTE_COLOR);
                        }
                    }
                    
                    
                }
            }
        }
            if (hintModeActive){
                if (hintCount >= 3) {
                    openHintDialog();
                }else{
                    int hintNumber = SolveSudoku[row][col];
                    placeNumberInCell(row, col, hintNumber, false);
                    hintCount++;
                }
                
            }
        
    
        
    
        lastHighlightedButton = sudokuButtons[row][col];
    }
    

    private void handleNumberButtonClick(int number) {
        int n=0;
        int x=0;
        JButton selectedButton = lastHighlightedButton;
        if (selectedButton == null) return;

        int row = -1, col = -1;
        outerLoop:
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (sudokuButtons[i][j] == selectedButton) {
                    row = i;
                    col = j;
                    break outerLoop;
                }
            }
        }
    
        if (notesModeActive) {
            if (!originalValues[row][col]) {
                @SuppressWarnings("unchecked")
                List<Integer>[][] notes1 = new List[SIZE][SIZE];
                for (int r = 0; r < SIZE; r++) {
                    for (int c = 0; c < SIZE; c++) {
                        notes1[r][c] = notes[r][c] == null ? null : new ArrayList<>(notes[r][c]);
                    }
                }
                if (notes[row][col].contains(number)) {
                    n=1;
                    notes[row][col].remove(Integer.valueOf(number));
                } else {
                    n=1;
                    notes[row][col].add(number);
                }
                moveStack.push(new Move(row, col, x, n, notes1));

                displayNotesInCell(row, col);
            }
        } else {
            placeNumberInCell(row, col, number,false);
        }
    }
    private void placeNumberInCell(int row, int col, int number,boolean load) {
        if (originalValues[row][col]) return; 
        int n=0;

        @SuppressWarnings("unchecked")
        List<Integer>[][] notes1 = new List[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                notes1[r][c] = notes[r][c] == null ? null : new ArrayList<>(notes[r][c]);
            }
        }
    
        JButton button = sudokuButtons[row][col];
        if(isNotesActiveInCell(row,col)){
            clearNotesInCell(row, col);
            n=1;
        }
        String currentText = button.getText();
        int currentValue = currentText.isEmpty() ? 0 : Integer.parseInt(currentText);
        
        if (currentValue != number) {
            moveStack.push(new Move(row, col, currentValue, n, notes1));
            clearNotesInCell(row, col);
        }
        if (currentValue != 0) {
            numberCount[currentValue]--;

        }

        button.setText(String.valueOf(number));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(ColorPalette.TEXT_DARK_GREEN);
        
        numberCount[number]++;
        
        if(!load){
            if (!isValid(sudokuBoard, row, col, number)) {
                errorCount++; 
                errorLabel.setText("Błędy: " + errorCount); 
            }else{
                for (int i = 1; i < numberCount.length; i++) {
                    if(numberCount[i]!=9){
                        break;
                    }
                    if (i==9 && numberCount[i]==9){ // zakonczona rozgrywka
                        if (isSudokuValid(sudokuBoard)){
                            long solveTime = System.currentTimeMillis() - startTime+time_2;
                            removeExistingEntry(username);
                            saveGameData(username, errorCount,(int) solveTime, SolveSudoku, difficultyLevelText, initialFilledCount, initialSudoku,hintCount);
                        }
                    }
                }
        
            }
            sudokuBoard[row][col] = number;
        }else{
            for (int i = 1; i < numberCount.length; i++) {
                if(numberCount[i]!=9){
                    break;
                }
                if (i==9 && numberCount[i]==9){ // zakonczona rozgrywka
                    if (isSudokuValid(sudokuBoard)){
                        long solveTime = System.currentTimeMillis() - startTime+time_2;
                        removeExistingEntry(username);
                        saveGameData(username, errorCount,(int) solveTime, SolveSudoku, difficultyLevelText, initialFilledCount, initialSudoku,hintCount);
                    }
                }
            }
        }
        
        updateNumberButtonStates();
        clearRelatedNotes(row,col,number);
    }

    private void clearRelatedNotes(int row, int col, int number) {
        for (int i = 0; i < SIZE; i++) {
            if (notes[i][col] != null && isNotesActiveInCell(i, col)) {
                notes[i][col].remove(Integer.valueOf(number));
                displayNotesInCell(i, col);
            }
        }
    
        for (int j = 0; j < SIZE; j++) {
            if (notes[row][j] != null && isNotesActiveInCell(row, j)) {
                notes[row][j].remove(Integer.valueOf(number));
                displayNotesInCell(row, j);
            }
        }
    
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
    
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (notes[i][j] != null && isNotesActiveInCell(i, j)) {
                    notes[i][j].remove(Integer.valueOf(number));
                    displayNotesInCell(i, j);
                }
            }
        }
    }
    
    public boolean removeExistingEntry(String username) {
        try (FileReader reader = new FileReader("game_state.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray usersArray = new JSONArray(tokener);
    
            JSONArray updatedArray = new JSONArray();
    
            boolean found = false;
    
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                String storedUsername = user.getString("username");
    
                if (storedUsername.equals(username)) {
                    found = true;
                } else {
                    updatedArray.put(user);
                }
            }
    
            try (FileWriter writer = new FileWriter("game_state.json")) {
                writer.write(updatedArray.toString(4)); 
            }
    
            return found; 
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    
    private void resetGameToInitialState() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (originalValues[row][col]==false){
                    JButton button = sudokuButtons[row][col];
                    button.setText("");
                }
                notes[row][col] = new ArrayList<>();
            }
        }
        for (int i = 1; i < numberCount.length; i++) {
            numberCount[i]=0;
        }
        countInitialNumbers();
        
        errorCount = 0;
        errorLabel.setText("Błędy: " + errorCount);
        moveStack.clear();
        moveStack2.clear();


        

        startTime = System.currentTimeMillis();
        timer = new Timer(1000, e -> updateTimer());
        timer.start();
        updateTimer();
    
    }
    
    
    
    public void saveGameData(String username, int errorCount, int solveTime, int[][] SolveSudoku,
                         String difficultyLevel, int initialFilledCount, int[][] initialSudoku, int hint) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JSONObject gameData = new JSONObject();

        try {
            JSONArray gameDataList;
            
            try (FileReader reader = new FileReader("game_data.json")) {
                gameDataList = new JSONArray(new JSONTokener(reader));
            } catch (IOException e) {
                gameDataList = new JSONArray();
            }

            int maxId = 0;
            for (int i = 0; i < gameDataList.length(); i++) {
                JSONObject existingData = gameDataList.getJSONObject(i);
                if (existingData.has("data_game_id")) {
                    maxId = Math.max(maxId, existingData.getInt("data_game_id"));
                }
            }
            int finalId = maxId + 1;


            gameData.put("data_game_id", finalId);
            gameData.put("username", username);
            gameData.put("solveTime", solveTime);
            gameData.put("errorCount", errorCount);
            gameData.put("solveDate", LocalDateTime.now().format(formatter));
            gameData.put("solvedDiagram", SolveSudoku);
            gameData.put("difficultyLevel", difficultyLevel);
            gameData.put("initialFilledCount", initialFilledCount);
            gameData.put("initialDiagram", initialSudoku);
            gameData.put("hintCount", hint);

            gameDataList.put(gameData);

            try (FileWriter file = new FileWriter("game_data.json")) {
                file.write(gameDataList.toString(4));
            }

            System.out.println("Dane gry zostały zapisane pomyślnie.");
            dispose(); 
                SwingUtilities.invokeLater(() -> {
                    EndGameScreen endgameScreen = new EndGameScreen(username, difficultyLevel, solveTime, errorCount, finalId); 
                    endgameScreen.setVisible(true); 
                });

        } catch (IOException e) {
            System.out.println("Błąd przy zapisie danych do pliku: " + e.getMessage());
        }
    }


    public void saveGame(String username, int errorCount,int elapsedTime, int[][] SolveSudoku,
                             String difficultyLevel, int initialFilledCount, int[][] initialSudoku, int[][]currentSudokuState, List<Integer>[][] notes, int hint) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         
        JSONObject gameData = new JSONObject();

        try {
            
            gameData.put("username", username);
            gameData.put("solveTime", elapsedTime);
            gameData.put("errorCount", errorCount);
            gameData.put("solveDate", LocalDateTime.now().format(formatter));
            gameData.put("solvedDiagram", SolveSudoku);
            gameData.put("difficultyLevel", difficultyLevel);
            gameData.put("initialFilledCount", initialFilledCount);
            gameData.put("initialDiagram", initialSudoku);
            gameData.put("currentSudokuState", currentSudokuState);
            gameData.put("notes", notes);
            gameData.put("hintCount", hint);

            

            JSONArray gameDataList;
            try (FileReader reader = new FileReader("game_state.json")) {
                gameDataList = new JSONArray(new JSONTokener(reader));
            } catch (IOException e) {
                gameDataList = new JSONArray();
            }

            gameDataList.put(gameData);

            try (FileWriter file = new FileWriter("game_state.json")) {
                file.write(gameDataList.toString(4));
            }

            System.out.println("Stan gry zostały zapisane pomyślnie.");

        } catch (IOException e) {
            System.out.println("Błąd przy zapisie danych do pliku: " + e.getMessage());
        }
    }
    
    private void displayNotesInCell(int row, int col) {
        JButton button = sudokuButtons[row][col];
        List<Integer> cellNotes = notes[row][col];
    
        StringBuilder sb = new StringBuilder("<html><pre>");
        
        for (int i = 1; i <= 9; i++) {
            if (cellNotes.contains(i)) {
                sb.append(i); 
            } else {
                sb.append(" "); 
            }
    
           
            if (i % 3 == 0) {
                sb.append("<br>");
            } else {
                sb.append(" ");
            }
        }
    
        sb.append("</pre></html>");
    
        button.setText(sb.toString());
        button.setFont(new Font("Arial", Font.PLAIN, 10)); 
        button.setForeground(ColorPalette.TEXT_DARK_GREEN);
    }
    
    
    private boolean isNotesActiveInCell(int row, int col) {
        return notes[row][col] != null && !notes[row][col].isEmpty();
    }
    private void clearNotesInCell(int row, int col) {
        notes[row][col].clear();
        
        JButton button = sudokuButtons[row][col];
        button.setText(""); 
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
    private int[][] generateSudokuWithDialog(int difficultyLevel) {
        String[] imagePaths = {
            "resources/l1.png",
            "resources/l2.png",
            "resources/l3.png",
            "resources/l4.png",
            "resources/l5.png",
            "resources/l6.png",
            "resources/l7.png",
            "resources/l8.png",

        };
    
        int imageWidth = 432;  
        int imageHeight = 768; 
    
        JDialog loadingDialog = new JDialog((Frame) null, "Generating Sudoku", true);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        loadingDialog.setSize(432, 768);
        loadingDialog.setLocationRelativeTo(null);
    
        JLabel loadingImage = new JLabel();
    
        Timer imageRotationTimer = new Timer(120, new ActionListener() {
            int currentIndex = 0;
    
            @Override
            public void actionPerformed(ActionEvent e) {
                currentIndex = (currentIndex + 1) % imagePaths.length; 
    
                ImageIcon icon = new ImageIcon(imagePaths[currentIndex]);
                Image scaledImage = icon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
                loadingImage.setIcon(new ImageIcon(scaledImage));
            }
        });
    
        loadingDialog.add(loadingImage);
    
        SwingWorker<int[][], Void> worker = new SwingWorker<>() {
            @Override
            protected int[][] doInBackground() throws Exception {
                imageRotationTimer.start(); 
                return generateSudoku(difficultyLevel); 
            }
    
            @Override
            protected void done() {
                
                imageRotationTimer.stop(); 
                loadingDialog.dispose(); 
            }
        };
    
        worker.execute();
        loadingDialog.setVisible(true);
    
        try {
            return worker.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    private int[][] generateSudoku(int difficultyLevel) {
        int[][] sudoku = new int[SIZE][SIZE];
        Arrays.fill(SolveSudoku, null);
        Arrays.fill(initialSudoku, null);
        fillSudoku(sudoku, 0, 0);
        // Tworzymy kopię uzupelnionego stanu diagramu
        for (int i = 0; i < SIZE; i++) {
            SolveSudoku[i] = sudoku[i].clone();
        }
        initialFilledCount= getNumberOfCellsToKeep(difficultyLevel);
        removeCells(sudoku, 81- initialFilledCount);

        // Tworzymy kopię początkowego stanu diagramu
        for (int i = 0; i < SIZE; i++) {
            initialSudoku[i] = sudoku[i].clone();
        }

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
    private boolean isSudokuValid(int[][] sudoku) {
        for (int row = 0; row < sudoku.length; row++) {
            for (int col = 0; col < sudoku[row].length; col++) {
                int num = sudoku[row][col];
    
                if (num == 0) continue;
    
                sudoku[row][col] = 0;
    
                if (!isValid(sudoku, row, col, num)) {
                    sudoku[row][col] = num; 
                    return false;
                }
    
                sudoku[row][col] = num;
            }
        }
        return true;
    }
    

    private boolean isValid(int[][] sudoku, int row, int col, int num) {
        for (int x = 0; x < SIZE; x++) {
            if (sudoku[row][x] == num || sudoku[x][col] == num || sudoku[row - row % 3 + x / 3][col - col % 3 + x % 3] == num) {
                //System.out.println(sudoku[row][x]);
                //System.out.println(sudoku[x][row]);
                //System.out.println(sudoku[row - row % 3 + x / 3][col - col % 3 + x % 3]);
                return false;
            }
        }
        return true;
    }
    

    private void removeCells(int[][] sudoku, int cellsToRemove) {
        Random random = new Random();
        long startTime = System.currentTimeMillis();

        while (cellsToRemove > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            if (System.currentTimeMillis() - startTime > 5000) {
                System.out.println("Przekroczono czas generowania, rozpoczynam od nowa.");
                int[][] newSudoku = generateSudoku(difficulty);
                for (int i = 0; i < SIZE; i++) {
                    System.arraycopy(newSudoku[i], 0, sudoku[i], 0, SIZE);
                }
                return;
                }

            if (sudoku[row][col] != 0) {
                int temp = sudoku[row][col];
                sudoku[row][col] = 0;

                if (!hasUniqueSolution(sudoku)) {
                    sudoku[row][col] = temp; 
                } else {
                    cellsToRemove--;
                }
            }
        }
    }

    private boolean hasUniqueSolution(int[][] sudoku) {
        return countSolutions(sudoku, 0, 0) == 1;
    }

    private int countSolutions(int[][] sudoku, int row, int col) {
        if (row == SIZE - 1 && col == SIZE) return 1;
        if (col == SIZE) { row++; col = 0; }
        if (sudoku[row][col] != 0) return countSolutions(sudoku, row, col + 1);

        int count = 0;
        for (int num = 1; num <= SIZE; num++) {
            if (isValid(sudoku, row, col, num)) {
                sudoku[row][col] = num;
                count += countSolutions(sudoku, row, col + 1);
                if (count > 1) break;
                sudoku[row][col] = 0;
            }
        }
        sudoku[row][col] = 0; 
        return count;
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
            SudokuGameScreen screen = new SudokuGameScreen("username",2,false);
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