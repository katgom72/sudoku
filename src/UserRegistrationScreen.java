import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.text.AttributeSet;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.json.JSONObject;
import java.io.FileWriter;





public class UserRegistrationScreen extends JFrame {

    private JTextField usernameField;
    private JTextField pinField;

    private JLabel error1Label;
    private JLabel error2Label;
    private JLabel error4Label;

    private ButtonGroup skillLevelGroup;

    public UserRegistrationScreen() {
        setSize(432, 768); 
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/background1.png"));
            setContentPane(new BackgroundPanel(backgroundImage));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        usernameField = createRoundedTextField(260);
        pinField = createPasswordField(340);
        ((PlainDocument) pinField.getDocument()).setDocumentFilter(new NumberOnlyFilter());

        error1Label = new JLabel("Nazwa użytkownika musi mieć więcej niż 3 znaki.");
        error1Label.setFont(new Font("Arial", Font.PLAIN, 14));
        error1Label.setForeground(Color.RED);
        error1Label.setBounds(65, 290, 550, 30);
        error1Label.setVisible(false); 
        add(error1Label);

        error2Label = new JLabel("PIN musi zawierać dokładnie 4 cyfry.");
        error2Label.setFont(new Font("Arial", Font.PLAIN, 14));
        error2Label.setForeground(Color.RED);
        error2Label.setBounds(100, 370, 550, 30);
        error2Label.setVisible(false);
        add(error2Label);


        error4Label = new JLabel("Wybierz twój poziom umiejętności gry Sudoku");
        error4Label.setFont(new Font("Arial", Font.PLAIN, 14));
        error4Label.setForeground(Color.RED);
        error4Label.setBounds(70, 535, 550, 30);
        error4Label.setVisible(false); 
        add(error4Label);


        addSkillLevelRadioButtons(430); 


        addButton("Zarejestruj", 580);
        addButton2("Zaloguj się", 640);
        
        setLocationRelativeTo(null); 
        setLayout(null); 
    }

    
    private void addSkillLevelRadioButtons(int y) {
        JRadioButton beginnerButton = new JRadioButton("Początkujący");
        JRadioButton intermediateButton = new JRadioButton("Średniozaawansowany");
        JRadioButton advancedButton = new JRadioButton("Zaawansowany");
        JRadioButton expertButton = new JRadioButton("Ekspert");

        beginnerButton.setActionCommand("Początkujący");
        intermediateButton.setActionCommand("Średniozaawansowany");
        advancedButton.setActionCommand("Zaawansowany");
        expertButton.setActionCommand("Ekspert");

    
        beginnerButton.setBounds(100, y, 200, 20);
        intermediateButton.setBounds(100, y + 25, 300, 20);
        advancedButton.setBounds(100, y + 50, 300, 20);
        expertButton.setBounds(100, y + 75, 200, 20);
    
        beginnerButton.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
        beginnerButton.setFont(new Font("Arial", Font.BOLD, 17));
        intermediateButton.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
        intermediateButton.setFont(new Font("Arial", Font.BOLD, 17));
        advancedButton.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
        advancedButton.setFont(new Font("Arial", Font.BOLD, 17));
        expertButton.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
        expertButton.setFont(new Font("Arial", Font.BOLD, 17));
    
        beginnerButton.setFocusPainted(false);
        intermediateButton.setFocusPainted(false);
        advancedButton.setFocusPainted(false);
        expertButton.setFocusPainted(false);
    
        beginnerButton.setContentAreaFilled(false);
        intermediateButton.setContentAreaFilled(false);
        advancedButton.setContentAreaFilled(false);
        expertButton.setContentAreaFilled(false);
    
        Icon unselectedIcon = createRadioButtonIcon(ColorPalette.LIGHT_PINK_COLOR); 
        Icon selectedIcon = createRadioButtonIcon(ColorPalette.LOGO_COLOR); 

        beginnerButton.setIcon(unselectedIcon);
        intermediateButton.setIcon(unselectedIcon);
        advancedButton.setIcon(unselectedIcon);
        expertButton.setIcon(unselectedIcon);
    
        beginnerButton.setSelectedIcon(selectedIcon);
        intermediateButton.setSelectedIcon(selectedIcon);
        advancedButton.setSelectedIcon(selectedIcon);
        expertButton.setSelectedIcon(selectedIcon);
    
        beginnerButton.setOpaque(false);
        intermediateButton.setOpaque(false);
        advancedButton.setOpaque(false);
        expertButton.setOpaque(false);
    
        skillLevelGroup = new ButtonGroup();
        skillLevelGroup.add(beginnerButton);
        skillLevelGroup.add(intermediateButton);
        skillLevelGroup.add(advancedButton);
        skillLevelGroup.add(expertButton);

        add(beginnerButton);
        add(intermediateButton);
        add(advancedButton);
        add(expertButton);
  
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
            String username = usernameField.getText().trim();
            String pin = pinField.getText().trim();
            String skillLevel = null;
        
            if (skillLevelGroup.getSelection() != null) {
                skillLevel = skillLevelGroup.getSelection().getActionCommand();
            }
        
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
        
        
            if (skillLevel == null) {
                error4Label.setVisible(true);
                valid = false;
            } else {
                error4Label.setVisible(false);
            }
        
            if (valid) {
                saveToJSON(username, pin, skillLevel);
            }
        });
        

        

        add(button);
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
        

        

        add(button);
    }

    private void saveToJSON(String username, String pin, String skillLevel) {
        JSONObject userJson = new JSONObject();
        userJson.put("username", username);
        userJson.put("pin", pin);
        userJson.put("skillLevel", skillLevel);
    
        try (FileWriter file = new FileWriter("registration_data.json")) {
            file.write(userJson.toString(4)); 
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd zapisu do pliku JSON.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
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
            UserRegistrationScreen screen = new UserRegistrationScreen();
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
            String text5 = "Twój poziom umiejętności gry:";

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(ColorPalette.TEXT_LIGHT_GREEN);

            g.drawString(text2, 116, 250);
            g.drawString(text1, 190, 330);
            g.drawString(text5, 70, 410);
        }
    }
    
}