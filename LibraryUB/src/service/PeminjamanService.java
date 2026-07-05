package service;

import exception.BukuTidakTersediaException;
import exception.FirebaseException;
import exception.MelebihiBatasPinjamException;
import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PeminjamanService - mengelola peminjaman dan pengembalian buku, tersinkron Firestore
 * OOP Concept: Exception Handling, Collection, Association
 */
public class PeminjamanService {

    private DataStore dataStore;

    public PeminjamanService() {
        this.dataStore = DataStore.getInstance();
    }

    /**
     * Proses pinjam buku
     * OOP: Exception Handling - melempar custom exception
     */
    public Peminjaman pinjamBuku(Mahasiswa mahasiswa, Buku buku)
            throws BukuTidakTersediaException, MelebihiBatasPinjamException, FirebaseException {

        // Cek stok buku
        if (!buku.tersedia()) {
            throw new BukuTidakTersediaException(buku.getJudul());
        }

        // Cek batas pinjam mahasiswa
        int jumlahAktif = hitungPeminjamanAktif(mahasiswa);
        if (jumlahAktif >= mahasiswa.getMaxPinjam()) {
            throw new MelebihiBatasPinjamException(mahasiswa.getNama(), mahasiswa.getMaxPinjam());
        }

        // Proses peminjaman
        buku.pinjam();
        Peminjaman peminjaman = new Peminjaman(mahasiswa, buku);

        // Tulis ke Firestore: peminjaman baru + update stok buku
        dataStore.tambahPeminjamanKeFirestore(peminjaman);
        dataStore.updateBukuKeFirestore(buku);

        // OOP: Polymorphism - kirimPesan (berbeda tiap subclass Pengguna)
        mahasiswa.kirimPesan("Berhasil meminjam buku: " + buku.getJudul() +
                ". Harap dikembalikan sebelum " + peminjaman.getTanggalKembaliRencana());

        return peminjaman;
    }

    /**
     * Proses pengembalian buku
     */
    public double kembalikanBuku(String idPeminjaman) throws FirebaseException {
        Peminjaman p = cariPeminjamanById(idPeminjaman);
        if (p != null && p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF) {
            p.kembalikan();
            double denda = p.getDenda();

            // Tulis perubahan ke Firestore
            dataStore.updatePeminjamanKeFirestore(p);
            dataStore.updateBukuKeFirestore(p.getBuku());

            String pesanNotif = "Buku \"" + p.getBuku().getJudul() + "\" berhasil dikembalikan.";
            if (denda > 0) {
                pesanNotif += " Denda keterlambatan: Rp " + String.format("%,.0f", denda);
            }
            p.getMahasiswa().kirimPesan(pesanNotif);
            return denda;
        }
        return 0;
    }

    /**
     * Batalkan peminjaman
     */
    public boolean batalkanPeminjaman(String idPeminjaman) throws FirebaseException {
        Peminjaman p = cariPeminjamanById(idPeminjaman);
        if (p != null && p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF) {
            p.batalkan();

            dataStore.updatePeminjamanKeFirestore(p);
            dataStore.updateBukuKeFirestore(p.getBuku());

            p.getMahasiswa().kirimPesan("Peminjaman buku \"" + p.getBuku().getJudul() + "\" dibatalkan.");
            return true;
        }
        return false;
    }

    public Peminjaman cariPeminjamanById(String id) {
        for (Peminjaman p : dataStore.getDaftarPeminjaman()) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public List<Peminjaman> getDaftarPeminjaman() {
        return dataStore.getDaftarPeminjaman();
    }

    public List<Peminjaman> getPeminjamanByMahasiswa(Mahasiswa mahasiswa) {
        List<Peminjaman> hasil = new ArrayList<>();
        for (Peminjaman p : dataStore.getDaftarPeminjaman()) {
            if (p.getMahasiswa().getId().equals(mahasiswa.getId())) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    public List<Peminjaman> getPeminjamanAktif() {
        List<Peminjaman> hasil = new ArrayList<>();
        for (Peminjaman p : dataStore.getDaftarPeminjaman()) {
            if (p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    public List<Peminjaman> getDaftarTerlambat() {
        List<Peminjaman> hasil = new ArrayList<>();
        for (Peminjaman p : dataStore.getDaftarPeminjaman()) {
            if (p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF &&
                p.hitungKeterlambatan() > 0) {
                hasil.add(p);
            }
        }
        return hasil;
    }

    private int hitungPeminjamanAktif(Mahasiswa mahasiswa) {
        int count = 0;
        for (Peminjaman p : dataStore.getDaftarPeminjaman()) {
            if (p.getMahasiswa().getId().equals(mahasiswa.getId()) &&
                p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF) {
                count++;
            }
        }
        return count;
    }

    public double getTotalDenda() {
        double total = 0;
        for (Peminjaman p : dataStore.getDaftarPeminjaman()) {
            total += p.getDenda();
            if (p.getStatusPeminjaman() == Peminjaman.StatusPeminjaman.AKTIF) {
                total += p.hitungDenda();
            }
        }
        return total;
    }
}
