import gui.LoginFrame;
import javax.swing.*;

/**
 * Main - Entry point aplikasi
 * Sistem Manajemen Perpustakaan Universitas Bakrie
 *
 * OOP Concepts yang diimplementasikan:
 * - Class & Object        : Buku, Peminjaman, Admin, Petugas, Mahasiswa, dll
 * - Encapsulation         : semua field private + getter/setter
 * - Inheritance           : Admin, Petugas, Mahasiswa extends Pengguna (abstract)
 * - Polymorphism          : override kirimPesan(), getInfoLengkap(), getDashboardTitle()
 * - Abstraction           : abstract class Pengguna
 * - Interface             : Notifikasi, Searchable<T>
 * - Enum                  : StatusBuku, KategoriBuku, RoleUser
 * - Collection            : ArrayList<Buku>, ArrayList<Pengguna>, ArrayList<Peminjaman>
 * - Exception Handling    : BukuTidakTersediaException, MelebihiBatasPinjamException,
 *                           PenggunaTidakDitemukanException
 */
public class Main {
    public static void main(String[] args) {
        // Set Look and Feel agar tampilan lebih modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
