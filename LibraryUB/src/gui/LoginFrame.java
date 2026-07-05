package gui;

import exception.FirebaseException;
import exception.PenggunaTidakDitemukanException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import model.Pengguna;
import model.RoleUser;
import service.AuthService;
import service.DataStore;

/**
 * LoginFrame - GUI Login yang diperbarui dengan design system Theme.
 * OOP Concept: Encapsulation, Event Handling, Exception Handling
 */
public class LoginFrame extends JFrame {

    // Kompatibilitas backward untuk komponen lain yang masih referensi konstanta lama
    public static final Color WARNA_PRIMER   = Theme.MERAH;
    public static final Color WARNA_SEKUNDER = Theme.MERAH_HOVER;
    public static final Color WARNA_AKSEN    = Theme.EMAS;
    public static final Color WARNA_BG       = Theme.LATAR;
    public static final Color WARNA_PUTIH    = Theme.PUTIH;
    public static final Font  FONT_JUDUL     = Theme.FONT_H1;
    public static final Font  FONT_LABEL     = Theme.FONT_BODY;
    public static final Font  FONT_BUTTON    = Theme.FONT_BUTTON;

    private AuthService authService;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JComboBox<RoleUser> cbRole;
    private JLabel lblStatus;
    private JButton btnLogin;

    public LoginFrame() {
        this.authService = new AuthService();
        initUI();
        muatDataDariFirestore();
    }

    private void initUI() {
        setTitle("Sistem Perpustakaan — Universitas Bakrie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setMinimumSize(new Dimension(780, 540));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new GridLayout(1, 2));

        // ── Panel Kiri: Ilustrasi & Branding ──────────────
        GradientPanel leftPanel = new GradientPanel(Theme.MERAH, Theme.MERAH_GELAP);
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setPreferredSize(new Dimension(400, 620));

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);
        leftContent.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Logo icon
        JLabel lblIcon = new JLabel(IconUtil.buku(72, new Color(255, 255, 255, 200)));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setBorder(new EmptyBorder(0, 0, 24, 0));

        // Judul
        JLabel lblNama = new JLabel("<html><div style='text-align:center;'>Perpustakaan<br>Universitas Bakrie</div></html>", SwingConstants.CENTER);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblNama.setForeground(Color.WHITE);
        lblNama.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divider emas
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(120, 2));
        sep.setForeground(Theme.EMAS);
        sep.setBackground(Theme.EMAS);
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Library Management System", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSub.setForeground(new Color(255, 220, 150));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setBorder(new EmptyBorder(10, 0, 32, 0));

        leftContent.add(lblIcon, 0);
        leftContent.add(lblNama, 1);
        leftContent.add(Box.createRigidArea(new Dimension(0, 12)), 2);
        leftContent.add(sep, 3);
        leftContent.add(lblSub, 4);

        leftPanel.add(leftContent);

        // Footer kiri
        JLabel lblFooter = new JLabel("Teknik Informatika • 2024/2025", SwingConstants.CENTER);
        lblFooter.setFont(Theme.FONT_SMALL);
        lblFooter.setForeground(new Color(255, 200, 150, 160));
        leftPanel.add(lblFooter, new GridBagConstraints() {{ gridy = 1; insets = new Insets(0, 0, 20, 0); }});

