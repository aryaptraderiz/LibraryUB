package exception;

/**
 * Custom Exception: User tidak ditemukan
 * OOP Concept: Exception Handling
 */
public class PenggunaTidakDitemukanException extends Exception {
    public PenggunaTidakDitemukanException(String username) {
        super("Pengguna dengan username \"" + username + "\" tidak ditemukan.");
    }
}
