package service;

import exception.FirebaseException;
import exception.PenggunaTidakDitemukanException;
import java.util.List;
import java.util.UUID;
import model.Mahasiswa;
import model.Pengguna;
import model.Petugas;
import model.RoleUser;

/**
 * AuthService - mengelola autentikasi & registrasi pengguna
 * OOP Concept: Encapsulation, Exception Handling, Polymorphism
 */
public class AuthService {

    private DataStore dataStore;
    private Pengguna penggunaSaatIni;

    public AuthService() {
        this.dataStore = DataStore.getInstance();
    }

    /**
     * Login user berdasarkan username, password, dan role (Admin, Petugas, Mahasiswa)
     * OOP: Exception Handling
     */
    public Pengguna login(String username, String password, RoleUser role)
            throws PenggunaTidakDitemukanException {

        List<Pengguna> daftarPengguna = dataStore.getDaftarPengguna();

        for (Pengguna p : daftarPengguna) {
            if (p.getUsername().equalsIgnoreCase(username) && p.getRole() == role) {
                if (p.verifikasiPassword(password)) {
                    penggunaSaatIni = p;
                    // OOP: Polymorphism - kirimPesan berbeda tiap subclass
                    p.kirimPesan("Login berhasil pada " + java.time.LocalDateTime.now());
                    return p;
                } else {
                    throw new PenggunaTidakDitemukanException(username + " (password salah)");
                }
            }
        }

        throw new PenggunaTidakDitemukanException(username);
    }

    /**
     * Registrasi akun mahasiswa baru lewat form Register (self-service).
     * OOP: Exception Handling - validasi & FirebaseException diteruskan ke pemanggil
     */
    public Mahasiswa register(String nama, String username, String password,
                               String nim, String prodi) throws FirebaseException, IllegalArgumentException {

        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }
        if (nim == null || nim.trim().isEmpty()) {
            throw new IllegalArgumentException("NIM tidak boleh kosong.");
        }

        cekUsernameTersedia(username);

        String id = "M-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Mahasiswa mahasiswaBaru = new Mahasiswa(id, nama.trim(), username.trim(), password, nim.trim(), prodi == null ? "-" : prodi.trim());

        // OOP: Exception Handling - FirebaseException dilempar jika gagal terhubung/menulis ke Firestore
        dataStore.tambahPenggunaKeFirestore(mahasiswaBaru);

        return mahasiswaBaru;
    }

    /**
     * Dipanggil oleh Admin (setelah login) untuk membuat akun Petugas baru
     * langsung dari dalam aplikasi, lewat menu Manajemen Anggota.
     *
     * OOP: Exception Handling - validasi & FirebaseException diteruskan ke pemanggil
     */
    public Petugas buatAkunPetugas(String nama, String username, String password, String shift)
            throws FirebaseException, IllegalArgumentException {

        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama tidak boleh kosong.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter.");
        }

        cekUsernameTersedia(username);

        String id = "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Petugas petugasBaru = new Petugas(id, nama.trim(), username.trim(), password,
                shift == null || shift.trim().isEmpty() ? "-" : shift.trim());

        dataStore.tambahPenggunaKeFirestore(petugasBaru);
        return petugasBaru;
    }

    /**
     * Validasi username belum dipakai oleh akun manapun (Admin/Petugas/Mahasiswa).
     * OOP: Exception Handling
     */
    private void cekUsernameTersedia(String username) throws IllegalArgumentException {
        for (Pengguna p : dataStore.getDaftarPengguna()) {
            if (p.getUsername().equalsIgnoreCase(username.trim())) {
                throw new IllegalArgumentException("Username \"" + username + "\" sudah digunakan. Silakan pilih username lain.");
            }
        }
    }

    public void logout() {
        if (penggunaSaatIni != null) {
            penggunaSaatIni.kirimPesan("Logout dari sistem.");
        }
        penggunaSaatIni = null;
    }

    public Pengguna getPenggunaSaatIni() { return penggunaSaatIni; }
}