package model;

/**
 * Class Mahasiswa
 * OOP Concept: Inheritance, Polymorphism, Encapsulation
 */
public class Mahasiswa extends Pengguna {

    private String nim;
    private String prodi;
    private int maxPinjam;

    public Mahasiswa(String id, String nama, String username, String password, String nim, String prodi) {
        super(id, nama, username, password, RoleUser.MAHASISWA);
        this.nim = nim;
        this.prodi = prodi;
        this.maxPinjam = 3; // Default max pinjam 3 buku
    }

    @Override
    public String getInfoLengkap() {
        return "Mahasiswa | NIM: " + nim + " | Nama: " + getNama() + " | Prodi: " + prodi;
    }

    @Override
    public String getDashboardTitle() {
        return "Dashboard Mahasiswa - " + getNama() + " (" + nim + ")";
    }

    @Override
    public void kirimPesan(String pesan) {
        System.out.println("[NOTIF MAHASISWA] " + getNama() + " (" + nim + "): " + pesan);
        super.kirimPesan(pesan);
    }

    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getProdi() { return prodi; }
    public void setProdi(String prodi) { this.prodi = prodi; }

    public int getMaxPinjam() { return maxPinjam; }
    public void setMaxPinjam(int maxPinjam) { this.maxPinjam = maxPinjam; }
}
