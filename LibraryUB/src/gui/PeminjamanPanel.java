package gui;

import exception.BukuTidakTersediaException;
import exception.FirebaseException;
import exception.MelebihiBatasPinjamException;
import model.*;
import service.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * PeminjamanPanel - GUI Peminjaman Buku
 * OOP Concept: Exception Handling, Polymorphism
 */
public class PeminjamanPanel extends JPanel {

    private Pengguna penggunaSaatIni;
    private PeminjamanService peminjamanService;
    private BukuService bukuService;
    private AnggotaService anggotaService;
    private DefaultTableModel tableModel;
    private JTable table;

    public PeminjamanPanel(Pengguna pengguna) {
        this.penggunaSaatIni = pengguna;
        this.peminjamanService = new PeminjamanService();
        this.bukuService = new BukuService();
        this.anggotaService = new AnggotaService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.LATAR);
        setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        JPanel topPanel = new JPanel(new BorderLayout(0, Theme.PADDING_SM));
        topPanel.setBackground(Theme.LATAR);
        topPanel.setBorder(new EmptyBorder(0, 0, Theme.PADDING_SM, 0));
        topPanel.add(Theme.buatPanelHeader("Peminjaman Buku",
            "Kelola peminjaman buku — pinjam dan batalkan",
            IconUtil.panahKeluar(22, Theme.MERAH)), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setBackground(Theme.LATAR_KARTU);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            new EmptyBorder(10, 14, 10, 14)
        ));
        JButton btnPinjam = Theme.buatTombolKecil("Pinjam Buku", IconUtil.tambah(13, Color.WHITE), Theme.HIJAU);
        btnPinjam.addActionListener(e -> dialogPinjam());
        JButton btnBatal = Theme.buatTombolKecil("Batalkan", IconUtil.silang(13, Color.WHITE), Theme.MERAH_STATUS);
        btnBatal.addActionListener(e -> batalkan());
        toolbar.add(btnPinjam);
        toolbar.add(btnBatal);
        topPanel.add(toolbar, BorderLayout.SOUTH);

        String[] kolom = {"ID Peminjaman", "Mahasiswa", "Judul Buku", "Tgl Pinjam", "Tgl Kembali", "Status"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = BukuPanel.buildTable(tableModel);

        // Renderer status
        table.getColumnModel().getColumn(5).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setOpaque(true);
                if (!sel) {
                    String val = String.valueOf(v);
                    if (val.contains("AKTIF")) { lbl.setForeground(Theme.BIRU); lbl.setBackground(Theme.BIRU_MUDA); }
                    else if (val.contains("DIKEMBALIKAN")) { lbl.setForeground(Theme.HIJAU); lbl.setBackground(Theme.HIJAU_MUDA); }
                    else { lbl.setForeground(Theme.TEKS_ABU); lbl.setBackground(Theme.LATAR); }
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Theme.LATAR_KARTU);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void refresh() {
        tableModel.setRowCount(0);
        List<Peminjaman> daftar = (penggunaSaatIni instanceof Mahasiswa)
            ? peminjamanService.getPeminjamanByMahasiswa((Mahasiswa) penggunaSaatIni)
            : peminjamanService.getDaftarPeminjaman();
        for (Peminjaman p : daftar) {
            tableModel.addRow(new Object[]{ p.getId(), p.getMahasiswa().getNama(),
                p.getBuku().getJudul(), p.getTanggalPinjam(),
                p.getTanggalKembaliRencana(), p.getStatusPeminjaman() });
        }
    }

    private void dialogPinjam() {
        List<Buku> tersedia = bukuService.getDaftarBuku().stream().filter(Buku::tersedia).toList();
        if (tersedia.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada buku yang tersedia saat ini."); return; }

        List<Mahasiswa> daftarMhs = anggotaService.getDaftarMahasiswa();
        JComboBox<Mahasiswa> cbMhs = new JComboBox<>(daftarMhs.toArray(new Mahasiswa[0]));
        JComboBox<Buku> cbBuku = new JComboBox<>(tersedia.toArray(new Buku[0]));

        if (penggunaSaatIni instanceof Mahasiswa) { cbMhs.setSelectedItem(penggunaSaatIni); cbMhs.setEnabled(false); }

        JPanel p = new JPanel(new GridLayout(2, 2, 10, 12));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(Theme.buatLabel("Mahasiswa:")); p.add(cbMhs);
        p.add(Theme.buatLabel("Buku:")); p.add(cbBuku);

        if (JOptionPane.showConfirmDialog(this, p, "Form Peminjaman", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            Mahasiswa mhs = (Mahasiswa) cbMhs.getSelectedItem();
            Buku buku = (Buku) cbBuku.getSelectedItem();
            try {
                Peminjaman pm = peminjamanService.pinjamBuku(mhs, buku);
                refresh();
                // OOP: Encapsulation — detail tampilan struk disembunyikan di dalam StrukDialog
                StrukDialog.tampilkan(
                    (java.awt.Frame) SwingUtilities.getWindowAncestor(this), pm);
            } catch (BukuTidakTersediaException | MelebihiBatasPinjamException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Gagal Meminjam", JOptionPane.ERROR_MESSAGE);
            } catch (FirebaseException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void batalkan() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih peminjaman yang ingin dibatalkan."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        Peminjaman p = peminjamanService.cariPeminjamanById(id);
        if (p == null || p.getStatusPeminjaman() != Peminjaman.StatusPeminjaman.AKTIF) {
            JOptionPane.showMessageDialog(this, "Peminjaman ini tidak dapat dibatalkan."); return;
        }
        if (JOptionPane.showConfirmDialog(this, "Batalkan peminjaman buku \"" + p.getBuku().getJudul() + "\"?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { peminjamanService.batalkanPeminjaman(id); refresh(); }
            catch (FirebaseException ex) { JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        }
    }
}