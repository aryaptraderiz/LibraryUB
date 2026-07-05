package exception;

/**
 * Custom Exception: Melebihi batas pinjam
 * OOP Concept: Exception Handling
 */
public class MelebihiBatasPinjamException extends Exception {
    public MelebihiBatasPinjamException(String nama, int max) {
        super("Mahasiswa " + nama + " telah mencapai batas maksimal peminjaman (" + max + " buku).");
    }
}
