package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Theme - Design System sentral untuk seluruh GUI aplikasi.
 * Semua warna, font, ukuran, dan helper styling ada di sini.
 *
 * Inner class (RoundedPanel, GradientPanel, ShadowBorder) telah dipindahkan
 * ke file terpisah agar kompatibel dengan semua versi JDT/IDE.
 *
 * OOP Concept: Encapsulation (konstanta dan helper disembunyikan di class ini)
 */
public class Theme {

    // ── Warna ──────────────────────────────────────────────
    public static final Color MERAH         = new Color(0x8B0014);
    public static final Color MERAH_GELAP   = new Color(0x5A000D);
    public static final Color MERAH_HOVER   = new Color(0xA8001A);
    public static final Color MERAH_MUDA    = new Color(0xFFF0F2);
    public static final Color EMAS          = new Color(0xD4AF37);
    public static final Color EMAS_MUDA     = new Color(0xFFF8E1);
    public static final Color PUTIH         = Color.WHITE;
    public static final Color LATAR         = new Color(0xF7F8FA);
    public static final Color LATAR_KARTU   = Color.WHITE;
    public static final Color BORDER        = new Color(0xE8E8EC);
    public static final Color BORDER_FOKUS  = new Color(0x8B0014);
    public static final Color TEKS_UTAMA    = new Color(0x1A1A2E);
    public static final Color TEKS_ABU      = new Color(0x6B7280);
    public static final Color TEKS_MUTED    = new Color(0x9CA3AF);
    public static final Color HIJAU         = new Color(0x059669);
    public static final Color HIJAU_MUDA    = new Color(0xECFDF5);
    public static final Color BIRU          = new Color(0x1D4ED8);
    public static final Color BIRU_MUDA     = new Color(0xEFF6FF);
    public static final Color MERAH_STATUS  = new Color(0xDC2626);

    // ── Font ───────────────────────────────────────────────
    public static final Font FONT_DISPLAY   = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_H1        = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_H2        = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_H3        = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_LABEL     = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BUTTON    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO      = new Font("Consolas", Font.PLAIN, 12);

    // ── Ukuran ─────────────────────────────────────────────
    public static final int RADIUS          = 12;
    public static final int RADIUS_SM       = 8;
    public static final int PADDING         = 24;
    public static final int PADDING_SM      = 16;
    public static final int SIDEBAR_W       = 230;
    public static final int HEADER_H        = 70;
    public static final int ROW_H           = 32;
    public static final int BTN_H           = 38;
    public static final int INPUT_H         = 40;

    // ── Border Helpers ─────────────────────────────────────
    public static Border kartuBorder() {
        return BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            new EmptyBorder(PADDING_SM, PADDING_SM, PADDING_SM, PADDING_SM)
        );
    }

    public static Border inputBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(8, 12, 8, 12)
        );
    }

    public static Border inputBorderFokus() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MERAH, 2),
            new EmptyBorder(7, 11, 7, 11)
        );
    }

    // ── Component Helpers ──────────────────────────────────

    /** Tombol utama (background merah marun) */
    public static JButton buatTombolPrimer(String text, Icon icon) {
        JButton btn = icon != null ? new JButton(text, icon) : new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(MERAH);
        btn.setForeground(PUTIH);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        if (icon != null) btn.setIconTextGap(8);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(MERAH_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(MERAH); }
        });
        return btn;
    }

    /** Tombol sekunder (background putih, border merah) */
    public static JButton buatTombolSekunder(String text, Icon icon) {
        JButton btn = icon != null ? new JButton(text, icon) : new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(PUTIH);
        btn.setForeground(MERAH);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        if (icon != null) btn.setIconTextGap(8);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MERAH, 1),
            new EmptyBorder(8, 17, 8, 17)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(MERAH_MUDA); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(PUTIH); }
        });
        return btn;
    }

    /** Tombol kecil berwarna (untuk toolbar tabel) */
    public static JButton buatTombolKecil(String text, Icon icon, Color bg) {
        JButton btn = icon != null ? new JButton(text, icon) : new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(PUTIH);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        if (icon != null) btn.setIconTextGap(6);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    /** Label judul panel dengan icon dan subtitle */
    public static JPanel buatPanelHeader(String judul, String subtitle, Icon icon) {
        JPanel panel = new JPanel(new java.awt.BorderLayout(0, 4));
        panel.setBackground(LATAR);
        panel.setBorder(new EmptyBorder(0, 0, PADDING_SM, 0));

        JLabel lblJudul = new JLabel(judul, icon, SwingConstants.LEFT);
        lblJudul.setFont(FONT_H1);
        lblJudul.setForeground(TEKS_UTAMA);
        lblJudul.setIconTextGap(10);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(TEKS_ABU);

        panel.add(lblJudul, java.awt.BorderLayout.NORTH);
        panel.add(lblSub,   java.awt.BorderLayout.CENTER);
        return panel;
    }

    /** Styling field input (JTextField / JPasswordField) */
    public static void styleInput(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBackground(PUTIH);
        field.setForeground(TEKS_UTAMA);
        field.setPreferredSize(new java.awt.Dimension(300, INPUT_H));
        field.setBorder(inputBorder());
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { field.setBorder(inputBorderFokus()); }
            public void focusLost(java.awt.event.FocusEvent e)   { field.setBorder(inputBorder()); }
        });
    }

    /** Styling JComboBox */
    public static void styleCombo(JComboBox<?> combo) {
        combo.setFont(FONT_BODY);
        combo.setBackground(PUTIH);
        combo.setPreferredSize(new java.awt.Dimension(300, INPUT_H));
        combo.setBorder(BorderFactory.createLineBorder(BORDER, 1));
    }

    /** Label form (bold, warna teks utama) */
    public static JLabel buatLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEKS_UTAMA);
        return lbl;
    }

    // ── Kompatibilitas backward ────────────────────────────
    public static final Color WARNA_PRIMER   = MERAH;
    public static final Color WARNA_SEKUNDER = MERAH_HOVER;
    public static final Color WARNA_AKSEN    = EMAS;
    public static final Color WARNA_BG       = LATAR;
    public static final Color WARNA_PUTIH    = PUTIH;
    public static final Font  FONT_JUDUL     = FONT_H1;
    public static final Font  FONT_LABEL_OLD = FONT_BODY;
    public static final Font  FONT_BUTTON_OLD = FONT_BUTTON;
}