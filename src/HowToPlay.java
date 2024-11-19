import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import java.awt.event.ActionEvent;


public class HowToPlay extends JFrame {
    private String username;

    private int currentBackgroundIndex = 0; // Index aktualnego tła
    private BufferedImage[] backgrounds;   // Tablica z obrazkami tła
    private BackgroundPanel backgroundPanel;

    public HowToPlay(String username) {
        this.username = username;
        setSize(432, 768); 
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Załaduj obrazki tła
        try {
            backgrounds = new BufferedImage[]{
                ImageIO.read(new File("resources/9.png")),
                ImageIO.read(new File("resources/10.png")),
                ImageIO.read(new File("resources/11.png"))
            };
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image files not found.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        // Ustaw początkowe tło
        backgroundPanel = new BackgroundPanel(backgrounds[currentBackgroundIndex]);
        setContentPane(backgroundPanel);


        addButton("Dalej", 25);
        addButton2("Menu", 25);
        
        setLocationRelativeTo(null); 
        setLayout(null); 
    }
 
    private void addButton(String text, int yPosition) {
        JButton button = new JButton(text);
        button.setBounds(320, yPosition, 100, 40);
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
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Zmień tło na następne
                currentBackgroundIndex = (currentBackgroundIndex + 1) % backgrounds.length;
                backgroundPanel.setBackgroundImage(backgrounds[currentBackgroundIndex]);
                backgroundPanel.repaint();
            }
        });
        
        add(button);
    }
    private void addButton2(String text, int yPosition) {
        JButton button = new JButton(text);
        button.setBounds(20, yPosition, 100, 40);
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
                SudokuMenu menuScreen = new SudokuMenu(username); 
                menuScreen.setVisible(true); 
            });
        });
        

        add(button);
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HowToPlay screen = new HowToPlay("username");
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
        private Image background;

        public BackgroundPanel(Image background) {
            this.background = background;
            setLayout(null); // Manualne pozycjonowanie elementów
        }

        public void setBackgroundImage(Image background) {
            this.background = background;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
}