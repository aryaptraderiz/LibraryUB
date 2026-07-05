package service;

import exception.FirebaseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.*;

/**
 * AnggotaService - mengelola data anggota (Mahasiswa), tersinkron dengan Firestore
 * OOP Concept: Encapsulation, Collection, Polymorphism, Exception Handling
 */
public class AnggotaService implements model.Searchable<Mahasiswa> {

    private DataStore dataStore;

    public AnggotaService() {
        this.dataStore = DataStore.getInstance();
    }

    public void tambahAnggota(Mahasiswa mahasiswa) throws FirebaseException {
        dataStore.tambahPenggunaKeFirestore(mahasiswa);
    }

    public boolean editAnggota(String id, String nama, String nim, String prodi) throws FirebaseException {
        Mahasiswa m = cariAnggotaById(id);
        if (m != null) {
            m.setNama(nama);
            m.setNim(nim);
            m.setProdi(prodi);
            dataStore.updatePenggunaKeFirestore(m);
            return true;
        }
        return false;
    }

    public boolean hapusAnggota(String id) throws FirebaseException {
        if (cariAnggotaById(id) == null) return false;
        dataStore.hapusPenggunaDariFirestore(id);
        return true;
    }

    public Mahasiswa cariAnggotaById(String id) {
        for (Pengguna p : dataStore.getDaftarPengguna()) {
            if (p instanceof Mahasiswa && p.getId().equals(id)) {
                return (Mahasiswa) p;
            }
        }
        return null;
    }

    public Mahasiswa cariAnggotaByUsername(String username) {
        for (Pengguna p : dataStore.getDaftarPengguna()) {
            if (p instanceof Mahasiswa && p.getUsername().equalsIgnoreCase(username)) {
                return (Mahasiswa) p;
            }
        }
        return null;
    }

    public List<Mahasiswa> getDaftarMahasiswa() {
        List<Mahasiswa> list = new ArrayList<>();
        for (Pengguna p : dataStore.getDaftarPengguna()) {
            // OOP: instanceof + casting (Polymorphism)
            if (p instanceof Mahasiswa) {
                list.add((Mahasiswa) p);
            }
        }
        return list;
    }

    /**
     * Mengambil daftar Petugas. Dipakai oleh Admin di menu Manajemen Anggota
     * untuk menampilkan & mengelola akun Petugas selain Mahasiswa.
     */
    public List<Petugas> getDaftarPetugas() {
        List<Petugas> list = new ArrayList<>();
        for (Pengguna p : dataStore.getDaftarPengguna()) {
            // OOP: instanceof + casting (Polymorphism)
            if (p instanceof Petugas) {
                list.add((Petugas) p);
            }
        }
        return list;
    }

    public Petugas cariPetugasById(String id) {
        for (Pengguna p : dataStore.getDaftarPengguna()) {
            if (p instanceof Petugas && p.getId().equals(id)) {
                return (Petugas) p;
            }
        }
        return null;
    }

    public boolean editPetugas(String id, String nama, String shift) throws FirebaseException {
        Petugas p = cariPetugasById(id);
        if (p != null) {
            p.setNama(nama);
            p.setShift(shift);
            dataStore.updatePenggunaKeFirestore(p);
            return true;
        }
        return false;
    }

    public boolean hapusPetugas(String id) throws FirebaseException {
        if (cariPetugasById(id) == null) return false;
        dataStore.hapusPenggunaDariFirestore(id);
        return true;
    }

    /**
     * OOP: Interface Searchable
     */
    @Override
    public List<Mahasiswa> cari(String keyword) {
        if (keyword == null || keyword.isEmpty()) return getDaftarMahasiswa();
        String kw = keyword.toLowerCase();
        List<Mahasiswa> hasil = new ArrayList<>();
        for (Mahasiswa m : getDaftarMahasiswa()) {
            if (m.getNama().toLowerCase().contains(kw) ||
                m.getNim().toLowerCase().contains(kw) ||
                m.getProdi().toLowerCase().contains(kw)) {
                hasil.add(m);
            }
        }
        return hasil;
    }

    /**
     * Generate ID unik menggunakan UUID (aman untuk data di cloud / multi-sesi).
     */
    public String generateId() {
        return "M-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}