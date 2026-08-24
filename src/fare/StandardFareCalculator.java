package fare;

import enums.TicketType;

/**
 * Standard implementation of {@link FareCalculator}.
 * <p>The fare is based on the route distance and the ticket type:</p>
 * <ul>
 *   <li>SINGLE  : base fare = distance * RM 0.50</li>
 *   <li>DAILY   : base fare * 2</li>
 *   <li>MONTHLY : base fare * 20</li>
 * </ul>
 */
public class StandardFareCalculator implements FareCalculator {

    private static final double BASE_RATE = 0.50;  // RM per km
    private static final int DAILY_MULTIPLIER = 2;
    private static final int MONTHLY_MULTIPLIER = 20;

    /**
     * Calculates the fare using the standard pricing rules.
     *
     * @param distance   the distance of the trip in kilometres
     * @param ticketType the ticket type
     * @return the fare amount in RM
     */
    @Override
    public double calculateFare(double distance, TicketType ticketType) {
        if (distance < 0) {
            distance = 0;
        }
        double baseFare = distance * BASE_RATE;

        switch (ticketType) {
            case DAILY:
                return baseFare * DAILY_MULTIPLIER;
            case MONTHLY:
                return baseFare * MONTHLY_MULTIPLIER;
            case SINGLE:
            default:
                return baseFare;
        }
    }
}
