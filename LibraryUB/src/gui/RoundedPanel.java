package gui;

import java.awt.*;
import javax.swing.JPanel;

/**
 * RoundedPanel - JPanel dengan sudut bulat dan background warna.
 * OOP Concept: Inheritance (extends JPanel), Override paintComponent
 */
public class RoundedPanel extends JPanel {

    private final int radius;
    private final Color bg;

    public RoundedPanel(Color bg, int radius) {
        super();
        this.bg = bg;
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}