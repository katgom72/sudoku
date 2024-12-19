import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import ui.RoundedButtonUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javax.imageio.ImageIO;



public class SudokuStats extends JFrame {
    private String username;

    public SudokuStats(String username) {
        this.username = username;
        setSize(432, 768);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            BufferedImage backgroundImage = ImageIO.read(new File("resources/s.png"));
            setContentPane(new BackgroundPanel(backgroundImage));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        setLocationRelativeTo(null);
        setLayout(null);


        JPanel statsPanel = new JPanel();
        statsPanel.setBounds(15, 80, 400, 550);
        statsPanel.setOpaque(false);

        addStats(statsPanel);
        add(statsPanel);

        addButton("Wróć do Menu", 660);

    }
    private void addStats(JPanel statsPanel) {
        Map<String, Long> bestTimes = getBestTimesForUser(username);
        Map<String, Long> averageTimes = getAverageTimesForUser(username);
        Map<String, Integer> gamesCount = getGamesCountForUser(username);
        Map<String, Integer> errorlessGamesCount = getErrorlessGamesCountForUser(username);
    
        statsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        // Nagłówki sekcji "Rozgrywki"
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 5, 5, 5);

        statsPanel.add(createHeaderLabel("Rozgrywki"), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        statsPanel.add(createSubHeaderLabel("Poziom"), gbc);
    
        gbc.gridx = 1;
        statsPanel.add(createSubHeaderLabel("Wszystkie"), gbc);
    
        gbc.gridx = 2;
        statsPanel.add(createSubHeaderLabel("Bez błędów"), gbc);
    
        // Dane sekcji "Rozgrywki"
        List<String> levels = Arrays.asList("Łatwy", "Średni", "Trudny", "Bardzo trudny");
        for (int i = 0; i < levels.size(); i++) {
            String level = levels.get(i);
    
            gbc.gridx = 0;
            gbc.gridy++;
            statsPanel.add(createDataLabel(level), gbc);
    
            gbc.gridx = 1;
            int allGames = gamesCount.getOrDefault(level, 0);
            statsPanel.add(createDataLabel(String.valueOf(allGames)), gbc);
    
            gbc.gridx = 2;
            int errorlessGames = errorlessGamesCount.getOrDefault(level, 0);
            statsPanel.add(createDataLabel(String.valueOf(errorlessGames)), gbc);
        }

    
        // Nagłówki sekcji "Czas"
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(15, 5, 5, 5); 
        statsPanel.add(createHeaderLabel("Czas"), gbc);
    
        gbc.gridx = 0;
        gbc.gridy++;
        statsPanel.add(createSubHeaderLabel("Poziom"), gbc);
    
        gbc.gridx = 1;
        statsPanel.add(createSubHeaderLabel("Najlepszy"), gbc);
    
        gbc.gridx = 2;
        statsPanel.add(createSubHeaderLabel("Średni"), gbc);
    
        // Dane sekcji "Czas"
        for (int i = 0; i < levels.size(); i++) {
            String level = levels.get(i);
    
            gbc.gridx = 0;
            gbc.gridy++;
            statsPanel.add(createDataLabel(level), gbc);
    
            gbc.gridx = 1;
            String bestTime = bestTimes.containsKey(level) ? formatTime(bestTimes.get(level)) : "-";
            statsPanel.add(createDataLabel(bestTime), gbc);
    
            gbc.gridx = 2;
            String avgTime = averageTimes.containsKey(level) ? formatTime(averageTimes.get(level)) : "-";
            statsPanel.add(createDataLabel(avgTime), gbc);
        }
    }
    
    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setForeground(ColorPalette.TEXT_DARK_GREEN);
        return label;
    }
    
    private JLabel createSubHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
        return label;
    }
    
    private JLabel createDataLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 17));
        label.setForeground(ColorPalette.TEXT_LIGHT_GREEN);
        return label;
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
                long solveTime = gameData.getLong("solveTime"); 
                bestTimes.put(difficulty, Math.min(bestTimes.getOrDefault(difficulty, Long.MAX_VALUE), solveTime));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bestTimes;
    }
    private Map<String, Long> getAverageTimesForUser(String username) {
        Map<String, Long> averageTimes = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>(); 
    
        try (FileReader reader = new FileReader("game_data.json")) {
            JSONArray dataArray = new JSONArray(new JSONTokener(reader));
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject gameData = dataArray.getJSONObject(i);
                if (!gameData.getString("username").equals(username)) {
                    continue;
                }
                String difficulty = gameData.getString("difficultyLevel");
                long solveTime = gameData.getLong("solveTime");
                
                averageTimes.put(difficulty, averageTimes.getOrDefault(difficulty, 0L) + solveTime);
                counts.put(difficulty, counts.getOrDefault(difficulty, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        for (Map.Entry<String, Long> entry : averageTimes.entrySet()) {
            String difficulty = entry.getKey();
            long totalSolveTime = entry.getValue();
            int count = counts.getOrDefault(difficulty, 1);
            averageTimes.put(difficulty, totalSolveTime / count); 
        }
    
        return averageTimes;
    }
    private Map<String, Integer> getGamesCountForUser(String username) {
        Map<String, Integer> gamesCount = new HashMap<>();
    
        try (FileReader reader = new FileReader("game_data.json")) {
            JSONArray dataArray = new JSONArray(new JSONTokener(reader));
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject gameData = dataArray.getJSONObject(i);
                if (!gameData.getString("username").equals(username)) {
                    continue;
                }
                String difficulty = gameData.getString("difficultyLevel");
                gamesCount.put(difficulty, gamesCount.getOrDefault(difficulty, 0) + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return gamesCount;
    }
    private Map<String, Integer> getErrorlessGamesCountForUser(String username) {
        Map<String, Integer> errorlessGamesCount = new HashMap<>();
    
        try (FileReader reader = new FileReader("game_data.json")) {
            JSONArray dataArray = new JSONArray(new JSONTokener(reader));
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject gameData = dataArray.getJSONObject(i);
                if (!gameData.getString("username").equals(username)) {
                    continue;
                }
                String difficulty = gameData.getString("difficultyLevel");
                int errorsCount = gameData.getInt("errorCount"); 
    
                if (errorsCount == 0) {
                    errorlessGamesCount.put(difficulty, errorlessGamesCount.getOrDefault(difficulty, 0) + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return errorlessGamesCount;
    }
    

    private String formatTime(long timeMillis) {
        long totalSeconds = timeMillis / 1000; 
        long minutes = totalSeconds / 60;     
        long seconds = totalSeconds % 60;     
        return String.format("%02d:%02d", minutes, seconds); 
    }
    
    private void addButton(String text, int yPosition) {
        JButton button = new JButton(text);
        button.setBounds(66, yPosition, 300, 60);
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



