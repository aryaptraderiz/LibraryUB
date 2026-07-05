package model;

/**
 * Enum untuk status buku
 * OOP Concept: Enum (tipe data khusus)
 */
public enum StatusBuku {
    TERSEDIA("Tersedia"),
    DIPINJAM("Dipinjam");

    private final String label;

    StatusBuku(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
