import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;



public class SudokuMenu extends JFrame {
    private String username;

    public SudokuMenu(String username) {
        this.username = username;
        setSize(432, 768);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/background2.png"));
            setContentPane(new BackgroundPanel(backgroundImage));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        setLocationRelativeTo(null);
        setLayout(null);
        
        addButton("Graj", 245,1);
        addButton("Jak grać?", 315,2);
        addButton("Statystyki", 385,3);
        addButton("Taktyki", 455,4);
        addButton("O aplikacji", 525,5);
        addButton("Wyloguj", 595,6);

    }

    private void addButton(String text, int yPosition, int numer) {
        JButton button = new JButton(text);
        button.setBounds(66, yPosition, 300, 60);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button.setBorder(BorderFactory.createEmptyBorder());

        // Zaokrąglone tło
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new RoundedButtonUI());

        // Podświetlenie przy kliknięciu
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(ColorPalette.TEXT_LIGHT_GREEN);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR);
            }
        });
        button.addActionListener(e -> {
            if (numer==1){
                if(hasUnfinishedGame(username)){
                    openSettingsDialog();
                }else{
                    if(countEntriesByUsername(username)==0){
                    
                        dispose(); 
                        SwingUtilities.invokeLater(() -> {
                            SudokuGameScreen gameScreen = new SudokuGameScreen(username,1,false); 
                            gameScreen.setVisible(true); 
                        });
                    }else{
                        int difficulty = determineNextLevel(username);
                        System.out.println(difficulty);
                        dispose(); 
                        SwingUtilities.invokeLater(() -> {
                            SudokuGameScreen gameScreen = new SudokuGameScreen(username,difficulty,false); 
                            gameScreen.setVisible(true); 
                        });
                    }
                }
            }
            if (numer==2){
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    HowToPlay htpScreen = new HowToPlay(username); 
                    htpScreen.setVisible(true); 
                });
            }
            if (numer==6){
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    UserLoginScreen loginScreen = new UserLoginScreen(); 
                    loginScreen.setVisible(true); 
                });
            }
        
        });

        add(button);
    }

    private void openSettingsDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Ustawienia");
    
        JPanel panel = new JPanel();
        panel.setBackground(ColorPalette.BACKGROUND_COLOR);
    
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); 
    
        JButton button1 = new JButton("Kontynuuj grę");
        JButton button2 = new JButton("Zacznij nową gra");
    
        button1.setPreferredSize(new Dimension(300, 60));
        button2.setPreferredSize(new Dimension(300, 60));
    
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
        button2.addActionListener(e -> {
            if(countEntriesByUsername(username)==0){
                    
                dialog.dispose();
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    SudokuGameScreen gameScreen = new SudokuGameScreen(username,1, false); 
                    gameScreen.setVisible(true); 
                });
            }else{
                int difficulty = determineNextLevel(username);
                dialog.dispose();
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    SudokuGameScreen gameScreen = new SudokuGameScreen(username,difficulty, false); 
                    gameScreen.setVisible(true); 
                });
            }
        
        });
        button1.addActionListener(e -> {
            dialog.dispose();
            dispose();
            SwingUtilities.invokeLater(() -> {
                SudokuGameScreen gameScreen = new SudokuGameScreen(username,1, true); 
                gameScreen.setVisible(true); 
            });
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
    
        panel.add(button1);
        panel.add(button2);
    
        dialog.add(panel);
    
        dialog.setSize(370, 180); 
    
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static int countEntriesByUsername(String username) {
        try (FileReader reader = new FileReader("game_data.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray usersArray = new JSONArray(tokener);
    
            
            int count = 0;
        
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                String storedUsername = user.getString("username");
    
                if (storedUsername.equals(username)) {
                    count=count+1;
                }
            }
    
            return count; 
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return 0;
    }

    public boolean hasUnfinishedGame(String username) {
        try (FileReader reader = new FileReader("game_state.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray usersArray = new JSONArray(tokener);
    
            
        
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                String storedUsername = user.getString("username");
    
                if (storedUsername.equals(username)) {
                    return true;
                }
            }
            return false;
     
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    public static int firstLevel(String username) {
        try (FileReader reader = new FileReader("registration_data.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray usersArray = new JSONArray(tokener);
    
            
            int difficulty = 0;
        
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                String storedUsername = user.getString("username");
                String d= user.getString("skillLevel");
    
                if (storedUsername.equals(username)) {
                    if("Początkujący".equals(d)){
                        difficulty=1;
                    }
                    if("Średniozaawansowany".equals(d)){
                        difficulty=1;
                    }
                    if("Zaawansowany".equals(d)){
                        difficulty=2;
                    }
                    if("Ekspert".equals(d)){
                        difficulty=3;
                    }
                }
            }
    
            return difficulty; 
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return 1;
    }
    public static int determineNextLevel(String username) {
        try (FileReader reader = new FileReader("game__feedback_data.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray usersArray = new JSONArray(tokener);
            
            int id = 0;
            int difficulty = 1;
        
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                String storedUsername = user.getString("username");
                int lastgame = user.getInt("data_game_id");
                String difficultyLevel = user.getString("difficultyLevel");
                String next = user.getString("a3");
                System.out.println(difficultyLevel);
                System.out.println(next);
    
                if (storedUsername.equals(username)) {
                    if (lastgame > id) {
                        System.out.println("udalo sie 1");
                        id = lastgame;
    
                        if ("Łatwy".equals(difficultyLevel)) {
                            System.out.println("udalo sie 2");
                            if ("Trudniejszy".equals(next)) {
                                difficulty = 2;
                            } else {
                                difficulty = 1;
                            }
                        }
                        if ("Średni".equals(difficultyLevel)) {
                            if ("Trudniejszy".equals(next)) {
                                difficulty = 3;
                            }
                            if ("Łatwiejszy".equals(next)) {
                                difficulty = 1;
                            }
                            if ("Na tym samym poziomie".equals(next)) {
                                difficulty = 2;
                            }
                        }
                        if ("Trudny".equals(difficultyLevel)) {
                            if ("Trudniejszy".equals(next)) {
                                difficulty = 4;
                            }
                            if ("Łatwiejszy".equals(next)) {
                                difficulty = 2;
                            }
                            if ("Na tym samym poziomie".equals(next)) {
                                difficulty = 3;
                            }
                        }
                        if ("Bardzo trudny".equals(difficultyLevel)) {
                            if ("Łatwiejszy".equals(next)) {
                                difficulty = 3;
                            } else {
                                difficulty = 4;
                            }
                        }
                    }
                }
            }
            System.out.println(difficulty);
            return difficulty; 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuMenu screen = new SudokuMenu("username");
            screen.setVisible(true);
        });
    }

    private class BackgroundPanel extends JPanel {
        private final Image background;

        public BackgroundPanel(Image background) {
            this.background = background;
            setLayout(null); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        JButton button = (JButton) c;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(button.getBackground());
        g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 30, 30);

        FontMetrics fm = g2.getFontMetrics();
        Rectangle r = new Rectangle(button.getWidth(), button.getHeight());
        int x = (r.width - fm.stringWidth(button.getText())) / 2;
        int y = (r.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(button.getForeground());
        g2.drawString(button.getText(), x, y);
    }
}
