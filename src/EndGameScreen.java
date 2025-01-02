import javax.swing.*;

import ui.RoundedButtonUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EndGameScreen extends JFrame{
    private String username;
    private String difficultyLevelText;
    @SuppressWarnings("unused")
    private long solveTime;
    @SuppressWarnings("unused")
    private int errorCount;
    private int finalId;
    public EndGameScreen(String username, String difficultyLevelText, long solveTime,int errorCount, int finalId) {
            this.username = username;
            this.difficultyLevelText = difficultyLevelText;
            this.solveTime = solveTime;
            this.errorCount = errorCount;
            this.finalId = finalId;
            System.out.println(difficultyLevelText);
            System.out.println(difficultyLevelText);

            setSize(432, 768);
            setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            long minutes = solveTime / 60000;
            long seconds = (solveTime % 60000) / 1000;


            try {
                BufferedImage backgroundImage = ImageIO.read(new File("resources/background5.png"));
                setContentPane(new BackgroundPanel(backgroundImage));
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            JLabel levelLabel = new JLabel("Ukończyłeś poziom: ");
            JLabel levelLabel2 = new JLabel(difficultyLevelText);

            JLabel timeLabel = new JLabel("Czas:");
            JLabel timeLabel2 = new JLabel(String.format("%02d:%02d", minutes, seconds));

            JLabel errorsLabel = new JLabel("Ilość błędów:");
            JLabel errorsLabel2 = new JLabel(Integer.toString(errorCount));


            levelLabel.setFont(new Font("Arial", Font.BOLD, 22));
            levelLabel.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
            levelLabel.setBounds(111, 340, 300, 30);
            add(levelLabel);

            levelLabel2.setFont(new Font("Arial", Font.BOLD, 25));
            levelLabel2.setForeground(ColorPalette.LOGO_COLOR);
            System.out.println(difficultyLevelText=="Średni");
            if (difficultyLevelText.equals("Łatwy")){
                System.out.println("i1");
                levelLabel2.setBounds(180, 370, 300, 40);
            }else if(difficultyLevelText.equals("Średni") || difficultyLevelText.equals("Trudny")){
                System.out.println("i2");

                levelLabel2.setBounds(175, 370, 300, 40);
            }else if(difficultyLevelText.equals("Bardzo trudny")){

                levelLabel2.setBounds(130, 370, 300, 40);
            }else{
                levelLabel2.setBounds(180, 370, 300, 40);

            }
            add(levelLabel2);

            timeLabel.setFont(new Font("Arial", Font.BOLD, 22));
            timeLabel.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
            timeLabel.setBounds(188, 405, 300, 30);
            add(timeLabel);

            timeLabel2.setFont(new Font("Arial", Font.BOLD, 25));
            timeLabel2.setForeground(ColorPalette.LOGO_COLOR);
            timeLabel2.setBounds(185, 430, 300, 40);
            add(timeLabel2);

            errorsLabel.setFont(new Font("Arial", Font.BOLD, 22));
            errorsLabel.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
            errorsLabel.setBounds(145, 465, 300, 30);
            add(errorsLabel);

            errorsLabel2.setFont(new Font("Arial", Font.BOLD, 28));
            errorsLabel2.setForeground(ColorPalette.LOGO_COLOR);
            errorsLabel2.setBounds(206, 490, 300, 40);
            add(errorsLabel2);


            setLocationRelativeTo(null);
            setLayout(null);
            
            addButton("Odpowiedz na pytania", 550,1);
            
        }

        private void addButton(String text, int yPosition, int numer) {
            JButton button = new JButton(text);
            button.setBounds(86, yPosition, 260, 60);
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
                    dispose(); 
                    SwingUtilities.invokeLater(() -> {
                        QuestionnaireScreen screen = new QuestionnaireScreen(username, finalId,difficultyLevelText); 
                        screen.setVisible(true); 
                    });
                }
            
            });

            add(button);
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                EndGameScreen screen = new EndGameScreen("username","Łatwy",000000,0,0);
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

    

    

