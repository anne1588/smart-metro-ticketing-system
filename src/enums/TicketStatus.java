package enums;

/**
 * Represents the current status of a ticket.
 * <ul>
 *   <li>ACTIVE    - valid and ready for travel</li>
 *   <li>USED      - travel completed / ticket consumed</li>
 *   <li>CANCELLED - cancelled and refunded</li>
 * </ul>
 */
public enum TicketStatus {

    ACTIVE,
    USED,
    CANCELLED
}
