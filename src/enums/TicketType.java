package enums;

/**
 * Represents the type of a metro ticket.
 * <ul>
 *   <li>SINGLE  - one single trip between two stations</li>
 *   <li>DAILY   - unlimited travel for one day</li>
 *   <li>MONTHLY - unlimited travel for one month</li>
 * </ul>
 */
public enum TicketType {

    SINGLE("Single Trip"),
    DAILY("Daily Pass"),
    MONTHLY("Monthly Pass");

    private final String description;

    /**
     * Enum constructor that stores a human-friendly description.
     *
     * @param description the description of the ticket type
     */
    TicketType(String description) {
        this.description = description;
    }

    /**
     * Returns the description of this ticket type.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
