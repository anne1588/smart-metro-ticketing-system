package enums;

/**
 * Represents the current status of a ticket.
 * <ul>
 *   <li>ACTIVE    - valid and ready for travel</li>
 *   <li>USED      - no longer valid: travel completed / ticket consumed, or the
 *                   validity period has passed (SINGLE/DAILY = 24 hours,
 *                   MONTHLY = 30 days after purchase)</li>
 *   <li>CANCELLED - cancelled and refunded</li>
 * </ul>
 */
public enum TicketStatus {

    ACTIVE,
    USED,
    CANCELLED
}
