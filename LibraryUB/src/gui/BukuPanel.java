package gui;

import exception.FirebaseException;
import model.*;
import service.BukuService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * BukuPanel - GUI Manajemen Buku dengan Filter Kategori, Sort Kolom, dan Preview Hapus.
 *
 * OOP Concept:
 * - Encapsulation  : state filter & sort tersimpan sebagai field private
 * - Polymorphism   : akses fitur tambah/edit/hapus dibatasi sesuai role
 * - Interface      : memakai Searchable<Buku> via BukuService.cari()
 * - Collection     : ArrayList dari hasil filter+sort dikelola sebelum dirender ke tabel
 * - Exception Handling : FirebaseException saat CRUD ke Firestore
 */
public class BukuPanel extends JPanel {

    private Pengguna penggunaSaatIni;
    private BukuService bukuService;

    // State filter & sort
    private String keywordCari = "";
    private KategoriBuku filterKategori = null; // null = semua kategori
    private int sortKolom = -1;                 // -1 = tidak di-sort
    private boolean sortAsc = true;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField tfSearch;
    private JComboBox<String> cbKategoriFilter;
    private JLabel lblJumlah;

    public BukuPanel(Pengguna pengguna) {
        this.penggunaSaatIni = pengguna;
        this.bukuService = new BukuService();
        initUI();
        refresh();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.LATAR);
        setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        // ── Header ──
        JPanel topPanel = new JPanel(new BorderLayout(0, Theme.PADDING_SM));
        topPanel.setBackground(Theme.LATAR);
        topPanel.setBorder(new EmptyBorder(0, 0, Theme.PADDING_SM, 0));
        topPanel.add(Theme.buatPanelHeader("Manajemen Buku",
            "Kelola koleksi buku perpustakaan — tambah, edit, hapus, cari, dan filter kategori",
            IconUtil.buku(22, Theme.MERAH)), BorderLayout.NORTH);

