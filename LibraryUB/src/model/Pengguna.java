package model;

/**
 * Abstract class Pengguna
 * OOP Concept: Abstraction, Encapsulation, Inheritance (base class)
 * 
 * Kelas ini tidak bisa diinstansiasi langsung.
 * Subclass wajib mengimplementasi method abstrak.
 */
public abstract class Pengguna implements Notifikasi {

    // OOP: Encapsulation - semua field private
    private String id;
    private String nama;
    private String username;
    private String password;
    private RoleUser role;
    private String pesanNotifikasi;

    // Constructor
    public Pengguna(String id, String nama, String username, String password, RoleUser role) {
        this.id = id;
        this.nama = nama;
        this.username = username;
        this.password = password;
        this.role = role;
        this.pesanNotifikasi = "";
    }

    // OOP: Abstraction - method abstrak wajib di-override subclass
    public abstract String getInfoLengkap();
    public abstract String getDashboardTitle();

    // OOP: Polymorphism - implementasi Notifikasi interface
    @Override
    public void kirimPesan(String pesan) {
        this.pesanNotifikasi = "[" + role.getLabel() + " - " + nama + "] " + pesan;
        System.out.println("Notifikasi dikirim: " + this.pesanNotifikasi);
    }

    @Override
    public String getPesan() {
        return pesanNotifikasi;
    }

    // Getter & Setter (Encapsulation)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RoleUser getRole() { return role; }
    public void setRole(RoleUser role) { this.role = role; }

    public boolean verifikasiPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return nama + " (" + role.getLabel() + ")";
    }
}
