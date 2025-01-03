import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
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

import ui.RoundedButtonUI;

import java.awt.event.ActionEvent;


public class HowToPlay extends JFrame {
    private String username;

    private int currentBackgroundIndex = 0; 
    private BufferedImage[] backgrounds;  
    private BackgroundPanel backgroundPanel;

    public HowToPlay(String username) {
        this.username = username;
        setSize(432, 768); 
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            backgrounds = new BufferedImage[]{
                ImageIO.read(new File("resources/21.png")),
                ImageIO.read(new File("resources/22.png")),
                ImageIO.read(new File("resources/23.png")),
                ImageIO.read(new File("resources/24.png")),
                ImageIO.read(new File("resources/25.png")),
                ImageIO.read(new File("resources/26.png")),
                ImageIO.read(new File("resources/27.png")),
                ImageIO.read(new File("resources/28.png")),
                ImageIO.read(new File("resources/29.png")),
                ImageIO.read(new File("resources/30.png")),
                ImageIO.read(new File("resources/31.png")),
                ImageIO.read(new File("resources/32.png")),
                ImageIO.read(new File("resources/33.png")),
                ImageIO.read(new File("resources/34.png"))
            };
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Image files not found.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        backgroundPanel = new BackgroundPanel(backgrounds[currentBackgroundIndex]);
        setContentPane(backgroundPanel);


        addButton("Wstecz", 20,1);
        addButton("Dalej", 20,3);

        addButton("Menu", 20,2);
        
        setLocationRelativeTo(null); 
        setLayout(null); 
    }
 
    private void addButton(String text, int yPosition,int a) {
        JButton button = new JButton(text);
        if(a==1){
            button.setBounds(18, yPosition, 120, 40);
        }else if(a==2){
            button.setBounds(161, yPosition, 110, 40);
        }else{
            button.setBounds(294, yPosition, 120, 40);
        }
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
        if (a==1){
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentBackgroundIndex = (currentBackgroundIndex - 1 + backgrounds.length) % backgrounds.length;
                    backgroundPanel.setBackgroundImage(backgrounds[currentBackgroundIndex]);
                    backgroundPanel.repaint();
                }
            });
            
        }else if(a==2){
            button.addActionListener(e -> {
                dispose(); 
                SwingUtilities.invokeLater(() -> {
                    SudokuMenu menuScreen = new SudokuMenu(username); 
                    menuScreen.setVisible(true); 
                });
            });
        }else{
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentBackgroundIndex = (currentBackgroundIndex + 1) % backgrounds.length;
                    backgroundPanel.setBackgroundImage(backgrounds[currentBackgroundIndex]);
                    backgroundPanel.repaint();
                }
            });
        }
        
        
        
        add(button);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HowToPlay screen = new HowToPlay("username");
            screen.setVisible(true);
        });
    }
    


    private class BackgroundPanel extends JPanel {
        private Image background;

        public BackgroundPanel(Image background) {
            this.background = background;
            setLayout(null); 
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