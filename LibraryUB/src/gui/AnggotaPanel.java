package gui;

import exception.FirebaseException;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.AnggotaService;
import service.AuthService;

/**
 * AnggotaPanel - GUI Manajemen Anggota (Mahasiswa & Petugas)
 * OOP Concept: Encapsulation, Polymorphism, Collection, Exception Handling
 */
public class AnggotaPanel extends JPanel {

    private Pengguna penggunaSaatIni;
    private AnggotaService anggotaService;
    private AuthService authService;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField tfSearch;

    public AnggotaPanel(Pengguna pengguna) {
        this.penggunaSaatIni = pengguna;
        this.anggotaService = new AnggotaService();
        this.authService = new AuthService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Theme.LATAR);
        setBorder(new EmptyBorder(Theme.PADDING, Theme.PADDING, Theme.PADDING, Theme.PADDING));

        JPanel topPanel = new JPanel(new BorderLayout(0, Theme.PADDING_SM));
        topPanel.setBackground(Theme.LATAR);
        topPanel.setBorder(new EmptyBorder(0, 0, Theme.PADDING_SM, 0));
        topPanel.add(Theme.buatPanelHeader("Manajemen Anggota",
            "Kelola akun Mahasiswa dan Petugas dalam satu panel",
            IconUtil.duaOrang(22, Theme.MERAH)), BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setBackground(Theme.LATAR_KARTU);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER),
            new EmptyBorder(10, 14, 10, 14)
        ));

        JPanel searchBox = new JPanel(new BorderLayout(8, 0));
        searchBox.setBackground(Theme.LATAR_KARTU);
        tfSearch = new JTextField();
        tfSearch.setPreferredSize(new Dimension(240, 34));
        tfSearch.setFont(Theme.FONT_BODY);
        tfSearch.setBorder(Theme.inputBorder());
        JButton btnSearch = Theme.buatTombolKecil("Cari", IconUtil.cari(13, Color.WHITE), Theme.MERAH);
        btnSearch.addActionListener(e -> {
            String kw = tfSearch.getText().trim();
            tampilkanData(anggotaService.cari(kw), filterPetugas(kw));
        });
        tfSearch.addActionListener(e -> btnSearch.doClick());
        searchBox.add(tfSearch, BorderLayout.CENTER);
        searchBox.add(btnSearch, BorderLayout.EAST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setBackground(Theme.LATAR_KARTU);
        JButton btnTambah = Theme.buatTombolKecil("Tambah", IconUtil.tambah(13, Color.WHITE), Theme.HIJAU);
        btnTambah.addActionListener(e -> dialogTambah());
        JButton btnEdit = Theme.buatTombolKecil("Edit", IconUtil.edit(13, Color.WHITE), Theme.BIRU);
        btnEdit.addActionListener(e -> dialogEdit());
        JButton btnHapus = Theme.buatTombolKecil("Hapus", IconUtil.hapus(13, Color.WHITE), Theme.MERAH_STATUS);
        btnHapus.addActionListener(e -> hapus());
        actionPanel.add(btnTambah); actionPanel.add(btnEdit); actionPanel.add(btnHapus);

        toolbar.add(searchBox, BorderLayout.WEST);
        toolbar.add(actionPanel, BorderLayout.EAST);
        topPanel.add(toolbar, BorderLayout.SOUTH);

        String[] kolom = {"ID", "Role", "Nama", "NIM / Shift", "Program Studi", "Username"};
        tableModel = new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = BukuPanel.buildTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setMaxWidth(90);

        // Renderer Role
        table.getColumnModel().getColumn(1).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setOpaque(true);
                if (!sel) {
                    String val = String.valueOf(v);
                    if ("Mahasiswa".equals(val)) { lbl.setForeground(Theme.BIRU); lbl.setBackground(Theme.BIRU_MUDA); }
                    else { lbl.setForeground(Theme.HIJAU); lbl.setBackground(Theme.HIJAU_MUDA); }
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
        tampilkanData(anggotaService.getDaftarMahasiswa(), anggotaService.getDaftarPetugas());
    }

    private List<Petugas> filterPetugas(String kw) {
        List<Petugas> all = anggotaService.getDaftarPetugas();
        if (kw == null || kw.isEmpty()) return all;
        String k = kw.toLowerCase();
        all.removeIf(p -> !p.getNama().toLowerCase().contains(k)
            && !p.getShift().toLowerCase().contains(k)
            && !p.getUsername().toLowerCase().contains(k));
        return all;
    }

    private void tampilkanData(List<Mahasiswa> mhs, List<Petugas> pet) {
        tableModel.setRowCount(0);
        for (Mahasiswa m : mhs)
            tableModel.addRow(new Object[]{ m.getId(), "Mahasiswa", m.getNama(), m.getNim(), m.getProdi(), m.getUsername() });
        if (penggunaSaatIni.getRole() == RoleUser.ADMIN)
            for (Petugas p : pet)
                tableModel.addRow(new Object[]{ p.getId(), "Petugas", p.getNama(), p.getShift(), "-", p.getUsername() });
    }

    private void dialogTambah() {
        JTextField tfNama = new JTextField(), tfUsername = new JTextField();
        JPasswordField pfPwd = new JPasswordField();
        JTextField tfNim = new JTextField(), tfProdi = new JTextField(), tfShift = new JTextField();

        boolean isAdmin = penggunaSaatIni.getRole() == RoleUser.ADMIN;
        JComboBox<RoleUser> cbRole = isAdmin
            ? new JComboBox<>(new RoleUser[]{RoleUser.MAHASISWA, RoleUser.PETUGAS})
            : new JComboBox<>(new RoleUser[]{RoleUser.MAHASISWA});

        CardLayout cl = new CardLayout();
        JPanel cardPanel = new JPanel(cl);

        JPanel panelMhs = new JPanel(new GridLayout(2, 2, 10, 8));
        panelMhs.add(Theme.buatLabel("NIM:")); panelMhs.add(tfNim);
        panelMhs.add(Theme.buatLabel("Program Studi:")); panelMhs.add(tfProdi);

        JPanel panelPet = new JPanel(new GridLayout(1, 2, 10, 8));
        panelPet.add(Theme.buatLabel("Shift:")); panelPet.add(tfShift);

        cardPanel.add(panelMhs, RoleUser.MAHASISWA.name());
        cardPanel.add(panelPet, RoleUser.PETUGAS.name());
        cbRole.addActionListener(e -> cl.show(cardPanel, ((RoleUser)cbRole.getSelectedItem()).name()));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel bRole = new JPanel(new GridLayout(1, 2, 10, 8));
        bRole.add(Theme.buatLabel("Role:")); bRole.add(cbRole);
        JPanel bInfo = new JPanel(new GridLayout(2, 2, 10, 8));
        bInfo.add(Theme.buatLabel("Nama:")); bInfo.add(tfNama);
        bInfo.add(Theme.buatLabel("Username:")); bInfo.add(tfUsername);
        JPanel bPwd = new JPanel(new GridLayout(1, 2, 10, 8));
        bPwd.add(Theme.buatLabel("Password:")); bPwd.add(pfPwd);

        p.add(bRole); p.add(Box.createRigidArea(new Dimension(0, 8)));
        p.add(bInfo); p.add(Box.createRigidArea(new Dimension(0, 8)));
        p.add(bPwd);  p.add(Box.createRigidArea(new Dimension(0, 8)));
        p.add(cardPanel);
        cl.show(cardPanel, ((RoleUser)cbRole.getSelectedItem()).name());

        if (JOptionPane.showConfirmDialog(this, p, "Tambah Anggota", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String nama = tfNama.getText().trim();
            if (nama.isEmpty()) { JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE); return; }
            try {
                if ((RoleUser)cbRole.getSelectedItem() == RoleUser.PETUGAS) {
                    authService.buatAkunPetugas(nama, tfUsername.getText().trim(), new String(pfPwd.getPassword()), tfShift.getText().trim());
                    JOptionPane.showMessageDialog(this, "Akun Petugas berhasil ditambahkan!");
                } else {
                    String nim = tfNim.getText().trim();
                    if (nim.isEmpty()) { JOptionPane.showMessageDialog(this, "NIM tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE); return; }
                    anggotaService.tambahAnggota(new Mahasiswa(anggotaService.generateId(), nama, tfUsername.getText().trim(), new String(pfPwd.getPassword()), nim, tfProdi.getText().trim()));
                    JOptionPane.showMessageDialog(this, "Anggota Mahasiswa berhasil ditambahkan!");
                }
                refresh();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (FirebaseException ex) {
                JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dialogEdit() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih anggota yang ingin diedit."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        if ("Petugas".equals(tableModel.getValueAt(row, 1))) {
            if (penggunaSaatIni.getRole() != RoleUser.ADMIN) { JOptionPane.showMessageDialog(this, "Hanya Admin yang dapat mengedit Petugas."); return; }
            Petugas pt = anggotaService.cariPetugasById(id);
            if (pt == null) return;
            JTextField tfNama = new JTextField(pt.getNama()), tfShift = new JTextField(pt.getShift());
            JPanel p = new JPanel(new GridLayout(2, 2, 10, 8)); p.setBorder(new EmptyBorder(10,10,10,10));
            p.add(Theme.buatLabel("Nama:")); p.add(tfNama);
            p.add(Theme.buatLabel("Shift:")); p.add(tfShift);
            if (JOptionPane.showConfirmDialog(this, p, "Edit Petugas", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                try { anggotaService.editPetugas(id, tfNama.getText().trim(), tfShift.getText().trim()); refresh(); JOptionPane.showMessageDialog(this, "Data petugas diperbarui!"); }
                catch (FirebaseException ex) { JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
            }
        } else {
            Mahasiswa m = anggotaService.cariAnggotaById(id);
            if (m == null) return;
            JTextField tfNama = new JTextField(m.getNama()), tfNim = new JTextField(m.getNim()), tfProdi = new JTextField(m.getProdi());
            JPanel p = new JPanel(new GridLayout(3, 2, 10, 8)); p.setBorder(new EmptyBorder(10,10,10,10));
            p.add(Theme.buatLabel("Nama:")); p.add(tfNama);
            p.add(Theme.buatLabel("NIM:")); p.add(tfNim);
            p.add(Theme.buatLabel("Program Studi:")); p.add(tfProdi);
            if (JOptionPane.showConfirmDialog(this, p, "Edit Mahasiswa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                try { anggotaService.editAnggota(id, tfNama.getText().trim(), tfNim.getText().trim(), tfProdi.getText().trim()); refresh(); JOptionPane.showMessageDialog(this, "Data mahasiswa diperbarui!"); }
                catch (FirebaseException ex) { JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }
    }

    private void hapus() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Pilih anggota yang ingin dihapus."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        String role = (String) tableModel.getValueAt(row, 1);
        String nama = (String) tableModel.getValueAt(row, 2);
        if ("Petugas".equals(role) && penggunaSaatIni.getRole() != RoleUser.ADMIN) {
            JOptionPane.showMessageDialog(this, "Hanya Admin yang dapat menghapus Petugas."); return;
        }
        if (JOptionPane.showConfirmDialog(this, "Hapus " + role.toLowerCase() + " \"" + nama + "\"?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if ("Petugas".equals(role)) anggotaService.hapusPetugas(id);
                else anggotaService.hapusAnggota(id);
                refresh();
            } catch (FirebaseException ex) {
                JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}