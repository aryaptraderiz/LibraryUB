package model;

/**
 * Class Buku
 * OOP Concept: Encapsulation, Object
 */
public class Buku {

    // OOP: Encapsulation - semua field private
    private String id;
    private String judul;
    private String pengarang;
    private String penerbit;
    private int tahunTerbit;
    private String isbn;
    private KategoriBuku kategori;
    private StatusBuku status;
    private int stok;
    private int totalDipinjam; // untuk laporan buku terpopuler

    public Buku(String id, String judul, String pengarang, String penerbit,
                int tahunTerbit, String isbn, KategoriBuku kategori, int stok) {
        this.id = id;
        this.judul = judul;
        this.pengarang = pengarang;
        this.penerbit = penerbit;
        this.tahunTerbit = tahunTerbit;
        this.isbn = isbn;
        this.kategori = kategori;
        this.status = StatusBuku.TERSEDIA;
        this.stok = stok;
        this.totalDipinjam = 0;
    }

    // Method bisnis
    public boolean tersedia() {
        return stok > 0 && status == StatusBuku.TERSEDIA;
    }

    public void pinjam() {
        if (stok > 0) {
            stok--;
            totalDipinjam++;
            if (stok == 0) {
                status = StatusBuku.DIPINJAM;
            }
        }
    }

    public void kembalikan() {
        stok++;
        if (stok > 0) {
            status = StatusBuku.TERSEDIA;
        }
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getPengarang() { return pengarang; }
    public void setPengarang(String pengarang) { this.pengarang = pengarang; }

    public String getPenerbit() { return penerbit; }
    public void setPenerbit(String penerbit) { this.penerbit = penerbit; }

    public int getTahunTerbit() { return tahunTerbit; }
    public void setTahunTerbit(int tahunTerbit) { this.tahunTerbit = tahunTerbit; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public KategoriBuku getKategori() { return kategori; }
    public void setKategori(KategoriBuku kategori) { this.kategori = kategori; }

    public StatusBuku getStatus() { return status; }
    public void setStatus(StatusBuku status) { this.status = status; }

    public int getStok() { return stok; }
    public void setStok(int stok) {
        this.stok = stok;
        this.status = stok > 0 ? StatusBuku.TERSEDIA : StatusBuku.DIPINJAM;
    }

    public int getTotalDipinjam() { return totalDipinjam; }

    @Override
    public String toString() {
        return judul + " - " + pengarang + " [" + kategori.getLabel() + "]";
    }
}
