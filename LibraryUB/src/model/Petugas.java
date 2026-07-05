package model;

/**
 * Class Petugas
 * OOP Concept: Inheritance, Polymorphism
 */
public class Petugas extends Pengguna {

    private String shift;

    public Petugas(String id, String nama, String username, String password, String shift) {
        super(id, nama, username, password, RoleUser.PETUGAS);
        this.shift = shift;
    }

    @Override
    public String getInfoLengkap() {
        return "Petugas | ID: " + getId() + " | Nama: " + getNama() + " | Shift: " + shift;
    }

    @Override
    public String getDashboardTitle() {
        return "Dashboard Petugas - " + getNama();
    }

    @Override
    public void kirimPesan(String pesan) {
        System.out.println("[PETUGAS] " + getNama() + " (Shift " + shift + "): " + pesan);
        super.kirimPesan(pesan);
    }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }
}
