import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UserRegistrationScreen extends JFrame {

    private JTextField usernameField;

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

        usernameField = new JTextField();
        usernameField.setBounds(100, 400, 230, 30); 
        usernameField.setFont(new Font("Arial", Font.PLAIN, 15)); 
        usernameField.setForeground(ColorPalette.TEXT_BLACK);
        usernameField.setBackground(ColorPalette.WHITE_COLOR); 
        usernameField.setBorder(BorderFactory.createLineBorder(ColorPalette.ICON_BORDER_COLOR)); 

        ((BackgroundPanel) getContentPane()).add(usernameField);
        
        setLocationRelativeTo(null); 
        setLayout(null); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserRegistrationScreen screen = new UserRegistrationScreen();
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
            g.setFont(new Font("Arial", Font.BOLD, 35)); 
            g.setColor(ColorPalette.TEXT_DARK_GREEN);

            String text1 = "Poznajmy się";
            String text2 = "Nazwa użytkownika:";
            String text3 = "Wiek:";
            String text4 = "Kraj:";
            String text5 = "Określ swój poziom umiejętności gry w Sudoku";

            int x = 100; 
            int y = 340; 
            g.drawString(text1, x, y);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(ColorPalette.TEXT_LIGHT_GREEN);

            g.drawString(text2, x + 16, y + 50);
            g.drawString(text3, x + 85, y + 120); 
        }
    }
}
