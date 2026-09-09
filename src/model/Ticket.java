package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import enums.TicketStatus;
import enums.TicketType;

/**
 * Represents a metro ticket.
 * <p>Every ticket has a unique ticket ID, the passenger who bought it,
 * a source and destination station, a ticket type, a status
 * (default ACTIVE), the fare amount in RM, the dates of purchase and use,
 * and an expiry date.</p>
 * <p>The expiry date is derived from the date of purchase:
 * SINGLE and DAILY tickets expire 24 hours after purchase, while a
 * MONTHLY ticket expires 30 days after purchase.</p>
 * <p>Implements {@link Comparable} so tickets can be sorted by fare
 * (bonus feature).</p>
 */
public class Ticket implements Comparable<Ticket> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String ticketId;
    private Passenger passenger;
    private Station source;
    private Station destination;
    private TicketType ticketType;
    private TicketStatus status;
    private double fare;
    private String dateOfPurchase; // date the ticket was purchased
    private String dateOfUsed; // date the ticket was used (set by Use Ticket)
    private String expiryDate; // Date/time when the ticket stops being valid


    /**
     * Creates a new ticket.
     *
     * @param ticketId    unique ticket ID (e.g. TK001)
     * @param passenger   the passenger who bought the ticket
     * @param source      the origin station
     * @param destination the destination station
     * @param ticketType  the ticket type (SINGLE / DAILY / MONTHLY)
     * @param fare        the calculated fare in RM
     */
    public Ticket(String ticketId, Passenger passenger, Station source,
                  Station destination, TicketType ticketType, double fare) {
        this.ticketId = ticketId;
        this.passenger = passenger;
        this.source = source;
        this.destination = destination;
        this.ticketType = ticketType;
        this.status = TicketStatus.ACTIVE;   // default status
        this.fare = fare;
        this.dateOfPurchase = "-"; // set the date of purchase
        this.dateOfUsed = "-"; // set when the ticket is used (Use Ticket)
        this.expiryDate = "-"; // set once the date of purchase is known
    }

    /**
     * @return the ticket ID
     */
    public String getTicketId() {
        return ticketId;
    }

    /**
     * @param ticketId the ticket ID to set
     */
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    /**
     * @return the passenger who owns this ticket
     */
    public Passenger getPassenger() {
        return passenger;
    }

    /**
     * @param passenger the passenger to set
     */
    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    /**
     * @return the source station
     */
    public Station getSource() {
        return source;
    }

    /**
     * @param source the source station to set
     */
    public void setSource(Station source) {
        this.source = source;
    }

    /**
     * @return the destination station
     */
    public Station getDestination() {
        return destination;
    }

    /**
     * @param destination the destination station to set
     */
    public void setDestination(Station destination) {
        this.destination = destination;
    }

    /**
     * @return the ticket type
     */
    public TicketType getTicketType() {
        return ticketType;
    }

    /**
     * @param ticketType the ticket type to set
     */
    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    /**
     * @return the ticket status
     */
    public TicketStatus getStatus() {
        return status;
    }

    /**
     * @param status the ticket status to set
     */
    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    /**
     * @return the fare in RM
     */
    public double getFare() {
        return fare;
    }

    /**
     * @param fare the fare to set
     */
    public void setFare(double fare) {
        this.fare = fare;
    }
    
    public String getDateOfPurchase() {
		return dateOfPurchase;
	}
    
    public void setDateOfPurchase(String dateOfPurchase) {
    	this.dateOfPurchase = dateOfPurchase;
    }

    public String getDateOfUsed() {
        return dateOfUsed;
    }

    public void setDateOfUsed(String dateOfUsed) {
        this.dateOfUsed = dateOfUsed;
    }
    

    /**
     * @return the date/time when this ticket expires
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * @param expiryDate the date/time when this ticket expires
     */
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Recalculates and stores the expiry date of this ticket from its
     * date of purchase and ticket type:
     * <ul>
     *   <li>SINGLE - 24 hours after purchase</li>
     *   <li>DAILY  - 24 hours after purchase</li>
     *   <li>MONTHLY - 30 days after purchase</li>
     * </ul>
     * <p>Should be called whenever the date of purchase is assigned.</p>
     */
    public void updateExpiryDate() {
        if (dateOfPurchase == null || dateOfPurchase.trim().isEmpty()
                || "-".equals(dateOfPurchase.trim())) {
            expiryDate = "-";
            return;
        }
        try {
            LocalDateTime purchase = LocalDateTime.parse(dateOfPurchase.trim(), FORMATTER);
            LocalDateTime expiry;
            if (ticketType == TicketType.MONTHLY) {
                expiry = purchase.plusDays(30);
            } else {
                // SINGLE and DAILY both expire 24 hours after purchase.
                expiry = purchase.plusHours(24);
            }
            expiryDate = expiry.format(FORMATTER);
        } catch (Exception e) {
            expiryDate = "-";
        }
    }

    /**
     * Checks whether the validity period of this ticket has already passed.
     *
     * @return true if the current time is after the expiry date, false otherwise
     */
    public boolean isExpired() {
        if (expiryDate == null || expiryDate.trim().isEmpty()
                || "-".equals(expiryDate.trim())) {
            return false;
        }
        try {
            LocalDateTime expiry = LocalDateTime.parse(expiryDate.trim(), FORMATTER);
            return LocalDateTime.now().isAfter(expiry);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Compares this ticket with another ticket by fare.
     * Tickets with a higher fare sort later.
     *
     * @param other the ticket to compare against
     * @return negative / zero / positive as per fare comparison
     */
    @Override
    public int compareTo(Ticket other) {
        return Double.compare(this.fare, other.fare);
    }

    /**
     * Returns a human-readable summary of the ticket.
     *
     * @return formatted ticket string
     */
    @Override
    public String toString() {
        return "Ticket ID: " + ticketId
                + " | Passenger: " + passenger.getName()
                + " | From: " + source.getName()
                + " -> To: " + destination.getName()
                + " | Type: " + ticketType
                + " | Status: " + status
                + " | Fare: RM " + String.format("%.2f", fare)
                + " | Date of Purchase: " + dateOfPurchase
                + " | Date of Used: " + dateOfUsed
                + " | Expiry Date: " + expiryDate;
    }
}
