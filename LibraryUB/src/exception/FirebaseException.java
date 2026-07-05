package exception;

/**
 * Custom Exception: error saat komunikasi dengan Firebase Firestore
 * OOP Concept: Exception Handling
 */
public class FirebaseException extends Exception {
    public FirebaseException(String message) {
        super(message);
    }

    public FirebaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
