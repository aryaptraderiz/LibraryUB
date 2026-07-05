package service;

import exception.FirebaseException;
import model.*;
import util.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DataStore - cache lokal (Collection) yang disinkronkan dengan Firebase Firestore.
 *
 * OOP Concept: Encapsulation, Collection (ArrayList), Singleton Pattern,
 *              Exception Handling, Polymorphism (instanceof saat sinkronisasi Pengguna)
 *
 * Strategi: data dimuat sekali dari Firestore saat aplikasi start (loadFromFirestore),
 * lalu setiap perubahan (tambah/edit/hapus) langsung ditulis balik ke Firestore
 * oleh service terkait (write-through), sekaligus diperbarui di ArrayList lokal
 * supaya tampilan GUI tetap responsif tanpa perlu fetch ulang setiap saat.
 */
public class DataStore {

    private static DataStore instance;

    // Nama collection di Firestore
    public static final String COLLECTION_PENGGUNA = "pengguna";
    public static final String COLLECTION_BUKU = "buku";
    public static final String COLLECTION_PEMINJAMAN = "peminjaman";

    // OOP: Collection - menyimpan objek-objek sebagai cache lokal
    private List<Pengguna> daftarPengguna;
    private List<Buku> daftarBuku;
    private List<Peminjaman> daftarPeminjaman;

    private FirestoreClient firestoreClient;
    private boolean sudahDimuat = false;

    private DataStore() {
        daftarPengguna = new ArrayList<>();
        daftarBuku = new ArrayList<>();
        daftarPeminjaman = new ArrayList<>();
        firestoreClient = FirestoreClient.getInstance();
    }

    // Singleton Pattern
    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /**
     * Memuat seluruh data dari Firestore ke cache lokal.
     * Jika koleksi 'pengguna' & 'buku' masih kosong di Firestore (project baru),
     * otomatis diisi data awal (seed) agar aplikasi tetap bisa langsung dipakai/didemokan.
     *
     * OOP: Exception Handling - melempar FirebaseException ke pemanggil (misalnya LoginFrame)
     * supaya UI bisa menampilkan pesan error yang jelas jika koneksi gagal.
     */
    public void loadFromFirestore() throws FirebaseException {
        daftarPengguna.clear();
        daftarBuku.clear();
        daftarPeminjaman.clear();

        // --- Load Pengguna ---
        Map<String, JsonObject> penggunaDocs = firestoreClient.getCollection(COLLECTION_PENGGUNA);
        for (Map.Entry<String, JsonObject> entry : penggunaDocs.entrySet()) {
            Pengguna p = jsonToPengguna(entry.getKey(), entry.getValue());
            if (p != null) daftarPengguna.add(p);
        }

        // --- Load Buku ---
        Map<String, JsonObject> bukuDocs = firestoreClient.getCollection(COLLECTION_BUKU);
        for (Map.Entry<String, JsonObject> entry : bukuDocs.entrySet()) {
            Buku b = jsonToBuku(entry.getKey(), entry.getValue());
            if (b != null) daftarBuku.add(b);
        }

        // --- Load Peminjaman ---
        Map<String, JsonObject> peminjamanDocs = firestoreClient.getCollection(COLLECTION_PEMINJAMAN);
        for (Map.Entry<String, JsonObject> entry : peminjamanDocs.entrySet()) {
            Peminjaman p = jsonToPeminjaman(entry.getValue());
            if (p != null) daftarPeminjaman.add(p);
        }

        // Jika Firestore masih kosong (project baru pertama kali dipakai), seed data awal
        if (daftarPengguna.isEmpty() && daftarBuku.isEmpty()) {
            seedDataAwal();
        }

        sudahDimuat = true;
    }

    public boolean isSudahDimuat() {
        return sudahDimuat;
    }

