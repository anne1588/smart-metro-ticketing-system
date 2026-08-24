package exception;

/**
 * Thrown when there is an error reading from or writing to a data file.
 * For example, when a file cannot be opened, is malformed, or cannot be saved.
 */
public class FileProcessingException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new FileProcessingException with a message.
     *
     * @param message the detail message
     */
    public FileProcessingException(String message) {
        super(message);
    }

    /**
     * Creates a new FileProcessingException with a message and a cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause (e.g. an IOException)
     */
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
