package gui;

import java.awt.*;
import javax.swing.JPanel;

/**
 * GradientPanel - JPanel dengan gradient vertikal dari atas ke bawah.
 * Dipakai di header Login, Register, dan sidebar MainFrame.
 * OOP Concept: Inheritance (extends JPanel), Override paintComponent
 */
public class GradientPanel extends JPanel {

    private final Color top;
    private final Color bottom;

    public GradientPanel(Color top, Color bottom) {
        super();
        this.top = top;
        this.bottom = bottom;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}