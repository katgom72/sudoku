import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.FileReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileWriter;

public class QuestionnaireScreen extends JFrame{
    private String username;
    private int finalId;
    private ButtonGroup q1;
    private ButtonGroup q3;
    private ButtonGroup q4;
    private JSlider difficultySlider;
    private JLabel difficultyLabel;  // Etykieta do wyświetlania wartości suwaka
    private JLabel error1Label;



    public QuestionnaireScreen(String username, int finalId) {
            this.username = username;
            this.finalId = finalId;

            setSize(432, 768);
            setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                BufferedImage backgroundImage = ImageIO.read(new File("resources/7.png"));
                setContentPane(new BackgroundPanel(backgroundImage));
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            error1Label = new JLabel("Odpowiedz na wszytskie pytania");
            error1Label.setFont(new Font("Arial", Font.PLAIN, 14));
            error1Label.setForeground(Color.RED);
            error1Label.setBounds(110, 700, 550, 30);
            error1Label.setVisible(false); 
            add(error1Label);


            addQ1RadioButtons(170,"Męcząca","Satysfakcjonująca","Nudząca");
            addDifficultySlider(310);  
            addQ1RadioButtons(410,"Łatwiejszy","Na tym samym poziomie","Trudniejszy");
            addQ1RadioButtons(540,"Relaksujące, spokojniejsze","Coś pomiędzy","Bardziej intensywne");

            
            addButton("Zapisz odpowiedzi", 640,1);
            
        }
        private void addDifficultySlider(int y) {
            difficultySlider = new JSlider(0, 100);
            difficultySlider.setBounds(60, y, 300, 40);
            difficultySlider.setMajorTickSpacing(20); 
            difficultySlider.setMinorTickSpacing(10);   
            difficultySlider.setPaintTicks(true);      
            difficultySlider.setPaintLabels(true);    
    
            difficultySlider.setFont(new Font("Arial", Font.BOLD, 14));
            difficultySlider.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
    
            // Dodajemy etykietę do wyświetlania wartości suwaka
            difficultyLabel = new JLabel(" " + difficultySlider.getValue());
            difficultyLabel.setBounds(197, y-30, 100, 40);
            difficultyLabel.setFont(new Font("Arial", Font.BOLD, 18));
            difficultyLabel.setForeground(ColorPalette.LOGO_COLOR);
            add(difficultyLabel);
    
            // Wyświetlaj wybraną wartość na suwaku
            difficultySlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = difficultySlider.getValue();
                    difficultyLabel.setText(" "+value);  // Aktualizacja etykiety z wartością
                }
            });
    
            // Dodajemy suwak do panelu
            add(difficultySlider);
        }
    

        private void addButton(String text, int yPosition, int numer) {
            JButton button = new JButton(text);
            button.setBounds(80, yPosition, 260, 60);
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
                String a1=null;
                int difficultyLevel = difficultySlider.getValue();
                String a3=null;
                String a4=null;
                error1Label.setVisible(false);

                if (q1.getSelection() != null) {
                    a1 = q1.getSelection().getActionCommand();
                }
                if (q3.getSelection() != null) {
                    a3 = q3.getSelection().getActionCommand();
                }
                if (q4.getSelection() != null) {
                    a4 = q4.getSelection().getActionCommand();
                }
                boolean valid = true;
                if (a1 == null){
                    error1Label.setVisible(true);
                    valid = false;
                }
                if (a3 == null){
                    error1Label.setVisible(true);
                    valid = false;
                }
                if (a4 == null){
                    error1Label.setVisible(true);
                    valid = false;
                }
                if(valid){
                    saveGameFeedback(username,finalId,a1,difficultyLevel,a3,a4);
                }
            
            });


            add(button);
        }
        public void saveGameFeedback(String username, int finalId, String a1, int a2, String a3, String a4) {


            JSONObject gameFeedbackData = new JSONObject();

            try {
                JSONArray gameFeedbackDataList;
                
                // Odczytaj istniejące dane
                try (FileReader reader = new FileReader("game__feedback_data.json")) {
                    gameFeedbackDataList = new JSONArray(new JSONTokener(reader));
                } catch (IOException e) {
                    gameFeedbackDataList = new JSONArray();
                }

                // Przygotuj nowe dane gry
                gameFeedbackData.put("data_game_id", finalId); // Nowy ID
                gameFeedbackData.put("username", username);
                gameFeedbackData.put("a1", a1);
                gameFeedbackData.put("a2", a2);
                gameFeedbackData.put("a3", a3);
                gameFeedbackData.put("a4", a4);
                

                // Dodaj nowe dane do listy
                gameFeedbackDataList.put(gameFeedbackData);

                // Zapisz zaktualizowaną listę do pliku
                try (FileWriter file = new FileWriter("game__feedback_data.json")) {
                    file.write(gameFeedbackDataList.toString(4));
                }

                System.out.println("Dane gry zostały zapisane pomyślnie.");
                dispose(); 
                    SwingUtilities.invokeLater(() -> {
                        SudokuMenu menuScreen = new SudokuMenu(username); 
                        menuScreen.setVisible(true); 
                    });

            } catch (IOException e) {
                System.out.println("Błąd przy zapisie danych do pliku: " + e.getMessage());
            }
        }
        private void addQ1RadioButtons(int y, String a1, String a2, String a3) {
            JRadioButton a1Button = new JRadioButton(a1);
            JRadioButton a2Button = new JRadioButton(a2);
            JRadioButton a3Button = new JRadioButton(a3);
    
            a1Button.setActionCommand(a1);
            a2Button.setActionCommand(a2);
            a3Button.setActionCommand(a3);
    
        
            a1Button.setBounds(80, y, 500, 20);
            a2Button.setBounds(80, y + 25, 500, 20);
            a3Button.setBounds(80, y + 50, 500, 20);
        
            a1Button.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
            a1Button.setFont(new Font("Arial", Font.BOLD, 17));
            a2Button.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
            a2Button.setFont(new Font("Arial", Font.BOLD, 17));
            a3Button.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
            a3Button.setFont(new Font("Arial", Font.BOLD, 17));
        
            a1Button.setFocusPainted(false);
            a2Button.setFocusPainted(false);
            a3Button.setFocusPainted(false);
        
            a1Button.setContentAreaFilled(false);
            a2Button.setContentAreaFilled(false);
            a3Button.setContentAreaFilled(false);
        
            Icon unselectedIcon = createRadioButtonIcon(ColorPalette.LIGHT_PINK_COLOR); 
            Icon selectedIcon = createRadioButtonIcon(ColorPalette.LOGO_COLOR); 
    
            a1Button.setIcon(unselectedIcon);
            a2Button.setIcon(unselectedIcon);
            a3Button.setIcon(unselectedIcon);
        
            a1Button.setSelectedIcon(selectedIcon);
            a2Button.setSelectedIcon(selectedIcon);
            a3Button.setSelectedIcon(selectedIcon);
        
            a1Button.setOpaque(false);
            a2Button.setOpaque(false);
            a3Button.setOpaque(false);
        
            if(a1=="Męcząca"){
                q1 = new ButtonGroup();
                q1.add(a1Button);
                q1.add(a2Button);
                q1.add(a3Button);
            }
            if(a1=="Łatwiejszy"){
                q3 = new ButtonGroup();
                q3.add(a1Button);
                q3.add(a2Button);
                q3.add(a3Button);
            }
            if(a1=="Relaksujące, spokojniejsze"){
                q4 = new ButtonGroup();
                q4.add(a1Button);
                q4.add(a2Button);
                q4.add(a3Button);
            }
            
    
            add(a1Button);
            add(a2Button);
            add(a3Button);
      
        }
        // Metoda tworząca niestandardową ikonę dla przycisku radiowego
        private Icon createRadioButtonIcon(Color color) {
            int size = 20;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillOval(2, 2, size - 4, size - 4);
            g2d.dispose();
            return new ImageIcon(image);
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                QuestionnaireScreen screen = new QuestionnaireScreen("username",0);
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
                String text1 = "1. Rozgrywka była....";
                String text2 = "2. Oceń trudność tej rozgrywki w skali 0-100";
                String text3 = "3. Chcesz aby nastepny poziom był...";
                String text4 = "4. Jakie aktualnie wyzwanie preferujesz?";

                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.setColor(ColorPalette.TEXT_LIGHT_GREEN);

                g.drawString(text1, 30, 150);
                g.drawString(text2, 30, 280);
                g.drawString(text3, 30, 390);
                g.drawString(text4, 30, 520);

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


    

