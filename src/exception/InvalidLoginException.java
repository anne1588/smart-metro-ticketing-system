package exception;

/**
 * Thrown when a user attempts to log in with incorrect credentials
 * (wrong email or password).
 */
public class InvalidLoginException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new InvalidLoginException with a message.
     *
     * @param message the detail message
     */
    public InvalidLoginException(String message) {
        super(message);
    }
}
