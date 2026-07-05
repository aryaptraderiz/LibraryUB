package gui;

import exception.FirebaseException;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.*;
import model.Mahasiswa;
import service.AuthService;

/**
 * RegisterFrame - GUI Registrasi Akun Mahasiswa
 * OOP Concept: Encapsulation, Exception Handling
 */
public class RegisterFrame extends JFrame {

    private AuthService authService;
    private JTextField tfNama, tfNim, tfProdi, tfUsername;
    private JPasswordField pfPassword, pfKonfirmasi;
    private JLabel lblStatus;
    private JButton btnDaftar;

    public RegisterFrame(AuthService authService) {
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setTitle("Daftar Akun Mahasiswa — Perpustakaan UB");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 680);
        setMinimumSize(new Dimension(420, 580));
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());

        // Header
        GradientPanel header = new GradientPanel(Theme.MERAH, Theme.MERAH_GELAP);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(28, 32, 24, 32));

        JLabel lblIcon = new JLabel(IconUtil.tambah(36, new Color(255, 255, 255, 180)));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblJudul = new JLabel("Daftar Akun Mahasiswa", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblJudul.setForeground(Color.WHITE);
        lblJudul.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblJudul.setBorder(new EmptyBorder(10, 0, 4, 0));

        JLabel lblSub = new JLabel("Perpustakaan Universitas Bakrie", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(Theme.EMAS);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblIcon); header.add(lblJudul); header.add(lblSub);

        // Form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(24, 32, 16, 32));

        tfNama     = addField(form, "Nama Lengkap");
        tfNim      = addField(form, "NIM");
        tfProdi    = addField(form, "Program Studi");
        tfUsername = addField(form, "Username");

        form.add(makeLabel("Password (min. 6 karakter)"));
        form.add(Box.createRigidArea(new Dimension(0, 5)));
        pfPassword = new JPasswordField();
        styleField(pfPassword); form.add(pfPassword);
        form.add(Box.createRigidArea(new Dimension(0, 14)));

        form.add(makeLabel("Konfirmasi Password"));
        form.add(Box.createRigidArea(new Dimension(0, 5)));
        pfKonfirmasi = new JPasswordField();
        styleField(pfKonfirmasi); form.add(pfKonfirmasi);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(Theme.FONT_SMALL);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(lblStatus);
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        btnDaftar = Theme.buatTombolPrimer("Daftar Sekarang", IconUtil.centang(15, Color.WHITE));
        btnDaftar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnDaftar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDaftar.addActionListener(this::proses);
        form.add(btnDaftar);
        form.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton btnKembali = Theme.buatTombolSekunder("Kembali ke Login", IconUtil.panahKiri(14, Theme.MERAH));
        btnKembali.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnKembali.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnKembali.addActionListener(e -> dispose());
        form.add(btnKembali);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        add(root);
    }

    private JTextField addField(JPanel panel, String labelText) {
        panel.add(makeLabel(labelText));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        JTextField tf = new JTextField();
        styleField(tf); panel.add(tf);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        return tf;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = Theme.buatLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void styleField(JTextField field) {
        field.setFont(Theme.FONT_BODY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, Theme.INPUT_H));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(Theme.inputBorder());
        Theme.styleInput(field);
    }

    private void proses(ActionEvent e) {
        String nama     = tfNama.getText().trim();
        String nim      = tfNim.getText().trim();
        String prodi    = tfProdi.getText().trim();
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());
        String konfirmasi = new String(pfKonfirmasi.getPassword());

        if (!password.equals(konfirmasi)) {
            lblStatus.setForeground(Theme.MERAH_STATUS);
            lblStatus.setText("Password dan konfirmasi tidak sama!");
            return;
        }

        btnDaftar.setEnabled(false);
        lblStatus.setForeground(Theme.TEKS_ABU);
        lblStatus.setText("Menghubungkan ke server...");

        SwingWorker<Mahasiswa, Void> worker = new SwingWorker<>() {
            private Exception error;
            @Override protected Mahasiswa doInBackground() {
                try { return authService.register(nama, username, password, nim, prodi); }
                catch (IllegalArgumentException | FirebaseException ex) { error = ex; return null; }
            }
            @Override protected void done() {
                btnDaftar.setEnabled(true);
                if (error != null) {
                    lblStatus.setForeground(Theme.MERAH_STATUS);
                    lblStatus.setText("<html><div style='width:320px'>" + error.getMessage() + "</div></html>");
                    return;
                }
                lblStatus.setForeground(Theme.HIJAU);
                lblStatus.setText("Registrasi berhasil! Silakan login.");
                JOptionPane.showMessageDialog(RegisterFrame.this,
                    "Akun berhasil didaftarkan!\nSilakan login dengan username dan password yang dibuat.",
                    "Registrasi Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        };
        worker.execute();
    }
}