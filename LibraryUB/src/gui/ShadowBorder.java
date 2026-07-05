package gui;

import java.awt.*;
import javax.swing.border.AbstractBorder;

/**
 * ShadowBorder - mensimulasikan drop shadow dengan layer rendering.
 * OOP Concept: Inheritance (extends AbstractBorder), Override paintBorder
 */
public class ShadowBorder extends AbstractBorder {

    private static final int SHADOW_SIZE = 4;
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 18);

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 1; i <= SHADOW_SIZE; i++) {
            float alpha = 0.06f - i * 0.01f;
            g2.setColor(new Color(0, 0, 0, Math.max(0, (int)(alpha * 255))));
            g2.setStroke(new BasicStroke(i));
            g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, Theme.RADIUS, Theme.RADIUS);
        }
        g2.setColor(Theme.BORDER);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(x, y, width - SHADOW_SIZE - 1, height - SHADOW_SIZE - 1, Theme.RADIUS, Theme.RADIUS);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, SHADOW_SIZE + 2, SHADOW_SIZE + 2);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(2, 2, SHADOW_SIZE + 2, SHADOW_SIZE + 2);
        return insets;
    }
}