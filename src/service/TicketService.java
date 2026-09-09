package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import enums.TicketStatus;
import enums.TicketType;
import exception.TicketNotFoundException;
import fare.FareCalculator;
import model.Passenger;
import model.Route;
//import model.Station;
import model.Ticket;

/**
 * Provides ticket-related operations: buying tickets (with automatic fare
 * calculation), canceling tickets (with refund), and viewing/sorting tickets.
 */
public class TicketService {

    private final List<Ticket> tickets;
    private final FareCalculator fareCalculator;
    private int ticketCounter;
    
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   // System.out.println("Formatted: " + now.format(formatter));

    /**
     * Creates a ticket service.
     *
     * @param tickets        the shared ticket list
     * @param fareCalculator the fare calculation strategy (polymorphism:
     *                       {@code FareCalculator calc = new StandardFareCalculator();})
     */
    public TicketService(List<Ticket> tickets, FareCalculator fareCalculator) {
        this.tickets = tickets;
        this.fareCalculator = fareCalculator;
        // Seed the counter so new IDs do not collide with loaded tickets.
        this.ticketCounter = 0;
        for (Ticket ticket : tickets) {
            this.ticketCounter = Math.max(this.ticketCounter, extractNumber(ticket.getTicketId()));
        }
    }

    /**
     * Extracts the numeric part of a ticket ID like "TK001" -> 1.
     *
     * @param ticketId the ticket ID
     * @return the numeric part, or 0 if none is present
     */
    private int extractNumber(String ticketId) {
        if (ticketId == null) {
            return 0;
        }
        String digits = ticketId.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Buys a ticket for the given passenger on the given route.
     * <p>The fare is calculated automatically using the injected
     * {@link FareCalculator} based on the route distance and ticket type.</p>
     *
     * @param passenger   the passenger buying the ticket
     * @param route       the selected route
     * @param ticketType  the ticket type
     * @return the newly created ticket (status ACTIVE)
     */
    public Ticket buyTicket(Passenger passenger, Route route, TicketType ticketType) {
        ticketCounter++;
        String ticketId = String.format("TK%03d", ticketCounter);
        double fare = fareCalculator.calculateFare(route.getDistance(), ticketType);

        Ticket ticket = new Ticket(ticketId, passenger,
                route.getSource(), route.getDestination(), ticketType, fare);
        tickets.add(ticket);
        passenger.addTicket(ticket);
        return ticket;
    }

    /**
     * Cancels an ACTIVE ticket by ID for the given passenger. The fare is
     * refunded to the passenger's balance and the ticket status changes to
     * CANCELLED.
     *
     * @param passenger the logged-in passenger trying to cancel the ticket
     * @param ticketId  the ticket ID to cancel
     * @return the cancelled ticket
     * @throws TicketNotFoundException if the ticket ID does not exist or the
     *                                 ticket does not belong to this passenger
     */
    public Ticket cancelTicket(Passenger passenger, String ticketId) throws TicketNotFoundException {
        if (passenger == null) {
            throw new TicketNotFoundException("You must be logged in to cancel a ticket.");
        }
        Ticket ticket = findTicketById(ticketId);
        if (ticket == null) {
            throw new TicketNotFoundException("Ticket not found with ID: " + ticketId);
        }
        // A passenger may only cancel a ticket that belongs to him/her.
        if (!ticket.getPassenger().getEmail().equalsIgnoreCase(passenger.getEmail())) {
            throw new TicketNotFoundException("Ticket " + ticketId
                    + " does not belong to you. You can only cancel your own tickets.");
        }
        // A ticket whose validity period has passed can no longer be cancelled/refunded.
        if (ticket.getStatus() == TicketStatus.ACTIVE && ticket.isExpired()) {
            ticket.setStatus(TicketStatus.EXPIRED);
            throw new TicketNotFoundException("Ticket " + ticketId
                    + " has expired on " + ticket.getExpiryDate()
                    + " and can no longer be cancelled.");
        }
        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new TicketNotFoundException("Ticket " + ticketId
                    + " cannot be cancelled because its status is " + ticket.getStatus() + ".");
        }
        // Refund the fare to the passenger's balance.
        passenger.topUp(ticket.getFare());
        ticket.setStatus(TicketStatus.CANCELLED);
        return ticket;
    }
    
    /**
     * Marks every ACTIVE ticket whose expiry date has already passed as EXPIRED.
     * <p>Called on startup and whenever the ticket menus are shown, so that
     * tickets that are no longer valid are always displayed and handled as
     * EXPIRED instead of ACTIVE.</p>
     */
    public void markExpiredTickets() {
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.ACTIVE && ticket.isExpired()) {
                ticket.setStatus(TicketStatus.EXPIRED);
            }
        }
    }

    /**
     * Finds a ticket by its ID.
     *
     * @param ticketId the ticket ID
     * @return the ticket, or null if not found
     */
    public Ticket findTicketById(String ticketId) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketId().equalsIgnoreCase(ticketId.trim())) {
                return ticket;
            }
        }
        return null;
    }

    /**
     * Returns a copy of all tickets.
     *
     * @return a new list of tickets
     */
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets);
    }

    /**
     * Returns the tickets belonging to one passenger.
     *
     * @param passenger the passenger
     * @return a new list of the passenger's tickets
     */
    public List<Ticket> getTicketsForPassenger(Passenger passenger) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getPassenger().getEmail().equalsIgnoreCase(passenger.getEmail())) {
                result.add(ticket);
            }
        }
        return result;
    }

    /**
     * Returns all tickets sorted ascending by fare (bonus feature).
     *
     * @return a new sorted list of tickets
     */
    public List<Ticket> getTicketsSortedByFare() {
        List<Ticket> sorted = new ArrayList<>(tickets);
        Collections.sort(sorted); // uses Ticket.compareTo()
        return sorted;
    }

    /**
     * Displays a list of tickets.
     *
     * @param tickets the tickets to display
     * @param title   the heading shown above the list
     */
    public void displayTickets(List<Ticket> tickets, String title) {
        if (tickets.isEmpty()) {
            System.out.println("\n[Info] " + title + " is empty.");
            return;
        }
        System.out.println("\n============= " + title + " =============");
        int index = 1;
        for (Ticket ticket : tickets) {
            System.out.println(index++ + ". " + ticket);
        }
        System.out.println("========================================");
    }
}
