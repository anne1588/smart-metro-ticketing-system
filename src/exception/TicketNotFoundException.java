package exception;

/**
 * Thrown when a ticket with a given ticket ID cannot be found in the system.
 */
public class TicketNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new TicketNotFoundException with a message.
     *
     * @param message the detail message
     */
    public TicketNotFoundException(String message) {
        super(message);
    }
}
