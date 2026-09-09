package fare;

import java.math.BigDecimal;

import enums.TicketType;
import util.Money;

/**
 * Standard implementation of {@link FareCalculator}.
 * <p>The fare is based on the route distance and the ticket type:</p>
 * <ul>
 *   <li>SINGLE  : base fare = distance * RM 0.50</li>
 *   <li>DAILY   : base fare * 2</li>
 *   <li>MONTHLY : base fare * 20</li>
 * </ul>
 * <p>All arithmetic uses {@link BigDecimal} so no rounding errors occur.</p>
 */
public class StandardFareCalculator implements FareCalculator {

    private static final BigDecimal BASE_RATE = new BigDecimal("0.50"); // RM per km
    private static final BigDecimal DAILY_MULTIPLIER = new BigDecimal("2");
    private static final BigDecimal MONTHLY_MULTIPLIER = new BigDecimal("20");

    /**
     * Calculates the fare using the standard pricing rules.
     *
     * @param distance   the distance of the trip in kilometres
     * @param ticketType the ticket type
     * @return the fare amount in RM rounded to 2 decimal places
     */
    @Override
    public BigDecimal calculateFare(double distance, TicketType ticketType) {
        if (Double.isNaN(distance) || Double.isInfinite(distance)) {
            distance = 0;
        }
        if (distance < 0) {
            distance = 0;
        }
        BigDecimal baseFare = BigDecimal.valueOf(distance).multiply(BASE_RATE);

        switch (ticketType) {
            case DAILY:
                return Money.scale(baseFare.multiply(DAILY_MULTIPLIER));
            case MONTHLY:
                return Money.scale(baseFare.multiply(MONTHLY_MULTIPLIER));
            case SINGLE:
            default:
                return Money.scale(baseFare);
        }
    }
}