    /**
     * Mengisi data awal ke Firestore (dipanggil otomatis sekali saja saat database kosong).
     */
    private void seedDataAwal() throws FirebaseException {
        tambahPenggunaKeFirestore(new Admin("A001", "Dr. Budi Santoso", "admin", "admin123", "Kepala Perpustakaan"));
        tambahPenggunaKeFirestore(new Petugas("P001", "Siti Rahayu", "petugas", "petugas123", "Pagi"));
        tambahPenggunaKeFirestore(new Petugas("P002", "Andi Wijaya", "petugas2", "petugas123", "Siang"));
        tambahPenggunaKeFirestore(new Mahasiswa("M001", "Arya Pratama", "arya", "mhs123", "1102213001", "Teknik Informatika"));
        tambahPenggunaKeFirestore(new Mahasiswa("M002", "Dewi Lestari", "dewi", "mhs123", "1102213002", "Sistem Informasi"));
        tambahPenggunaKeFirestore(new Mahasiswa("M003", "Rizky Fajar", "rizky", "mhs123", "1102213003", "Teknik Informatika"));

        // ── TEKNOLOGI (14 buku) ──
        tambahBukuKeFirestore(new Buku("B001", "Pemrograman Java untuk Pemula", "Eko Kurniawan", "Elex Media", 2022, "978-602-001", KategoriBuku.TEKNOLOGI, 3));
        tambahBukuKeFirestore(new Buku("B002", "Algoritma dan Struktur Data", "Rinaldi Munir", "Informatika", 2021, "978-602-002", KategoriBuku.TEKNOLOGI, 2));
        tambahBukuKeFirestore(new Buku("B006", "Basis Data", "Fathansyah", "Informatika", 2022, "978-602-006", KategoriBuku.TEKNOLOGI, 5));
        tambahBukuKeFirestore(new Buku("B010", "Jaringan Komputer", "Andrew Tanenbaum", "Andi Offset", 2022, "978-602-010", KategoriBuku.TEKNOLOGI, 4));
        tambahBukuKeFirestore(new Buku("B011", "Pemrograman Python Modern", "Farid Azis", "Informatika", 2023, "978-602-011", KategoriBuku.TEKNOLOGI, 3));
        tambahBukuKeFirestore(new Buku("B012", "Rekayasa Perangkat Lunak", "Roger Pressman", "Andi Offset", 2021, "978-602-012", KategoriBuku.TEKNOLOGI, 2));
        tambahBukuKeFirestore(new Buku("B013", "Kecerdasan Buatan", "Suyanto", "Informatika", 2020, "978-602-013", KategoriBuku.TEKNOLOGI, 4));
        tambahBukuKeFirestore(new Buku("B014", "Keamanan Sistem Informasi", "Dony Ariyus", "Andi Offset", 2022, "978-602-014", KategoriBuku.TEKNOLOGI, 2));
        tambahBukuKeFirestore(new Buku("B015", "Cloud Computing", "Rudi Rusdiah", "Elex Media", 2023, "978-602-015", KategoriBuku.TEKNOLOGI, 3));
        tambahBukuKeFirestore(new Buku("B016", "Pemrograman Web dengan PHP", "Betha Sidik", "Informatika", 2021, "978-602-016", KategoriBuku.TEKNOLOGI, 4));
        tambahBukuKeFirestore(new Buku("B017", "Machine Learning dengan Python", "Aurélien Géron", "Andi Offset", 2022, "978-602-017", KategoriBuku.TEKNOLOGI, 2));
        tambahBukuKeFirestore(new Buku("B018", "Sistem Operasi Modern", "Andrew Tanenbaum", "Andi Offset", 2020, "978-602-018", KategoriBuku.TEKNOLOGI, 3));
        tambahBukuKeFirestore(new Buku("B019", "Desain UI/UX Mobile App", "Laura Klein", "Elex Media", 2023, "978-602-019", KategoriBuku.TEKNOLOGI, 5));
        tambahBukuKeFirestore(new Buku("B020", "Internet of Things", "Endang Kurniawan", "Informatika", 2022, "978-602-020", KategoriBuku.TEKNOLOGI, 3));
        // ── BISNIS (10 buku) ──
        tambahBukuKeFirestore(new Buku("B003", "Manajemen Strategik", "Fred David", "Salemba", 2020, "978-602-003", KategoriBuku.BISNIS, 4));
        tambahBukuKeFirestore(new Buku("B008", "Pemasaran Digital", "Philip Kotler", "Erlangga", 2023, "978-602-008", KategoriBuku.BISNIS, 3));
        tambahBukuKeFirestore(new Buku("B021", "Manajemen Sumber Daya Manusia", "Gary Dessler", "Salemba", 2021, "978-602-021", KategoriBuku.BISNIS, 3));
        tambahBukuKeFirestore(new Buku("B022", "Kewirausahaan dan Inovasi", "Bygrave & Zacharakis", "Erlangga", 2022, "978-602-022", KategoriBuku.BISNIS, 4));
        tambahBukuKeFirestore(new Buku("B023", "Manajemen Keuangan", "Eugene Brigham", "Salemba", 2020, "978-602-023", KategoriBuku.BISNIS, 2));
        tambahBukuKeFirestore(new Buku("B024", "Perilaku Konsumen", "Schiffman & Kanuk", "Erlangga", 2021, "978-602-024", KategoriBuku.BISNIS, 3));
        tambahBukuKeFirestore(new Buku("B025", "Manajemen Operasi", "Jay Heizer", "Salemba", 2022, "978-602-025", KategoriBuku.BISNIS, 2));
        tambahBukuKeFirestore(new Buku("B026", "Bisnis Internasional", "Charles Hill", "Salemba", 2021, "978-602-026", KategoriBuku.BISNIS, 3));
        tambahBukuKeFirestore(new Buku("B027", "Kepemimpinan dan Motivasi", "Stephen Robbins", "Erlangga", 2022, "978-602-027", KategoriBuku.BISNIS, 4));
        tambahBukuKeFirestore(new Buku("B028", "Supply Chain Management", "Sunil Chopra", "Salemba", 2020, "978-602-028", KategoriBuku.BISNIS, 2));
        // ── EKONOMI (10 buku) ──
        tambahBukuKeFirestore(new Buku("B004", "Ekonomi Mikro", "N. Gregory Mankiw", "Erlangga", 2019, "978-602-004", KategoriBuku.EKONOMI, 2));
        tambahBukuKeFirestore(new Buku("B007", "Akuntansi Dasar", "Hery", "Grasindo", 2020, "978-602-007", KategoriBuku.EKONOMI, 2));
        tambahBukuKeFirestore(new Buku("B029", "Ekonomi Makro", "N. Gregory Mankiw", "Erlangga", 2020, "978-602-029", KategoriBuku.EKONOMI, 3));
        tambahBukuKeFirestore(new Buku("B030", "Pengantar Ilmu Ekonomi", "Pratama Rahardja", "FEUI Press", 2021, "978-602-030", KategoriBuku.EKONOMI, 4));
        tambahBukuKeFirestore(new Buku("B031", "Akuntansi Manajemen", "Hansen & Mowen", "Salemba", 2021, "978-602-031", KategoriBuku.EKONOMI, 3));
        tambahBukuKeFirestore(new Buku("B032", "Pasar Modal Indonesia", "Eduardus Tandelilin", "UPP STIM YKPN", 2022, "978-602-032", KategoriBuku.EKONOMI, 2));
        tambahBukuKeFirestore(new Buku("B033", "Perpajakan Indonesia", "Mardiasmo", "Andi Offset", 2021, "978-602-033", KategoriBuku.EKONOMI, 3));
        tambahBukuKeFirestore(new Buku("B034", "Statistika Ekonomi", "Supranto", "Erlangga", 2020, "978-602-034", KategoriBuku.EKONOMI, 4));
        tambahBukuKeFirestore(new Buku("B035", "Ekonomi Pembangunan", "Arsyad Lincolin", "BPFE", 2022, "978-602-035", KategoriBuku.EKONOMI, 2));
        tambahBukuKeFirestore(new Buku("B036", "Audit Keuangan", "Mulyadi", "Salemba", 2021, "978-602-036", KategoriBuku.EKONOMI, 3));
        // ── HUKUM (6 buku) ──
        tambahBukuKeFirestore(new Buku("B005", "Hukum Bisnis Indonesia", "Zainal Asikin", "Rajawali Pers", 2021, "978-602-005", KategoriBuku.HUKUM, 3));
        tambahBukuKeFirestore(new Buku("B009", "Hukum Perdata", "Sudikno Mertokusumo", "Liberty", 2018, "978-602-009", KategoriBuku.HUKUM, 1));
        tambahBukuKeFirestore(new Buku("B037", "Hukum Pidana Indonesia", "Moeljatno", "Bumi Aksara", 2020, "978-602-037", KategoriBuku.HUKUM, 2));
        tambahBukuKeFirestore(new Buku("B038", "Hukum Kontrak", "Salim HS", "Rajawali Pers", 2021, "978-602-038", KategoriBuku.HUKUM, 3));
        tambahBukuKeFirestore(new Buku("B039", "Hukum Ketenagakerjaan", "Lalu Husni", "Rajawali Pers", 2020, "978-602-039", KategoriBuku.HUKUM, 2));
        tambahBukuKeFirestore(new Buku("B040", "Hukum Tata Negara Indonesia", "Jimly Asshiddiqie", "Konstitusi Press", 2022, "978-602-040", KategoriBuku.HUKUM, 4));
    }

