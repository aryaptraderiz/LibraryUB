package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * TableStyler - memastikan header tabel tampil konsisten di semua Look & Feel.
 * OOP Concept: Encapsulation
 */
public class TableStyler {
    public static void terapkan(JTable table) {
        table.setRowHeight(Theme.ROW_H);
        table.setFont(Theme.FONT_BODY);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(Theme.MERAH_MUDA);
        table.setSelectionForeground(Theme.MERAH);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                            boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                lbl.setOpaque(true);
                lbl.setBackground(Theme.MERAH);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(Theme.FONT_LABEL);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.EMAS),
                    BorderFactory.createEmptyBorder(6, 12, 6, 12)
                ));
                return lbl;
            }
        });
    }
}