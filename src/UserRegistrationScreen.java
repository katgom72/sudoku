import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UserRegistrationScreen extends JFrame {

    // Pole tekstowe do wprowadzania nazwy użytkownika
    private JTextField usernameField;

    public UserRegistrationScreen() {
        // Ustawienia główne okna
        setSize(432, 768); 
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Ustawienie tła
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/background1.png"));
            setContentPane(new BackgroundPanel(backgroundImage));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Inicjalizacja pola tekstowego
        usernameField = new JTextField();
        usernameField.setBounds(100, 400, 230, 30); // Ustawienia pozycji i rozmiaru pola tekstowego
        usernameField.setFont(new Font("Arial", Font.PLAIN, 15)); // Ustawienie czcionki
        usernameField.setForeground(ColorPalette.TEXT_BLACK); // Kolor tekstu w polu tekstowym
        usernameField.setBackground(ColorPalette.WHITE_COLOR); // Kolor tła w polu tekstowym
        usernameField.setBorder(BorderFactory.createLineBorder(ColorPalette.ICON_BORDER_COLOR)); // Obramowanie pola tekstowego

        // Dodanie pola tekstowego do panelu
        ((BackgroundPanel) getContentPane()).add(usernameField); // Dodajemy do panelu, który rysuje tło
        
        setLocationRelativeTo(null); 
        setLayout(null); // Ustawienie układu na null, aby można było ustawić pozycje ręcznie
    }

    // Główna metoda uruchomieniowa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserRegistrationScreen screen = new UserRegistrationScreen();
            screen.setVisible(true);
        });
    }

    // Klasa wewnętrzna do renderowania tła
    private class BackgroundPanel extends JPanel {
        private final Image background;

        public BackgroundPanel(Image background) {
            this.background = background;
            setLayout(null); // Ustawienie układu na null, aby można było ręcznie dodawać komponenty
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            // Ustawienia czcionki
            g.setFont(new Font("Arial", Font.BOLD, 35)); 
            g.setColor(ColorPalette.TEXT_DARK_GREEN);

            // Rysowanie napisu
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