    // ============== WRITE-THROUGH KE FIRESTORE ==============

    public void tambahPenggunaKeFirestore(Pengguna p) throws FirebaseException {
        firestoreClient.setDocument(COLLECTION_PENGGUNA, p.getId(), penggunaToJson(p));
        if (!daftarPengguna.contains(p)) daftarPengguna.add(p);
    }

    public void updatePenggunaKeFirestore(Pengguna p) throws FirebaseException {
        firestoreClient.setDocument(COLLECTION_PENGGUNA, p.getId(), penggunaToJson(p));
    }

    public void hapusPenggunaDariFirestore(String id) throws FirebaseException {
        firestoreClient.deleteDocument(COLLECTION_PENGGUNA, id);
        daftarPengguna.removeIf(p -> p.getId().equals(id));
    }

    public void tambahBukuKeFirestore(Buku b) throws FirebaseException {
        firestoreClient.setDocument(COLLECTION_BUKU, b.getId(), bukuToJson(b));
        if (!daftarBuku.contains(b)) daftarBuku.add(b);
    }

    public void updateBukuKeFirestore(Buku b) throws FirebaseException {
        firestoreClient.setDocument(COLLECTION_BUKU, b.getId(), bukuToJson(b));
    }

    public void hapusBukuDariFirestore(String id) throws FirebaseException {
        firestoreClient.deleteDocument(COLLECTION_BUKU, id);
        daftarBuku.removeIf(b -> b.getId().equals(id));
    }

