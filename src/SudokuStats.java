import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;



public class SudokuStats extends JFrame {
    private String username;

    public SudokuStats(String username) {
        this.username = username;
        setSize(432, 768);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/background3.png"));
            setContentPane(new BackgroundPanel(backgroundImage));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        setLocationRelativeTo(null);
        setLayout(null);


        JPanel statsPanel = new JPanel();
        statsPanel.setBounds(50, 150, 330, 400);
        statsPanel.setBackground(ColorPalette.BACKGROUND_COLOR); // Półprzezroczyste tło

        // Dodanie statystyk
        addStats(statsPanel);
        add(statsPanel);


        addButton("Wróć do Menu", 595);

    }
    private void addStats(JPanel statsPanel) {
        Map<String, Long> bestTimes = getBestTimesForUser(username);
        
        // Ustawienie BoxLayout na pionowy układ
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS)); 
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Wyrównanie do środka
    
        if (bestTimes.isEmpty()) {
            JLabel noDataLabel = new JLabel("Brak danych dla tego użytkownika.", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noDataLabel.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
            statsPanel.add(noDataLabel);
        } else {
            for (Map.Entry<String, Long> entry : bestTimes.entrySet()) {
                String level = entry.getKey();
                long time = entry.getValue();
                JLabel statLabel = new JLabel(level + ": " + formatTime(time), SwingConstants.CENTER);
                statLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                statLabel.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
                statLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Wyrównanie do środka
                statsPanel.add(statLabel);
    
                // Możesz dodać przerwę między elementami, aby oddzielić statystyki
                statsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
    }    
    

    private Map<String, Long> getBestTimesForUser(String username) {
        Map<String, Long> bestTimes = new HashMap<>();
        try (FileReader reader = new FileReader("game_data.json")) {
            JSONArray dataArray = new JSONArray(new JSONTokener(reader));
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject gameData = dataArray.getJSONObject(i);
                if (!gameData.getString("username").equals(username)) {
                    continue;
                }
                String difficulty = gameData.getString("difficultyLevel");
                long solveTime = gameData.getLong("solveTime"); // Odczyt jako long
                bestTimes.put(difficulty, Math.min(bestTimes.getOrDefault(difficulty, Long.MAX_VALUE), solveTime));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bestTimes;
    }
    

    private String formatTime(long timeMillis) {
        long totalSeconds = timeMillis / 1000; // Zamiana milisekund na całkowitą liczbę sekund
        long minutes = totalSeconds / 60;     // Obliczenie minut
        long seconds = totalSeconds % 60;     // Reszta sekund
        return String.format("%02d:%02d", minutes, seconds); // Formatowanie z dwoma cyframi
    }
    
    private void addButton(String text, int yPosition) {
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
            dispose(); 
            SwingUtilities.invokeLater(() -> {
                SudokuMenu screen = new SudokuMenu(username); 
                screen.setVisible(true); 
            });
        
        });

        add(button);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SudokuStats screen = new SudokuStats("username");
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

class RoundedScrollPane extends JPanel {
    private final JScrollPane scrollPane;
    private final int arcWidth;
    private final int arcHeight;

    public RoundedScrollPane(JScrollPane scrollPane, int arcWidth, int arcHeight, Color backgroundColor, Color borderColor) {
        this.scrollPane = scrollPane;
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;

        // Ustawienie transparentnego tła dla scrollPane
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Usuń domyślne obramowanie

        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setOpaque(false);
        setBorder(BorderFactory.createLineBorder(borderColor, 2)); // Dodaj obramowanie

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Rysowanie zaokrąglonego tła
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);

        // Rysowanie zaokrąglonego obramowania
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcWidth, arcHeight);
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
