
import javax.swing.*;
import java.awt.*;

public class RoundedButtonUI extends javax.swing.plaf.basic.BasicButtonUI {
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