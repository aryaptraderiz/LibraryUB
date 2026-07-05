package gui;

import model.Peminjaman;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * StrukDialog - Dialog struk peminjaman buku.
 *
 * Muncul otomatis setiap kali peminjaman buku berhasil.
 * Menampilkan detail peminjaman dalam format struk yang rapi,
 * dan menyediakan opsi untuk menyimpan struk ke file .txt.
 *
 * OOP Concept:
 * - Encapsulation  : data struk disembunyikan, hanya diakses lewat generateIsiStruk()
 * - Exception Handling : IOException ditangani saat menulis file (File I/O)
 * - Inheritance    : extends JDialog
 */
public class StrukDialog extends JDialog {

    private Peminjaman peminjaman;
    private String isiStruk;

    public StrukDialog(Frame parent, Peminjaman peminjaman) {
        super(parent, "Struk Peminjaman Buku", true);
        this.peminjaman = peminjaman;
        this.isiStruk = generateIsiStruk();
        initUI();
    }

    /**
     * Membuat isi struk dalam format teks.
     * OOP: Encapsulation — logika format struk tersembunyi di dalam method ini.
     */
    private String generateIsiStruk() {
        String garis  = "=".repeat(44);
        String garisTipis = "-".repeat(44);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss",
                java.util.Locale.of("id", "ID"));
        String waktuCetak = LocalDateTime.now().format(fmt);

        return garis + "\n" +
               "      PERPUSTAKAAN UNIVERSITAS BAKRIE     \n" +
               "           Jl. HR Rasuna Said Kav. C-22   \n" +
               "              Jakarta Selatan 12920        \n" +
               garis + "\n" +
               "  STRUK PEMINJAMAN BUKU\n" +
               garisTipis + "\n" +
               String.format("  ID Peminjaman : %s%n",  peminjaman.getId()) +
               String.format("  Tanggal Cetak : %s%n",  waktuCetak) +
               garisTipis + "\n" +
               "  DATA PEMINJAM\n" +
               garisTipis + "\n" +
               String.format("  Nama          : %s%n",  peminjaman.getMahasiswa().getNama()) +
               String.format("  NIM           : %s%n",  peminjaman.getMahasiswa().getNim()) +
               String.format("  Program Studi : %s%n",  peminjaman.getMahasiswa().getProdi()) +
               garisTipis + "\n" +
               "  DATA BUKU\n" +
               garisTipis + "\n" +
               String.format("  Judul         : %s%n",  wrap(peminjaman.getBuku().getJudul(), 26)) +
               String.format("  Pengarang     : %s%n",  peminjaman.getBuku().getPengarang()) +
               String.format("  Kategori      : %s%n",  peminjaman.getBuku().getKategori().getLabel()) +
               garisTipis + "\n" +
               "  DETAIL PEMINJAMAN\n" +
               garisTipis + "\n" +
               String.format("  Tgl Pinjam    : %s%n",  peminjaman.getTanggalPinjam()) +
               String.format("  Tgl Kembali   : %s%n",  peminjaman.getTanggalKembaliRencana()) +
               String.format("  Batas Hari    : %d hari%n", Peminjaman.getBATAS_HARI()) +
               String.format("  Denda/Hari    : Rp %,.0f%n", Peminjaman.getDENDA_PER_HARI()) +
               garis + "\n" +
               "  Harap kembalikan buku tepat waktu.\n" +
               "  Denda keterlambatan: Rp 2.000/hari.\n" +
               "  Terima kasih telah menggunakan\n" +
               "  layanan Perpustakaan UB.\n" +
               garis + "\n";
    }

    /** Wrap teks panjang agar tidak melewati batas karakter per baris */
    private String wrap(String text, int maxLen) {
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 1) + "…";
    }

    private void initUI() {
        setSize(440, 580);
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(getParent());
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.LATAR);

        // ── Header ───────────────────────────────────────
        GradientPanel header = new GradientPanel(Theme.MERAH, Theme.MERAH_GELAP);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(18, 24, 16, 24));

        JLabel lblJudul = new JLabel("Struk Peminjaman", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblJudul.setForeground(Color.WHITE);
        lblJudul.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblId = new JLabel("ID: " + peminjaman.getId(), SwingConstants.CENTER);
        lblId.setFont(Theme.FONT_SMALL);
        lblId.setForeground(Theme.EMAS);
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblId.setBorder(new EmptyBorder(4, 0, 0, 0));

        header.add(lblJudul);
        header.add(lblId);

        // ── Isi Struk (JTextArea monospace) ──────────────
        JTextArea txtStruk = new JTextArea(isiStruk);
        txtStruk.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtStruk.setEditable(false);
        txtStruk.setBackground(new Color(0xFAFAFA));
        txtStruk.setForeground(Theme.TEKS_UTAMA);
        txtStruk.setBorder(new EmptyBorder(12, 16, 12, 16));
        txtStruk.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(txtStruk);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(new Color(0xFAFAFA));

        // ── Footer: Tombol ────────────────────────────────
        JPanel footer = new JPanel(new GridLayout(1, 2, 10, 0));
        footer.setBackground(Theme.LATAR);
        footer.setBorder(new EmptyBorder(14, 16, 16, 16));

        JButton btnSimpan = Theme.buatTombolPrimer(
            "Simpan ke File .txt", IconUtil.tambah(14, Color.WHITE));
        btnSimpan.addActionListener(e -> simpanKeFile());

        JButton btnTutup = Theme.buatTombolSekunder("Tutup", IconUtil.silang(14, Theme.MERAH));
        btnTutup.addActionListener(e -> dispose());

        footer.add(btnSimpan);
        footer.add(btnTutup);

        root.add(header,  BorderLayout.NORTH);
        root.add(scroll,  BorderLayout.CENTER);
        root.add(footer,  BorderLayout.SOUTH);
        add(root);
    }

    /**
     * Menyimpan isi struk ke file .txt menggunakan File I/O.
     *
     * OOP Concept: Exception Handling — IOException ditangkap dan ditampilkan
     * sebagai pesan error ke pengguna, bukan dibiarkan crash.
     * Konsep File I/O: FileWriter + BufferedWriter untuk penulisan file teks.
     */
    private void simpanKeFile() {
        // Default nama file: Struk_PJM-XXXXXXXX.txt
        String namaFileDefault = "Struk_" + peminjaman.getId() + ".txt";

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan Struk Peminjaman");
        chooser.setSelectedFile(new File(namaFileDefault));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Text File (*.txt)", "txt"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        // Pastikan ekstensi .txt
        if (!file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }

        // OOP: Exception Handling — IOException untuk operasi file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(isiStruk);
            JOptionPane.showMessageDialog(this,
                "Struk berhasil disimpan ke:\n" + file.getAbsolutePath(),
                "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal menyimpan file:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method statis untuk memudahkan pemanggilan dari PeminjamanPanel.
     * Cukup panggil: StrukDialog.tampilkan(parent, peminjaman);
     */
    public static void tampilkan(Frame parent, Peminjaman peminjaman) {
        StrukDialog dialog = new StrukDialog(parent, peminjaman);
        dialog.setVisible(true);
    }
}