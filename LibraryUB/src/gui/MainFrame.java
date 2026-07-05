package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import model.*;
import service.AuthService;
import service.DataStore;

/**
 * MainFrame - Dashboard utama dengan sidebar modern.
 * OOP Concept: Composition, Polymorphism
 */
public class MainFrame extends JFrame {

    private Pengguna penggunaSaatIni;
    private AuthService authService;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private String activeCard = "dashboard";

    private BukuPanel bukuPanel;
    private PeminjamanPanel peminjamanPanel;
    private PengembalianPanel pengembalianPanel;
    private AnggotaPanel anggotaPanel;
    private LaporanPanel laporanPanel;

    public MainFrame(Pengguna pengguna, AuthService authService) {
        this.penggunaSaatIni = pengguna;
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setTitle("Perpustakaan UB — " + penggunaSaatIni.getDashboardTitle());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Theme.LATAR);

        bukuPanel          = new BukuPanel(penggunaSaatIni);
        peminjamanPanel    = new PeminjamanPanel(penggunaSaatIni);
        pengembalianPanel  = new PengembalianPanel(penggunaSaatIni);
        anggotaPanel       = new AnggotaPanel(penggunaSaatIni);
        laporanPanel       = new LaporanPanel();

        contentPanel.add(buildDashboard(), "dashboard");
        contentPanel.add(bukuPanel,         "buku");
        contentPanel.add(peminjamanPanel,   "peminjaman");
        contentPanel.add(pengembalianPanel, "pengembalian");
        contentPanel.add(anggotaPanel,      "anggota");
        contentPanel.add(laporanPanel,      "laporan");

