package model;

/**
 * Class Admin
 * OOP Concept: Inheritance (extends Pengguna), Polymorphism (override method)
 */
public class Admin extends Pengguna {

    private String jabatan;

    public Admin(String id, String nama, String username, String password, String jabatan) {
        // OOP: super() untuk memanggil constructor parent
        super(id, nama, username, password, RoleUser.ADMIN);
        this.jabatan = jabatan;
    }

    // OOP: Polymorphism - override method abstrak dari Pengguna
    @Override
    public String getInfoLengkap() {
        return "Admin | ID: " + getId() + " | Nama: " + getNama() + " | Jabatan: " + jabatan;
    }

    @Override
    public String getDashboardTitle() {
        return "Dashboard Admin - " + getNama();
    }

    // OOP: Override kirimPesan dengan perilaku khusus Admin
    @Override
    public void kirimPesan(String pesan) {
        System.out.println("[ADMIN BROADCAST] " + getNama() + ": " + pesan);
        super.kirimPesan(pesan);
    }

    public String getJabatan() { return jabatan; }
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }
}
