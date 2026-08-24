package fare;

import enums.TicketType;

/**
 * Interface that defines the contract for any fare calculation strategy.
 * <p>This interface-based design allows the system to calculate fares
 * polymorphically, e.g.
 * {@code FareCalculator calc = new StandardFareCalculator();}</p>
 */
public interface FareCalculator {

    /**
     * Calculates the fare for a trip of the given distance and ticket type.
     *
     * @param distance   the distance of the trip in kilometres
     * @param ticketType the type of ticket (SINGLE / DAILY / MONTHLY)
     * @return the fare amount in RM
     */
    double calculateFare(double distance, TicketType ticketType);
}