        // ── Panel Kanan: Form Login ────────────────────────
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Theme.PUTIH);

        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBackground(Theme.PUTIH);
        formWrapper.setBorder(new EmptyBorder(0, 48, 0, 48));
        formWrapper.setMaximumSize(new Dimension(380, Integer.MAX_VALUE));

        // Heading form
        JLabel lblWelcome = new JLabel("Selamat Datang");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(Theme.TEKS_UTAMA);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel("Masuk ke sistem perpustakaan");
        lblDesc.setFont(Theme.FONT_BODY);
        lblDesc.setForeground(Theme.TEKS_ABU);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDesc.setBorder(new EmptyBorder(4, 0, 28, 0));

        formWrapper.add(lblWelcome);
        formWrapper.add(lblDesc);

        // Login sebagai
        formWrapper.add(makeFormLabel("Login sebagai"));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 6)));
        cbRole = new JComboBox<>(RoleUser.values());
        cbRole.setFont(Theme.FONT_BODY);
        cbRole.setBackground(Theme.PUTIH);
        cbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.INPUT_H));
        cbRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbRole.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        formWrapper.add(cbRole);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 16)));

        // Username
        formWrapper.add(makeFormLabel("Username"));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 6)));
        tfUsername = new JTextField();
        Theme.styleInput(tfUsername);
        tfUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.INPUT_H));
        tfUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(tfUsername);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 16)));

        // Password
        formWrapper.add(makeFormLabel("Password"));
        formWrapper.add(Box.createRigidArea(new Dimension(0, 6)));
        pfPassword = new JPasswordField();
        Theme.styleInput(pfPassword);
        pfPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.INPUT_H));
        pfPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        pfPassword.addActionListener(e -> btnLogin.doClick());
        formWrapper.add(pfPassword);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 12)));

        // Status
        lblStatus = new JLabel("\u29D7  Menghubungkan ke server...", SwingConstants.CENTER);
        lblStatus.setFont(Theme.FONT_SMALL);
        lblStatus.setForeground(Theme.TEKS_ABU);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(lblStatus);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 16)));

        // Tombol Masuk
        btnLogin = Theme.buatTombolPrimer("Masuk", IconUtil.gembok(16, Color.WHITE));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setEnabled(false);
        btnLogin.addActionListener(this::prosesLogin);
        formWrapper.add(btnLogin);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 10)));

        // Tombol Register
        JButton btnRegister = Theme.buatTombolSekunder("Daftar Akun Mahasiswa Baru", IconUtil.tambah(14, Theme.MERAH));
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.addActionListener(e -> new RegisterFrame(authService).setVisible(true));
        formWrapper.add(btnRegister);
        formWrapper.add(Box.createRigidArea(new Dimension(0, 20)));

        // Hint demo
        JLabel lblHint = new JLabel("<html><center><font color='#9CA3AF' size='2'>Demo — admin/admin123 &nbsp;|&nbsp; petugas/petugas123 &nbsp;|&nbsp; arya/mhs123</font></center></html>");
        lblHint.setFont(Theme.FONT_SMALL);
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        formWrapper.add(lblHint);

        rightPanel.add(formWrapper);

        root.add(leftPanel);
        root.add(rightPanel);
        add(root);
    }

    private JLabel makeFormLabel(String text) {
        JLabel lbl = Theme.buatLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void muatDataDariFirestore() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private FirebaseException error;
            @Override protected Void doInBackground() {
                try { DataStore.getInstance().loadFromFirestore(); }
                catch (FirebaseException ex) { error = ex; }
                return null;
            }
            @Override protected void done() {
                if (error != null) {
                    lblStatus.setForeground(Theme.MERAH_STATUS);
                    lblStatus.setText("\u2717  Gagal terhubung. Periksa koneksi internet.");
                    int retry = JOptionPane.showConfirmDialog(LoginFrame.this,
                        "Tidak dapat memuat data dari server:\n" + error.getMessage() + "\n\nCoba lagi?",
                        "Koneksi Gagal", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (retry == JOptionPane.YES_OPTION) {
                        lblStatus.setForeground(Theme.TEKS_ABU);
                        lblStatus.setText("\u29D7  Menghubungkan ke server...");
                        muatDataDariFirestore();
                    }
                    return;
                }
                lblStatus.setForeground(Theme.HIJAU);
                lblStatus.setText("\u2713  Terhubung ke server. Silakan login.");
                btnLogin.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void prosesLogin(ActionEvent e) {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());
        RoleUser role = (RoleUser) cbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setForeground(Theme.MERAH_STATUS);
            lblStatus.setText("\u26a0  Username dan password harus diisi!");
            return;
        }

        try {
            Pengguna pengguna = authService.login(username, password, role);
            lblStatus.setForeground(Theme.HIJAU);
            lblStatus.setText("\u2713  Login berhasil! Memuat dashboard...");
            Timer timer = new Timer(700, ev -> {
                dispose();
                new MainFrame(pengguna, authService).setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        } catch (PenggunaTidakDitemukanException ex) {
            lblStatus.setForeground(Theme.MERAH_STATUS);
            lblStatus.setText("\u2717  " + ex.getMessage());
            pfPassword.setText("");
        }
    }

    // Helper static untuk kompabilitas backward (komponen lain yang masih manggil LoginFrame.createButton)
    public static JButton createButton(String text, Color bg) {
        return Theme.buatTombolKecil(text, null, bg);
    }

    public static JButton createButton(String text, Icon icon, Color bg) {
        return Theme.buatTombolKecil(text, icon, bg);
    }
}