    public void tambahPeminjamanKeFirestore(Peminjaman p) throws FirebaseException {
        firestoreClient.setDocument(COLLECTION_PEMINJAMAN, p.getId(), peminjamanToJson(p));
        if (!daftarPeminjaman.contains(p)) daftarPeminjaman.add(p);
    }

    public void updatePeminjamanKeFirestore(Peminjaman p) throws FirebaseException {
        firestoreClient.setDocument(COLLECTION_PEMINJAMAN, p.getId(), peminjamanToJson(p));
    }

    // ============== KONVERSI OBJECT <-> JSON ==============

    private JsonObject penggunaToJson(Pengguna p) {
        JsonObject json = new JsonObject();
        json.put("id", p.getId());
        json.put("nama", p.getNama());
        json.put("username", p.getUsername());
        json.put("password", p.getPassword());
        json.put("role", p.getRole().name());

        // OOP: Polymorphism - field tambahan tergantung tipe konkret
        if (p instanceof Admin) {
            json.put("jabatan", ((Admin) p).getJabatan());
        } else if (p instanceof Petugas) {
            json.put("shift", ((Petugas) p).getShift());
        } else if (p instanceof Mahasiswa) {
            Mahasiswa m = (Mahasiswa) p;
            json.put("nim", m.getNim());
            json.put("prodi", m.getProdi());
            json.put("maxPinjam", m.getMaxPinjam());
        }
        return json;
    }

    private Pengguna jsonToPengguna(String docId, JsonObject json) {
        String id = json.has("id") ? json.getString("id") : docId;
        String nama = json.getString("nama");
        String username = json.getString("username");
        String password = json.getString("password");
        String roleStr = json.getString("role");

        if (roleStr == null) return null;
        RoleUser role = RoleUser.valueOf(roleStr);

        switch (role) {
            case ADMIN:
                return new Admin(id, nama, username, password,
                        json.has("jabatan") ? json.getString("jabatan") : "-");
            case PETUGAS:
                return new Petugas(id, nama, username, password,
                        json.has("shift") ? json.getString("shift") : "-");
            case MAHASISWA:
                Mahasiswa m = new Mahasiswa(id, nama, username, password,
                        json.has("nim") ? json.getString("nim") : "-",
                        json.has("prodi") ? json.getString("prodi") : "-");
                if (json.has("maxPinjam")) m.setMaxPinjam(json.getInt("maxPinjam"));
                return m;
            default:
                return null;
        }
    }

