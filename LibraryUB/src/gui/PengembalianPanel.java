package gui;

import exception.FirebaseException;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.PeminjamanService;

/**
 * PengembalianPanel - GUI Pengembalian Buku
 * OOP Concept: Encapsulation, Exception Handling
 */
public class PengembalianPanel extends JPanel {

    private Pengguna penggunaSaatIni;
    private PeminjamanService peminjamanService;
    private DefaultTableModel tableModel;
    private JTable table;

    public PengembalianPanel(Pengguna pengguna) {
        this.penggunaSaatIni = pengguna;
        this.peminjamanService = new PeminjamanService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.LATAR);
        setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        JPanel topPanel = new JPanel(new BorderLayout(0, Theme.PADDING_SM));
        topPanel.setBackground(Theme.LATAR);
        topPanel.setBorder(new EmptyBorder(0, 0, Theme.PADDING_SM, 0));
        topPanel.add(Theme.buatPanelHeader("Pengembalian Buku",
            "Proses pengembalian dan hitung denda keterlambatan otomatis",
            IconUtil.panahMasuk(22, Theme.MERAH)), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Theme.LATAR_KARTU);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblInfo = new JLabel("Denda: Rp 2.000/hari  •  Batas pinjam: 7 hari");
        lblInfo.setFont(Theme.FONT_SMALL);
        lblInfo.setForeground(Theme.TEKS_ABU);

        JButton btnKembalikan = Theme.buatTombolKecil("Proses Pengembalian", IconUtil.centang(13, Color.WHITE), Theme.HIJAU);
        btnKembalikan.addActionListener(e -> proses());

        toolbar.add(lblInfo, BorderLayout.WEST);
        toolbar.add(btnKembalikan, BorderLayout.EAST);
        topPanel.add(toolbar, BorderLayout.SOUTH);

        String[] kolom = {"ID Peminjaman", "Mahasiswa", "Judul Buku", "Tgl Pinjam", "Tgl Rencana Kembali", "Terlambat", "Estimasi Denda"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = BukuPanel.buildTable(tableModel);

        // Renderer kolom denda
        table.getColumnModel().getColumn(6).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setOpaque(true);
                if (!sel) {
                    String val = String.valueOf(v);
                    boolean ada = !val.equals("Rp 0") && !val.equals("Rp 0,00");
                    lbl.setForeground(ada ? Theme.MERAH_STATUS : Theme.HIJAU);
                    lbl.setBackground(ada ? Theme.MERAH_MUDA : Theme.HIJAU_MUDA);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
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
            ? peminjamanService.getPeminjamanByMahasiswa((Mahasiswa) penggunaSaatIni).stream()
                .filter(p -> p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF).toList()
            : peminjamanService.getPeminjamanAktif();

        for (Peminjaman p : daftar) {
            long terlambat = p.hitungKeterlambatan();
            double denda = p.hitungDenda();
            tableModel.addRow(new Object[]{ p.getId(), p.getMahasiswa().getNama(),
                p.getBuku().getJudul(), p.getTanggalPinjam(), p.getTanggalKembaliRencana(),
                terlambat + " hari", "Rp " + String.format("%,.0f", denda) });
        }
    }

    private void proses() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih peminjaman yang ingin dikembalikan."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        String judul = (String) tableModel.getValueAt(row, 2);
        String dendaStr = (String) tableModel.getValueAt(row, 6);

        String konfirmasiMsg = "Proses pengembalian buku \"" + judul + "\"?\n";
        if (!dendaStr.equals("Rp 0")) konfirmasiMsg += "\nEstimasi denda: " + dendaStr;

        if (JOptionPane.showConfirmDialog(this, konfirmasiMsg, "Konfirmasi Pengembalian", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                double denda = peminjamanService.kembalikanBuku(id);
                refresh();
                String pesan = "Buku berhasil dikembalikan!";
                if (denda > 0) pesan += "\nDenda: Rp " + String.format("%,.0f", denda);
                else pesan += "\nTidak ada denda. Terima kasih!";
                JOptionPane.showMessageDialog(this, pesan, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (FirebaseException ex) {
                JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}