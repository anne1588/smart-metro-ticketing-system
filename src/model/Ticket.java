package model;

import enums.TicketStatus;
import enums.TicketType;

/**
 * Represents a metro ticket.
 * <p>Every ticket has a unique ticket ID, the passenger who bought it,
 * a source and destination station, a ticket type, a status
 * (default ACTIVE) and the fare amount in RM.</p>
 * <p>Implements {@link Comparable} so tickets can be sorted by fare
 * (bonus feature).</p>
 */
public class Ticket implements Comparable<Ticket> {

    private String ticketId;
    private Passenger passenger;
    private Station source;
    private Station destination;
    private TicketType ticketType;
    private TicketStatus status;
    private double fare;
    private String dateOfPurchase; // New field to store the date of purchase
    private String dateOfUsed; // New field to store the date of used


    /**
     * Creates a new ticket.
     *
     * @param ticketId    unique ticket ID (e.g. TK001)
     * @param passenger   the passenger who bought the ticket
     * @param source      the origin station
     * @param destination the destination station
     * @param ticketType  the ticket type (SINGLE / DAILY / MONTHLY)
     * @param fare        the calculated fare in RM
     * @param dateOfPurchase the date when the ticket was purchased
     * @param dateOfUsed the date when the ticket was used (if applicable)
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
        this.dateOfUsed = "-"; // initialize date of used as null
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
        		+ " | Date of Used: " + dateOfUsed;
    }
}