    private JsonObject bukuToJson(Buku b) {
        JsonObject json = new JsonObject();
        json.put("id", b.getId());
        json.put("judul", b.getJudul());
        json.put("pengarang", b.getPengarang());
        json.put("penerbit", b.getPenerbit());
        json.put("tahunTerbit", b.getTahunTerbit());
        json.put("isbn", b.getIsbn());
        json.put("kategori", b.getKategori().name());
        json.put("status", b.getStatus().name());
        json.put("stok", b.getStok());
        json.put("totalDipinjam", b.getTotalDipinjam());
        return json;
    }

    private Buku jsonToBuku(String docId, JsonObject json) {
        String id = json.has("id") ? json.getString("id") : docId;
        Buku b = new Buku(
                id,
                json.getString("judul"),
                json.getString("pengarang"),
                json.getString("penerbit"),
                json.getInt("tahunTerbit"),
                json.getString("isbn"),
                KategoriBuku.valueOf(json.getString("kategori")),
                json.getInt("stok")
        );
        if (json.has("status")) {
            b.setStatus(StatusBuku.valueOf(json.getString("status")));
        }
        return b;
    }

    private JsonObject peminjamanToJson(Peminjaman p) {
        JsonObject json = new JsonObject();
        json.put("id", p.getId());
        json.put("mahasiswaId", p.getMahasiswa().getId());
        json.put("bukuId", p.getBuku().getId());
        json.put("tanggalPinjam", p.getTanggalPinjam().toString());
        json.put("tanggalKembaliRencana", p.getTanggalKembaliRencana().toString());
        json.put("tanggalKembaliAktual", p.getTanggalKembaliAktual() != null ? p.getTanggalKembaliAktual().toString() : null);
        json.put("statusPeminjaman", p.getStatusPeminjaman().name());
        json.put("denda", p.getDenda());
        return json;
    }

    private Peminjaman jsonToPeminjaman(JsonObject json) {
        try {
            String mahasiswaId = json.getString("mahasiswaId");
            String bukuId = json.getString("bukuId");

            Mahasiswa mahasiswa = null;
            for (Pengguna p : daftarPengguna) {
                if (p instanceof Mahasiswa && p.getId().equals(mahasiswaId)) {
                    mahasiswa = (Mahasiswa) p;
                    break;
                }
            }
            Buku buku = null;
            for (Buku b : daftarBuku) {
                if (b.getId().equals(bukuId)) {
                    buku = b;
                    break;
                }
            }
            if (mahasiswa == null) {
                System.err.println("[DataStore] Peminjaman dilewati: mahasiswa dengan id '" + mahasiswaId + "' tidak ditemukan.");
                return null;
            }
            if (buku == null) {
                System.err.println("[DataStore] Peminjaman dilewati: buku dengan id '" + bukuId + "' tidak ditemukan.");
                return null;
            }

            Peminjaman p = new Peminjaman(json.getString("id"), mahasiswa, buku,
                    java.time.LocalDate.parse(json.getString("tanggalPinjam")),
                    java.time.LocalDate.parse(json.getString("tanggalKembaliRencana")),
                    json.has("tanggalKembaliAktual") && json.get("tanggalKembaliAktual") != null
                            ? java.time.LocalDate.parse(json.getString("tanggalKembaliAktual")) : null,
                    Peminjaman.StatusPeminjaman.valueOf(json.getString("statusPeminjaman")),
                    json.has("denda") ? json.getDouble("denda") : 0
            );
            return p;
        } catch (Exception e) {
            System.err.println("[DataStore] Gagal mem-parsing data peminjaman: " + e.getMessage());
            return null;
        }
    }

    // --- Getters ---
    public List<Pengguna> getDaftarPengguna() { return daftarPengguna; }
    public List<Buku> getDaftarBuku() { return daftarBuku; }
    public List<Peminjaman> getDaftarPeminjaman() { return daftarPeminjaman; }
}