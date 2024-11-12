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
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


public class UserLoginScreen extends JFrame {

    private JTextField usernameField;
    private JTextField pinField;

    private JLabel error1Label;
    private JLabel error2Label;


    public UserLoginScreen() {
        setSize(432, 768); 
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/background4.png"));
            setContentPane(new BackgroundPanel(backgroundImage));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        usernameField = createRoundedTextField(390);
        pinField = createPasswordField(470);
        ((PlainDocument) pinField.getDocument()).setDocumentFilter(new NumberOnlyFilter());

        error1Label = new JLabel("Nazwa użytkownika niepoprawna");
        error1Label.setFont(new Font("Arial", Font.PLAIN, 14));
        error1Label.setForeground(Color.RED);
        error1Label.setBounds(110, 420, 550, 30);
        error1Label.setVisible(false); 
        add(error1Label);

        error2Label = new JLabel("PIN niepoprawny");
        error2Label.setFont(new Font("Arial", Font.PLAIN, 14));
        error2Label.setForeground(Color.RED);
        error2Label.setBounds(160, 500, 550, 30);
        error2Label.setVisible(false);
        add(error2Label);


        addButton2("Zarejestruj się", 590);
        addButton("Zaloguj", 530);
        
        setLocationRelativeTo(null); 
        setLayout(null); 
    }
 

    private JTextField createRoundedTextField(int y) {
        JTextField textField = new JTextField();
        textField.setBounds(100, y, 230, 38);
        textField.setFont(new Font("Arial", Font.PLAIN, 15));
        textField.setForeground(ColorPalette.TEXT_DARK_GREEN);
        textField.setBackground(ColorPalette.HIGHLIGHT_COLOR);
        textField.setOpaque(true);
        
        textField.setBorder(new RoundedBorder(10, ColorPalette.TEXT_DARK_GREEN));
        add(textField);
        return textField;
    }
    private JPasswordField createPasswordField(int y) {  
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(100, y, 230, 38);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordField.setForeground(ColorPalette.TEXT_DARK_GREEN);
        passwordField.setBackground(ColorPalette.HIGHLIGHT_COLOR);
        passwordField.setOpaque(true);

        passwordField.setBorder(new RoundedBorder(10, ColorPalette.TEXT_DARK_GREEN));
        add(passwordField);
        return passwordField;
    }

    private void addButton(String text, int yPosition) {
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
            String username = usernameField.getText().trim();
            String pin = pinField.getText().trim();
            boolean valid = true;
        
            if (username.length() <= 3) {
                error1Label.setVisible(true);
                valid = false;
            } else {
                error1Label.setVisible(false);
            }
        
            if (!pin.matches("\\d{4}")) {
                error2Label.setVisible(true);
                valid = false;
            } else {
                error2Label.setVisible(false);
            }
            if (valid) {
                if (verifyPin(username, pin)){
                    dispose(); 
                    SwingUtilities.invokeLater(() -> {
                        SudokuMenu menuScreen = new SudokuMenu(username); 
                        menuScreen.setVisible(true); 
                    });
                }else{
                    error2Label.setVisible(true);
                    error1Label.setVisible(true);
                }
            }
        
        });
        
        add(button);
    }
    public boolean verifyPin(String username, String inputPin) {
        try (FileReader reader = new FileReader("registration_data.json")) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray usersArray = new JSONArray(tokener);

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                String storedUsername = user.getString("username");
                String storedPinHash = user.getString("pin");

                if (storedUsername.equals(username)) {
                    String inputPinHash = EncryptionUtils.hashPin(inputPin);
                    return storedPinHash.equals(inputPinHash);  
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addButton2(String text, int yPosition) {
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
            dispose(); 
            SwingUtilities.invokeLater(() -> {
                UserRegistrationScreen registrationScreen = new UserRegistrationScreen(); 
                registrationScreen.setVisible(true); 
            });
        });
        

        add(button);
    }
    


    private class NumberOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string.matches("\\d+")) { 
                super.insertString(fb, offset, string, attr);
            }
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text.matches("\\d+")) { 
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserLoginScreen screen = new UserLoginScreen();
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

            String text1 = "PIN:";
            String text2 = "Nazwa użytkownika:";

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(ColorPalette.TEXT_LIGHT_GREEN);

            g.drawString(text2, 116, 380);
            g.drawString(text1, 190, 460);
        }
    }
    
}