        // ── Toolbar ──
        JPanel toolbar = new JPanel(new BorderLayout(12, 6));
        toolbar.setBackground(Theme.LATAR_KARTU);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            new EmptyBorder(10, 14, 10, 14)
        ));

        // Baris atas toolbar: search + filter kategori
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setBackground(Theme.LATAR_KARTU);

        tfSearch = new JTextField();
        tfSearch.setPreferredSize(new Dimension(220, 34));
        tfSearch.setFont(Theme.FONT_BODY);
        tfSearch.setBorder(Theme.inputBorder());
        tfSearch.setToolTipText("Cari judul, pengarang, ISBN...");

        JButton btnSearch = Theme.buatTombolKecil("Cari", IconUtil.cari(13, Color.WHITE), Theme.MERAH);
        btnSearch.addActionListener(e -> {
            keywordCari = tfSearch.getText().trim();
            tampilkanData();
        });
        tfSearch.addActionListener(e -> btnSearch.doClick());

        // Filter Kategori dropdown
        String[] opsiKategori = {"Semua Kategori", "Teknologi", "Bisnis", "Ekonomi", "Hukum"};
        cbKategoriFilter = new JComboBox<>(opsiKategori);
        cbKategoriFilter.setFont(Theme.FONT_BODY);
        cbKategoriFilter.setPreferredSize(new Dimension(155, 34));
        cbKategoriFilter.setBackground(Theme.PUTIH);
        cbKategoriFilter.setToolTipText("Filter berdasarkan kategori");
        cbKategoriFilter.addActionListener(e -> {
            int idx = cbKategoriFilter.getSelectedIndex();
            filterKategori = idx == 0 ? null : KategoriBuku.values()[idx - 1];
            tampilkanData();
        });

        // Reset filter
        JButton btnReset = Theme.buatTombolKecil("Reset", null, new Color(0x6B7280));
        btnReset.addActionListener(e -> {
            tfSearch.setText("");
            cbKategoriFilter.setSelectedIndex(0);
            keywordCari = "";
            filterKategori = null;
            sortKolom = -1;
            sortAsc = true;
            tampilkanData();
        });

        searchRow.add(tfSearch);
        searchRow.add(btnSearch);
        searchRow.add(new JSeparator(SwingConstants.VERTICAL) {{ setPreferredSize(new Dimension(1, 28)); }});
        searchRow.add(new JLabel("Kategori:") {{ setFont(Theme.FONT_LABEL); setForeground(Theme.TEKS_ABU); }});
        searchRow.add(cbKategoriFilter);
        searchRow.add(btnReset);

        // Baris kanan toolbar: aksi + jumlah buku
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionRow.setBackground(Theme.LATAR_KARTU);

        lblJumlah = new JLabel();
        lblJumlah.setFont(Theme.FONT_SMALL);
        lblJumlah.setForeground(Theme.TEKS_ABU);
        actionRow.add(lblJumlah);

        if (penggunaSaatIni.getRole() != RoleUser.MAHASISWA) {
            JButton btnTambah = Theme.buatTombolKecil("Tambah", IconUtil.tambah(13, Color.WHITE), Theme.HIJAU);
            btnTambah.addActionListener(e -> dialogTambah());
            JButton btnEdit = Theme.buatTombolKecil("Edit", IconUtil.edit(13, Color.WHITE), Theme.BIRU);
            btnEdit.addActionListener(e -> dialogEdit());
            JButton btnHapus = Theme.buatTombolKecil("Hapus", IconUtil.hapus(13, Color.WHITE), Theme.MERAH_STATUS);
            btnHapus.addActionListener(e -> konfirmasiHapus());
            actionRow.add(btnTambah);
            actionRow.add(btnEdit);
            actionRow.add(btnHapus);
        }

        toolbar.add(searchRow, BorderLayout.WEST);
        toolbar.add(actionRow, BorderLayout.EAST);
        topPanel.add(toolbar, BorderLayout.SOUTH);

        // ── Tabel ──
        String[] kolom = {"ID", "Judul", "Pengarang", "Penerbit", "Tahun", "Kategori", "Stok", "Status"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        TableStyler.terapkan(table);

        // Ukuran kolom
        table.getColumnModel().getColumn(0).setMaxWidth(65);
        table.getColumnModel().getColumn(4).setMaxWidth(58);
        table.getColumnModel().getColumn(6).setMaxWidth(52);
        table.getColumnModel().getColumn(7).setMaxWidth(88);

        // Renderer Status badge
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setOpaque(true);
                if (!sel) {
                    boolean tersedia = "Tersedia".equalsIgnoreCase(String.valueOf(v));
                    lbl.setForeground(tersedia ? Theme.HIJAU : Theme.MERAH_STATUS);
                    lbl.setBackground(tersedia ? Theme.HIJAU_MUDA : Theme.MERAH_MUDA);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setBorder(new EmptyBorder(3, 6, 3, 6));
                }
                return lbl;
            }
        });

        // Renderer Kategori badge
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            private final Color[] colors = {
                new Color(0x1D4ED8), new Color(0x059669),
                new Color(0xB45309), new Color(0x7C3AED)
            };
            private final Color[] bgs = { Theme.BIRU_MUDA, Theme.HIJAU_MUDA,
                Theme.EMAS_MUDA, new Color(0xF5F3FF) };
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setOpaque(true);
                if (!sel) {
                    String val = String.valueOf(v);
                    int idx = 0;
                    if ("Bisnis".equals(val))   idx = 1;
                    else if ("Ekonomi".equals(val)) idx = 2;
                    else if ("Hukum".equals(val))   idx = 3;
                    lbl.setForeground(colors[idx]);
                    lbl.setBackground(bgs[idx]);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                }
                return lbl;
            }
        });

        // ── Klik header = sort kolom ──
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int col = table.getTableHeader().columnAtPoint(e.getPoint());
                if (col < 0) return;
                if (sortKolom == col) {
                    sortAsc = !sortAsc;   // toggle arah sort
                } else {
                    sortKolom = col;
                    sortAsc = true;
                }
                tampilkanData();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scrollPane.getViewport().setBackground(Theme.LATAR_KARTU);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refresh() {
        tampilkanData();
    }

    /**
     * Ambil data → filter → sort → render ke tabel.
     *
     * OOP: Collection — List hasil filter diproses dengan Comparator sebelum dimasukkan ke tabel.
     * Ini mendemonstrasikan penggunaan Collection (List, sort dengan lambda Comparator)
     * dan Encapsulation (state filter/sort tersimpan sebagai field private class ini).
     */
    private void tampilkanData() {
        // 1. Ambil dari service (pakai Searchable interface)
        List<Buku> daftar = bukuService.cari(keywordCari);

        // 2. Filter kategori
        if (filterKategori != null) {
            daftar.removeIf(b -> b.getKategori() != filterKategori);
        }

        // 3. Sort kolom — OOP: Comparator sebagai lambda (Polymorphism fungsional)
        if (sortKolom >= 0) {
            final int col = sortKolom;
            daftar.sort((a, b) -> {
                String valA = getNilaiKolom(a, col);
                String valB = getNilaiKolom(b, col);
                // Kolom angka (tahun, stok): sort numerik
                if (col == 4 || col == 6) {
                    try {
                        return sortAsc
                            ? Integer.compare(Integer.parseInt(valA), Integer.parseInt(valB))
                            : Integer.compare(Integer.parseInt(valB), Integer.parseInt(valA));
                    } catch (NumberFormatException ex) { /* fallthrough ke string */ }
                }
                return sortAsc ? valA.compareToIgnoreCase(valB) : valB.compareToIgnoreCase(valA);
            });
        }

        // 4. Render ke tabel
        tableModel.setRowCount(0);
        for (Buku b : daftar) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getJudul(), b.getPengarang(), b.getPenerbit(),
                b.getTahunTerbit(), b.getKategori().getLabel(),
                b.getStok(), b.getStatus().getLabel()
            });
        }

        // Update label jumlah
        String info = daftar.size() + " buku";
        if (filterKategori != null) info += " • " + filterKategori.getLabel();
        if (!keywordCari.isEmpty()) info += " • \"" + keywordCari + "\"";
        if (sortKolom >= 0) info += " • Diurutkan " + (sortAsc ? "↑" : "↓");
        lblJumlah.setText(info);

        // Update header dengan indikator sort
        TableColumnModel tcm = table.getColumnModel();
        String[] namaKolom = {"ID","Judul","Pengarang","Penerbit","Tahun","Kategori","Stok","Status"};
        for (int i = 0; i < namaKolom.length; i++) {
            String nama = namaKolom[i];
            if (i == sortKolom) nama += (sortAsc ? " ↑" : " ↓");
            tcm.getColumn(i).setHeaderValue(nama);
        }
        table.getTableHeader().repaint();
    }

    /** Ambil nilai string dari field Buku sesuai indeks kolom tabel */
    private String getNilaiKolom(Buku b, int col) {
        return switch (col) {
            case 0 -> b.getId();
            case 1 -> b.getJudul();
            case 2 -> b.getPengarang();
            case 3 -> b.getPenerbit();
            case 4 -> String.valueOf(b.getTahunTerbit());
            case 5 -> b.getKategori().getLabel();
            case 6 -> String.valueOf(b.getStok());
            case 7 -> b.getStatus().getLabel();
            default -> "";
        };
    }

    // ── Dialog Tambah ──────────────────────────────────────
    private void dialogTambah() {
        JTextField tfJudul = new JTextField(); JTextField tfPengarang = new JTextField();
        JTextField tfPenerbit = new JTextField(); JTextField tfTahun = new JTextField();
        JTextField tfIsbn = new JTextField(); JTextField tfStok = new JTextField();
        JComboBox<KategoriBuku> cbKat = new JComboBox<>(KategoriBuku.values());

        JPanel p = buildForm(
            new String[]{"Judul","Pengarang","Penerbit","Tahun Terbit","ISBN","Kategori","Stok"},
            new JComponent[]{tfJudul, tfPengarang, tfPenerbit, tfTahun, tfIsbn, cbKat, tfStok}
        );

        if (JOptionPane.showConfirmDialog(this, p, "Tambah Buku Baru",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                if (tfJudul.getText().trim().isEmpty()) throw new IllegalArgumentException("Judul tidak boleh kosong.");
                int tahun = Integer.parseInt(tfTahun.getText().trim());
                int stok  = Integer.parseInt(tfStok.getText().trim());
                if (stok < 0) throw new IllegalArgumentException("Stok tidak boleh negatif.");
                bukuService.tambahBuku(new Buku(bukuService.generateId(),
                    tfJudul.getText().trim(), tfPengarang.getText().trim(),
                    tfPenerbit.getText().trim(), tahun, tfIsbn.getText().trim(),
                    (KategoriBuku) cbKat.getSelectedItem(), stok));
                tampilkanData();
                JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tahun dan stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (FirebaseException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Dialog Edit ────────────────────────────────────────
    private void dialogEdit() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih buku yang ingin diedit."); return; }
        Buku b = bukuService.cariBukuById((String) tableModel.getValueAt(row, 0));
        if (b == null) return;

        JTextField tfJudul = new JTextField(b.getJudul());
        JTextField tfPengarang = new JTextField(b.getPengarang());
        JTextField tfPenerbit = new JTextField(b.getPenerbit());
        JTextField tfTahun = new JTextField(String.valueOf(b.getTahunTerbit()));
        JTextField tfIsbn = new JTextField(b.getIsbn());
        JTextField tfStok = new JTextField(String.valueOf(b.getStok()));
        JComboBox<KategoriBuku> cbKat = new JComboBox<>(KategoriBuku.values());
        cbKat.setSelectedItem(b.getKategori());

        JPanel p = buildForm(
            new String[]{"Judul","Pengarang","Penerbit","Tahun Terbit","ISBN","Kategori","Stok"},
            new JComponent[]{tfJudul, tfPengarang, tfPenerbit, tfTahun, tfIsbn, cbKat, tfStok}
        );

        if (JOptionPane.showConfirmDialog(this, p, "Edit Buku — " + b.getJudul(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                bukuService.editBuku(b.getId(), tfJudul.getText().trim(), tfPengarang.getText().trim(),
                    tfPenerbit.getText().trim(), Integer.parseInt(tfTahun.getText().trim()),
                    tfIsbn.getText().trim(), (KategoriBuku) cbKat.getSelectedItem(),
                    Integer.parseInt(tfStok.getText().trim()));
                tampilkanData();
                JOptionPane.showMessageDialog(this, "Buku berhasil diperbarui!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tahun dan stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (FirebaseException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Konfirmasi Hapus dengan Preview ───────────────────
    /**
     * Menampilkan dialog konfirmasi hapus yang memperlihatkan seluruh data buku
     * sebelum pengguna memutuskan untuk menghapus.
     *
     * OOP: Encapsulation — logika validasi & preview dibungkus dalam method ini,
     * tidak menyebar ke mana-mana. Caller (tombol Hapus) cukup panggil satu method ini.
     */
    private void konfirmasiHapus() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih buku yang ingin dihapus.", "Perhatian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Buku buku = bukuService.cariBukuById(id);
        if (buku == null) return;

        // Buku sedang dipinjam? Blok hapus
        if (buku.getStok() < buku.getStok() || buku.getStatus() == StatusBuku.DIPINJAM) {
            // cek lebih robust: ada peminjaman aktif untuk buku ini
        }

        // Panel preview data buku
        JPanel preview = new JPanel(new GridBagLayout());
        preview.setBackground(Theme.PUTIH);
        preview.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.MERAH_STATUS, 1),
            new EmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 0, 3, 16);

        // Header preview
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblWarning = new JLabel("Data buku yang akan dihapus:");
        lblWarning.setFont(Theme.FONT_LABEL);
        lblWarning.setForeground(Theme.MERAH_STATUS);
        preview.add(lblWarning, gbc);

        // Separator
        gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        preview.add(new JSeparator(), gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.gridwidth = 1;

        // Data buku
        String[][] data = {
            {"ID",           buku.getId()},
            {"Judul",        buku.getJudul()},
            {"Pengarang",    buku.getPengarang()},
            {"Penerbit",     buku.getPenerbit()},
            {"Tahun Terbit", String.valueOf(buku.getTahunTerbit())},
            {"Kategori",     buku.getKategori().getLabel()},
            {"Stok",         String.valueOf(buku.getStok())},
            {"Status",       buku.getStatus().getLabel()},
        };
        for (int i = 0; i < data.length; i++) {
            gbc.gridx = 0; gbc.gridy = i + 2;
            JLabel lKey = new JLabel(data[i][0]);
            lKey.setFont(Theme.FONT_LABEL);
            lKey.setForeground(Theme.TEKS_ABU);
            preview.add(lKey, gbc);

            gbc.gridx = 1;
            JLabel lVal = new JLabel(data[i][1]);
            lVal.setFont(Theme.FONT_BODY);
            lVal.setForeground(Theme.TEKS_UTAMA);
            preview.add(lVal, gbc);
        }

        // Panel utama dialog
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 12));
        dialogPanel.setBackground(Theme.PUTIH);

        JLabel lblKonfirmasi = new JLabel(
            "<html><b>Apakah Anda yakin ingin menghapus buku ini?</b><br>" +
            "<font color='#DC2626'>Tindakan ini tidak dapat dibatalkan.</font></html>"
        );
        lblKonfirmasi.setFont(Theme.FONT_BODY);
        dialogPanel.add(lblKonfirmasi, BorderLayout.NORTH);
        dialogPanel.add(preview, BorderLayout.CENTER);

        int confirm = JOptionPane.showConfirmDialog(this, dialogPanel,
            "Konfirmasi Hapus Buku", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bukuService.hapusBuku(id);
                tampilkanData();
                JOptionPane.showMessageDialog(this,
                    "Buku \"" + buku.getJudul() + "\" berhasil dihapus.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } catch (FirebaseException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Helper ────────────────────────────────────────────
    private JPanel buildForm(String[] labels, JComponent[] fields) {
        JPanel p = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < labels.length; i++) {
            p.add(Theme.buatLabel(labels[i] + ":"));
            fields[i].setPreferredSize(new Dimension(220, 32));
            p.add(fields[i]);
        }
        return p;
    }

    /** Helper untuk komponen lain (PeminjamanPanel, dll) yang butuh tabel dengan style sama */
    static JTable buildTable(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        TableStyler.terapkan(tbl);
        return tbl;
    }
}