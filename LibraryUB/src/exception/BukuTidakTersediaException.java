package exception;

/**
 * Custom Exception: Buku tidak tersedia
 * OOP Concept: Exception Handling (custom exception extends Exception)
 */
public class BukuTidakTersediaException extends Exception {
    public BukuTidakTersediaException(String judul) {
        super("Buku \"" + judul + "\" tidak tersedia saat ini. Stok habis atau sedang dipinjam.");
    }
}
