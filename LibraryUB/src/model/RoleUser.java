package model;

/**
 * Enum untuk role pengguna sistem
 * OOP Concept: Enum
 */
public enum RoleUser {
    ADMIN("Admin"),
    PETUGAS("Petugas"),
    MAHASISWA("Mahasiswa");

    private final String label;

    RoleUser(String label) {
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
