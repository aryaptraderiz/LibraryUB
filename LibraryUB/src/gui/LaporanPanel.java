package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.*;

/**
 * LaporanPanel - GUI Laporan
 * OOP Concept: Collection, Polymorphism
 */
public class LaporanPanel extends JPanel {

    private BukuService bukuService;
    private AnggotaService anggotaService;
    private PeminjamanService peminjamanService;
    private DefaultTableModel modelTerpopuler, modelAktif, modelTerlambat;
    private JLabel lblTotalDenda;

    public LaporanPanel() {
        this.bukuService = new BukuService();
        this.anggotaService = new AnggotaService();
        this.peminjamanService = new PeminjamanService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, Theme.PADDING_SM));
        setBackground(Theme.LATAR);
        setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        add(Theme.buatPanelHeader("Laporan Perpustakaan",
            "Ringkasan data perpustakaan secara real-time dari Firebase",
            IconUtil.grafik(22, Theme.MERAH)), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.FONT_H3);
        tabs.setBackground(Theme.LATAR_KARTU);

        modelTerpopuler = new DefaultTableModel(new String[]{"Judul Buku","Pengarang","Kategori","Total Dipinjam"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        modelAktif = new DefaultTableModel(new String[]{"Nama","NIM","Program Studi","Pinjaman Aktif"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        modelTerlambat = new DefaultTableModel(new String[]{"Mahasiswa","Buku","Tgl Rencana Kembali","Terlambat","Denda"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabs.addTab("Buku Terpopuler",  IconUtil.buku(14, Theme.MERAH),       wrapTable(modelTerpopuler));
        tabs.addTab("Anggota Aktif",    IconUtil.duaOrang(14, Theme.HIJAU),   wrapTable(modelAktif));
        tabs.addTab("Keterlambatan",    IconUtil.jam(14, Theme.MERAH_STATUS), wrapTable(modelTerlambat));
        tabs.addTab("Total Denda",      IconUtil.uang(14, Theme.EMAS),         buildDendaPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane wrapTable(DefaultTableModel model) {
        JTable tbl = BukuPanel.buildTable(model);
        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        sp.getViewport().setBackground(Theme.LATAR_KARTU);
        return sp;
    }

    private JPanel buildDendaPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Theme.LATAR_KARTU);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.LATAR_KARTU);
        card.setBorder(new ShadowBorder());

        JLabel lblIcon = new JLabel(IconUtil.uang(52, Theme.EMAS));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setBorder(new EmptyBorder(24, 40, 12, 40));

        lblTotalDenda = new JLabel("Rp 0", SwingConstants.CENTER);
        lblTotalDenda.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblTotalDenda.setForeground(Theme.MERAH);
        lblTotalDenda.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Total Denda Keseluruhan", SwingConstants.CENTER);
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEKS_ABU);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setBorder(new EmptyBorder(4, 40, 24, 40));

        card.add(lblIcon); card.add(lblTotalDenda); card.add(lblSub);
        p.add(card);
        return p;
    }

    public void refresh() {
        // Buku terpopuler
        modelTerpopuler.setRowCount(0);
        for (Buku b : bukuService.getBukuTerpopuler()) {
            if (b.getTotalDipinjam() > 0)
                modelTerpopuler.addRow(new Object[]{ b.getJudul(), b.getPengarang(), b.getKategori().getLabel(), b.getTotalDipinjam() });
        }
        if (modelTerpopuler.getRowCount() == 0)
            modelTerpopuler.addRow(new Object[]{"Belum ada data peminjaman","-","-","-"});

        // Anggota aktif
        modelAktif.setRowCount(0);
        for (Mahasiswa m : anggotaService.getDaftarMahasiswa()) {
            long aktif = peminjamanService.getPeminjamanByMahasiswa(m).stream()
                .filter(p -> p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF).count();
            if (aktif > 0) modelAktif.addRow(new Object[]{ m.getNama(), m.getNim(), m.getProdi(), aktif });
        }
        if (modelAktif.getRowCount() == 0) modelAktif.addRow(new Object[]{"Belum ada anggota aktif","-","-","-"});

        // Keterlambatan
        modelTerlambat.setRowCount(0);
        for (Peminjaman p : peminjamanService.getDaftarTerlambat())
            modelTerlambat.addRow(new Object[]{ p.getMahasiswa().getNama(), p.getBuku().getJudul(),
                p.getTanggalKembaliRencana(), p.hitungKeterlambatan() + " hari",
                "Rp " + String.format("%,.0f", p.hitungDenda()) });
        if (modelTerlambat.getRowCount() == 0) modelTerlambat.addRow(new Object[]{"Tidak ada keterlambatan","-","-","-","-"});

        // Total denda
        lblTotalDenda.setText("Rp " + String.format("%,.0f", peminjamanService.getTotalDenda()));
    }
}