        root.add(contentPanel, BorderLayout.CENTER);
        add(root);
    }

    // ── Sidebar ───────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Theme.MERAH_GELAP);
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Header user
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
        userPanel.setBackground(new Color(70, 0, 10));
        userPanel.setBorder(new EmptyBorder(20, 16, 18, 16));

        String inisial = penggunaSaatIni.getNama().length() > 0
            ? String.valueOf(penggunaSaatIni.getNama().charAt(0)).toUpperCase() : "U";
        JLabel lblAvatar = new JLabel(inisial, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.EMAS);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lblAvatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAvatar.setForeground(Theme.MERAH_GELAP);
        lblAvatar.setOpaque(false);
        lblAvatar.setPreferredSize(new Dimension(38, 38));
        lblAvatar.setMinimumSize(new Dimension(38, 38));
        lblAvatar.setMaximumSize(new Dimension(38, 38));

        JPanel namaPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        namaPanel.setOpaque(false);
        namaPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel lblNama = new JLabel("<html><b>" + truncate(penggunaSaatIni.getNama(), 16) + "</b></html>");
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblNama.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel(penggunaSaatIni.getRole().getLabel());
        lblRole.setFont(Theme.FONT_SMALL);
        lblRole.setForeground(new Color(255, 210, 150));

        namaPanel.add(lblNama);
        namaPanel.add(lblRole);
        userPanel.add(lblAvatar);
        userPanel.add(namaPanel);

        // Divider tipis
        JPanel divider = new JPanel();
        divider.setBackground(new Color(120, 0, 20));
        divider.setPreferredSize(new Dimension(220, 1));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Theme.MERAH_GELAP);
        menuPanel.setBorder(new EmptyBorder(10, 8, 10, 8));

        addMenuItem(menuPanel, "Dashboard",          IconUtil.rumah(16, Color.WHITE),       "dashboard");
        addMenuItem(menuPanel, "Manajemen Buku",     IconUtil.buku(16, Color.WHITE),        "buku");
        addMenuItem(menuPanel, "Peminjaman",         IconUtil.panahKeluar(16, Color.WHITE), "peminjaman");
        addMenuItem(menuPanel, "Pengembalian",       IconUtil.panahMasuk(16, Color.WHITE),  "pengembalian");
        if (penggunaSaatIni.getRole() != RoleUser.MAHASISWA) {
            addMenuItem(menuPanel, "Manajemen Anggota", IconUtil.duaOrang(16, Color.WHITE), "anggota");
            addMenuItem(menuPanel, "Laporan",           IconUtil.grafik(16, Color.WHITE),   "laporan");
        }

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(Theme.MERAH_GELAP);
        topSection.add(userPanel);
        topSection.add(divider);
        topSection.add(menuPanel);

        // Logout
        JButton btnLogout = new JButton("Logout", IconUtil.keluar(14, new Color(255, 180, 180)));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(new Color(255, 180, 180));
        btnLogout.setBackground(new Color(55, 0, 8));
        btnLogout.setOpaque(true);
        btnLogout.setContentAreaFilled(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setIconTextGap(8);
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setBorder(new EmptyBorder(12, 16, 14, 16));
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogout.setBackground(new Color(80, 0, 12)); }
            public void mouseExited(MouseEvent e)  { btnLogout.setBackground(new Color(55, 0, 8)); }
        });
        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                authService.logout();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        sidebar.add(topSection, BorderLayout.NORTH);
        sidebar.add(btnLogout, BorderLayout.SOUTH);
        return sidebar;
    }

    private void addMenuItem(JPanel parent, String text, Icon icon, String card) {
        JButton btn = new JButton(text, icon);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(210, 180, 180));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(10);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(Theme.MERAH_GELAP);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Set initial active state
        if (card.equals(activeCard)) {
            btn.setBackground(Theme.MERAH);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!card.equals(activeCard)) {
                    btn.setBackground(Theme.MERAH_HOVER);
                    btn.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!card.equals(activeCard)) {
                    btn.setBackground(Theme.MERAH_GELAP);
                    btn.setForeground(new Color(210, 180, 180));
                }
            }
        });

        btn.addActionListener(e -> {
            activeCard = card;
            if (card.equals("peminjaman"))   peminjamanPanel.refresh();
            if (card.equals("pengembalian")) pengembalianPanel.refresh();
            if (card.equals("anggota"))      anggotaPanel.refresh();
            if (card.equals("laporan"))      laporanPanel.refresh();
            if (card.equals("buku"))         bukuPanel.refresh();
            cardLayout.show(contentPanel, card);

            for (Component c : parent.getComponents()) {
                if (c instanceof JButton) {
                    JButton b = (JButton) c;
                    boolean isActive = b == btn;
                    b.setBackground(isActive ? Theme.MERAH : Theme.MERAH_GELAP);
                    b.setForeground(isActive ? Color.WHITE : new Color(210, 180, 180));
                    b.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
                }
            }
        });

        parent.add(btn);
    }


    // ── Dashboard ─────────────────────────────────────────
    private JPanel buildDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.LATAR);
        panel.setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        // Header
        JPanel headerPanel = Theme.buatPanelHeader(
            "Dashboard",
            "Selamat datang, " + penggunaSaatIni.getNama() + " \u2014 " + penggunaSaatIni.getRole().getLabel(),
            IconUtil.rumah(22, Theme.MERAH)
        );

        // Grid kartu statistik
        JPanel cardsGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        cardsGrid.setBackground(Theme.LATAR);
        cardsGrid.setBorder(new EmptyBorder(0, 0, 0, 0));

        DataStore ds = DataStore.getInstance();
        long totalBuku     = ds.getDaftarBuku().size();
        long totalAnggota  = ds.getDaftarPengguna().stream().filter(p -> p instanceof Mahasiswa).count();
        long pinjamAktif   = ds.getDaftarPeminjaman().stream().filter(p -> p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF).count();

        cardsGrid.add(buildStatCard("Total Buku",        String.valueOf(totalBuku),    "koleksi di perpustakaan",   IconUtil.buku(28, Theme.BIRU),       Theme.BIRU,   Theme.BIRU_MUDA));
        cardsGrid.add(buildStatCard("Anggota Terdaftar", String.valueOf(totalAnggota), "mahasiswa aktif",           IconUtil.duaOrang(28, Theme.HIJAU),  Theme.HIJAU,  Theme.HIJAU_MUDA));
        cardsGrid.add(buildStatCard("Peminjaman Aktif",  String.valueOf(pinjamAktif),  "buku sedang dipinjam",      IconUtil.panahKeluar(28, Theme.MERAH), Theme.MERAH, Theme.MERAH_MUDA));
        cardsGrid.add(buildStatCard("Kategori Buku",     "4",                          "Teknologi, Bisnis, Ekonomi, Hukum", IconUtil.label(28, new Color(0x7C3AED)), new Color(0x7C3AED), new Color(0xF5F3FF)));
        cardsGrid.add(buildStatCard("Role Aktif",        penggunaSaatIni.getRole().getLabel(), "level akses Anda", IconUtil.gembok(28, Theme.EMAS),     Theme.EMAS,   Theme.EMAS_MUDA));
        cardsGrid.add(buildStatCard("Status Sistem",     "Online",                     "terhubung ke Firebase",     IconUtil.statusBulat(28, Theme.HIJAU), Theme.HIJAU, Theme.HIJAU_MUDA));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(cardsGrid,   BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildStatCard(String judul, String nilai, String sub, Icon icon, Color warna, Color bg) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Theme.LATAR_KARTU);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, warna),
            BorderFactory.createCompoundBorder(
                new ShadowBorder(),
                new EmptyBorder(18, 18, 18, 18)
            )
        ));

        RoundedPanel iconBox = new RoundedPanel(bg, 10);
        iconBox.setLayout(new GridBagLayout());
        iconBox.setPreferredSize(new Dimension(52, 52));
        iconBox.add(new JLabel(icon));

        JLabel lblNilai = new JLabel(nilai);
        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblNilai.setForeground(Theme.TEKS_UTAMA);

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(Theme.FONT_H3);
        lblJudul.setForeground(Theme.TEKS_UTAMA);

        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(Theme.FONT_SMALL);
        lblSub.setForeground(Theme.TEKS_ABU);

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        textPanel.setBackground(Theme.LATAR_KARTU);
        textPanel.add(lblNilai);
        textPanel.add(lblJudul);
        textPanel.add(lblSub);

        card.add(iconBox,   BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }
}