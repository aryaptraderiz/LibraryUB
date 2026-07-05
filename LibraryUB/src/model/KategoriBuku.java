package model;

/**
 * Enum untuk kategori buku
 * OOP Concept: Enum
 */
public enum KategoriBuku {
    TEKNOLOGI("Teknologi"),
    BISNIS("Bisnis"),
    EKONOMI("Ekonomi"),
    HUKUM("Hukum");

    private final String label;

    KategoriBuku(String label) {
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
