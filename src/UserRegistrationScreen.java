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



public class UserRegistrationScreen extends JFrame {

    private JTextField usernameField;
    private JTextField ageField;
    private JTextField countryField;
    private JTextField starting_skill_levelField;
    private JTextField pinField;



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

        usernameField = createRoundedTextField(290);
        pinField = createRoundedTextField(370);
        ageField = createRoundedTextField(450);
        countryField = createRoundedTextField(530);
        starting_skill_levelField = createRoundedTextField(610);
        ((PlainDocument) ageField.getDocument()).setDocumentFilter(new NumberOnlyFilter());
        ((PlainDocument) pinField.getDocument()).setDocumentFilter(new NumberOnlyFilter());

        addButton("Zarejestruj", 665);
        
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

    private void addButton(String text, int yPosition) {
        JButton button = new JButton(text);
        button.setBounds(90, yPosition, 250, 50);
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
        // Sprawdzenie, czy nazwa użytkownika jest dłuższa niż 3 znaki
        button.addActionListener(e -> {
            String username = usernameField.getText().trim(); 
            if (username.length() <= 3) {
                JOptionPane.showMessageDialog(this, "Nazwa użytkownika musi mieć więcej niż 3 znaki.", "Błąd rejestracji", JOptionPane.ERROR_MESSAGE);
            } else {
                // Możesz dodać dalszą logikę rejestracji tutaj, jeśli nazwa jest poprawna
                JOptionPane.showMessageDialog(this, "Rejestracja udana!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
                // Możesz zamknąć okno po udanej rejestracji, np.:
                // dispose();
            }
        });

        add(button);
    }


    private class NumberOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string.matches("\\d+")) { // Akceptuj tylko cyfry
                super.insertString(fb, offset, string, attr);
            }
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text.matches("\\d+")) { // Akceptuj tylko cyfry
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
    // Klasa do tworzenia zaokrąglonych obramowań
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

            // Obramowanie
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
            String text3 = "Wiek:";
            String text4 = "Kraj:";
            String text5 = "Twój poziom umiejętności gry:";

            int y = 340; 
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(ColorPalette.TEXT_LIGHT_GREEN);

            g.drawString(text2, 116, 280);
            g.drawString(text1, 190, 360);
            g.drawString(text3, 185, 440);
            g.drawString(text4, 185, 520);
            g.drawString(text5, 70, 600); 
        }
    }
}
