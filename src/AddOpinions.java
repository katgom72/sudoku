import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import ui.RoundedButtonUI;


public class AddOpinions extends JFrame {
    private String username;
    private int b;
    private JTextArea opinia;
    private JLabel error1Label;


    public AddOpinions(String username, int b) {
        this.username = username;
        this.b=b;
        setSize(432, 768); 
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/opinie.png"));
            setContentPane(new BackgroundPanel(backgroundImage));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        opinia = createRoundedTextArea(410);

        error1Label = new JLabel("Ups! Wygląda na to, że zapomniałeś wpisać opinię.");
        error1Label.setFont(new Font("Arial", Font.PLAIN, 14));
        error1Label.setForeground(Color.RED);
        error1Label.setBounds(55, 575, 550, 30);
        error1Label.setVisible(false); 
        add(error1Label);

        if(b==1){ //Opinie otwierane z menu
            addButton("Wróć do Menu", 660,1);
        }
        if(b==0){ //Opinie otwierane w trakcie rozgrywki
            addButton("Wróć do gry", 660,1);
        }
        addButton("Dodaj opinie", 600,2);
        
        setLocationRelativeTo(null); 
        setLayout(null); 
    }
    private JTextArea createRoundedTextArea(int y) {
        JTextArea textArea = new JTextArea();
        textArea.setBounds(65, y, 300, 170);
        textArea.setFont(new Font("Arial", Font.PLAIN, 15));
        textArea.setForeground(ColorPalette.TEXT_DARK_GREEN);
        textArea.setBackground(ColorPalette.HIGHLIGHT_COLOR);
        textArea.setOpaque(true);
        textArea.setLineWrap(true); 
        textArea.setWrapStyleWord(true); 

        textArea.setBorder(new RoundedBorder(10, ColorPalette.TEXT_DARK_GREEN));
        add(textArea);
        return textArea;
    }
    

    private void addButton(String text, int yPosition, int a) {
        JButton button = new JButton(text);
        button.setBounds(90, yPosition, 250, 50);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setForeground(ColorPalette.TEXT_DARK_GREEN); 
        button.setBackground(ColorPalette.BUTTON_HIGHLIGHT_COLOR); 
        button.setBorder(BorderFactory.createEmptyBorder());

        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setUI(new RoundedButtonUI());

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
            if (a==1){
                if(b==1){
                    dispose(); 
                    SwingUtilities.invokeLater(() -> {
                        SudokuMenu menuScreen = new SudokuMenu(username); 
                        menuScreen.setVisible(true); 
                    });
                }
                if(b==0){
                    dispose();
                }
                
            }
            if (a==2){
                boolean valid = true;
                String opiniaText = opinia.getText().trim();
                if (opiniaText.isEmpty()) {
                    error1Label.setVisible(true); 
                    valid = false;
                } else {
                    error1Label.setVisible(false); 
                    valid = true;
                }
                if(valid){
                    saveOpinionsData(username,opiniaText);
                    dispose(); 
                    if(b==1){
                        SwingUtilities.invokeLater(() -> {
                            SudokuMenu menuScreen = new SudokuMenu(username); 
                            menuScreen.setVisible(true); 
                        });
                    }
                    
                }
            }
        
        });
        
        add(button);
    }
    private void saveOpinionsData(String username, String opinia) {
        JSONObject opinionData = new JSONObject();
        opinionData.put("username", username);
        opinionData.put("opinia", opinia);   

        File file = new File("opionions_data.json");
        JSONArray opinionsArray = new JSONArray();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JSONTokener tokener = new JSONTokener(reader);
                opinionsArray = new JSONArray(tokener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        opinionsArray.put(opinionData);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(opinionsArray.toString(4));  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddOpinions screen = new AddOpinions("username", 0);
            screen.setVisible(true);
        });
    }
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color borderColor;

        RoundedBorder(int radius, Color borderColor) {
            this.radius = radius;
            this.borderColor = borderColor;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = radius + 1;
            return insets;
        }
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