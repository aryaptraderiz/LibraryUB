package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Class Peminjaman
 * OOP Concept: Encapsulation, Association (relasi dengan Buku & Mahasiswa)
 */
public class Peminjaman {

    public enum StatusPeminjaman {
        AKTIF, DIKEMBALIKAN, DIBATALKAN
    }

    private String id;
    private Mahasiswa mahasiswa;
    private Buku buku;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalKembaliRencana;
    private LocalDate tanggalKembaliAktual;
    private StatusPeminjaman statusPeminjaman;
    private double denda;

    private static final int BATAS_HARI = 7;       // Batas pinjam 7 hari
    private static final double DENDA_PER_HARI = 2000; // Denda Rp 2.000/hari

    /**
     * Constructor untuk peminjaman baru.
     * ID dibuat menggunakan UUID (bukan counter statis) karena data disimpan
     * di Firebase Firestore yang bisa diakses dari banyak sesi aplikasi sekaligus;
     * counter statis akan reset tiap aplikasi dijalankan ulang dan berisiko bentrok ID.
     */
    public Peminjaman(Mahasiswa mahasiswa, Buku buku) {
        this.id = "PJM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.mahasiswa = mahasiswa;
        this.buku = buku;
        this.tanggalPinjam = LocalDate.now();
        this.tanggalKembaliRencana = tanggalPinjam.plusDays(BATAS_HARI);
        this.statusPeminjaman = StatusPeminjaman.AKTIF;
        this.denda = 0;
    }

    /**
     * Constructor lengkap, dipakai untuk merekonstruksi objek dari data Firestore.
     * OOP: Constructor Overloading
     */
    public Peminjaman(String id, Mahasiswa mahasiswa, Buku buku, LocalDate tanggalPinjam,
                       LocalDate tanggalKembaliRencana, LocalDate tanggalKembaliAktual,
                       StatusPeminjaman statusPeminjaman, double denda) {
        this.id = id;
        this.mahasiswa = mahasiswa;
        this.buku = buku;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembaliRencana = tanggalKembaliRencana;
        this.tanggalKembaliAktual = tanggalKembaliAktual;
        this.statusPeminjaman = statusPeminjaman;
        this.denda = denda;
    }

    /**
     * Hitung keterlambatan dalam hari
     * OOP: Method dengan logika bisnis
     */
    public long hitungKeterlambatan() {
        LocalDate tanggalCek = (tanggalKembaliAktual != null) ? tanggalKembaliAktual : LocalDate.now();
        long terlambat = ChronoUnit.DAYS.between(tanggalKembaliRencana, tanggalCek);
        return Math.max(0, terlambat);
    }

    /**
     * Hitung denda otomatis
     */
    public double hitungDenda() {
        return hitungKeterlambatan() * DENDA_PER_HARI;
    }

    /**
     * Proses pengembalian buku
     */
    public void kembalikan() {
        this.tanggalKembaliAktual = LocalDate.now();
        this.denda = hitungDenda();
        this.statusPeminjaman = StatusPeminjaman.DIKEMBALIKAN;
        this.buku.kembalikan();
    }

    /**
     * Batalkan peminjaman
     */
    public void batalkan() {
        this.statusPeminjaman = StatusPeminjaman.DIBATALKAN;
        this.buku.kembalikan();
    }

    // Getters
    public String getId() { return id; }
    public Mahasiswa getMahasiswa() { return mahasiswa; }
    public Buku getBuku() { return buku; }
    public LocalDate getTanggalPinjam() { return tanggalPinjam; }
    public LocalDate getTanggalKembaliRencana() { return tanggalKembaliRencana; }
    public LocalDate getTanggalKembaliAktual() { return tanggalKembaliAktual; }
    public StatusPeminjaman getStatusPeminjaman() { return statusPeminjaman; }
    public double getDenda() { return denda; }

    public static int getBATAS_HARI() { return BATAS_HARI; }
    public static double getDENDA_PER_HARI() { return DENDA_PER_HARI; }
}
