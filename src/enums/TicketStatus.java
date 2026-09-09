package enums;

/**
 * Represents the current status of a ticket.
 * <ul>
 *   <li>ACTIVE    - valid and ready for travel</li>
 *   <li>USED      - travel completed / ticket consumed via Use Ticket
 *                   (date of used is recorded)</li>
 *   <li>CANCELLED - cancelled and refunded</li>
 *   <li>EXPIRED   - validity period has passed without being used
 *                   (SINGLE/DAILY = 24 hours, MONTHLY = 30 days after purchase)</li>
 * </ul>
 */
public enum TicketStatus {

    ACTIVE,
    USED,
    CANCELLED,
    EXPIRED
}
