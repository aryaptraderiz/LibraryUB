package service;

import exception.FirebaseException;
import model.Buku;
import model.KategoriBuku;
import model.Searchable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * BukuService - mengelola operasi CRUD buku, tersinkron dengan Firestore
 * OOP Concept: Encapsulation, Collection, Interface Implementation (Searchable),
 *              Exception Handling
 */
public class BukuService implements Searchable<Buku> {

    private DataStore dataStore;

    public BukuService() {
        this.dataStore = DataStore.getInstance();
    }

    public void tambahBuku(Buku buku) throws FirebaseException {
        dataStore.tambahBukuKeFirestore(buku);
    }

    public boolean editBuku(String id, String judul, String pengarang, String penerbit,
                            int tahun, String isbn, KategoriBuku kategori, int stok) throws FirebaseException {
        Buku buku = cariBukuById(id);
        if (buku != null) {
            buku.setJudul(judul);
            buku.setPengarang(pengarang);
            buku.setPenerbit(penerbit);
            buku.setTahunTerbit(tahun);
            buku.setIsbn(isbn);
            buku.setKategori(kategori);
            buku.setStok(stok);
            dataStore.updateBukuKeFirestore(buku);
            return true;
        }
        return false;
    }

    public boolean hapusBuku(String id) throws FirebaseException {
        if (cariBukuById(id) == null) return false;
        dataStore.hapusBukuDariFirestore(id);
        return true;
    }

    public Buku cariBukuById(String id) {
        for (Buku b : dataStore.getDaftarBuku()) {
            if (b.getId().equals(id)) return b;
        }
        return null;
    }

    /**
     * OOP: Interface Searchable - implementasi cari()
     * Cari buku berdasarkan judul, pengarang, atau ISBN
     */
    @Override
    public List<Buku> cari(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>(dataStore.getDaftarBuku());
        }
        String kw = keyword.toLowerCase();
        List<Buku> hasil = new ArrayList<>();
        for (Buku b : dataStore.getDaftarBuku()) {
            if (b.getJudul().toLowerCase().contains(kw) ||
                b.getPengarang().toLowerCase().contains(kw) ||
                b.getIsbn().toLowerCase().contains(kw) ||
                b.getKategori().getLabel().toLowerCase().contains(kw)) {
                hasil.add(b);
            }
        }
        return hasil;
    }

    public List<Buku> getDaftarBuku() {
        return dataStore.getDaftarBuku();
    }

    /**
     * Laporan: buku paling sering dipinjam
     */
    public List<Buku> getBukuTerpopuler() {
        List<Buku> sorted = new ArrayList<>(dataStore.getDaftarBuku());
        sorted.sort((a, b) -> b.getTotalDipinjam() - a.getTotalDipinjam());
        return sorted;
    }

    /**
     * Generate ID unik menggunakan UUID, aman untuk data yang tersimpan di cloud
     * (tidak akan bentrok meski aplikasi dijalankan dari beberapa komputer berbeda).
     */
    public String generateId() {
        return "B-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
