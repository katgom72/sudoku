import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        
        addButton("Poziom 1", 255,1);
        addButton("Jak grać?", 325,2);
        addButton("Statystyki", 395,3);
        addButton("Taktyki", 465,4);
        addButton("O aplikacji", 535,5);
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
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    SudokuGameScreen gameScreen = new SudokuGameScreen(username); 
                    gameScreen.setVisible(true); 
                });
            }
            if (numer==2){
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    HowToPlay htpScreen = new HowToPlay(username); 
                    htpScreen.setVisible(true); 
                });
            }
        
        });

        add(button);
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
