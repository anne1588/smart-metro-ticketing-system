package service;

import java.util.List;

import enums.TicketStatus;
import enums.TicketType;
import model.Ticket;

/**
 * Generates summary reports from the ticket collection.
 * <p>The report shows total tickets sold (overall and broken down by
 * ticket type), total revenue, and the number of cancelled tickets.</p>
 */
public class ReportService {

    private final List<Ticket> tickets;

    /**
     * Creates a report service backed by the ticket list.
     *
     * @param tickets the shared ticket list
     */
    public ReportService(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    /**
     * Generates and prints the full sales report.
     */
    public void generateReport() {
        System.out.println("\n=========== SYSTEM REPORT ===========");

        int totalTickets = tickets.size();
        int singleCount = 0;
        int dailyCount = 0;
        int monthlyCount = 0;
        int cancelledCount = 0;
        double totalRevenue = 0.0;

        for (Ticket ticket : tickets) {
            switch (ticket.getTicketType()) {
                case SINGLE:
                    singleCount++;
                    break;
                case DAILY:
                    dailyCount++;
                    break;
                case MONTHLY:
                    monthlyCount++;
                    break;
            }
            if (ticket.getStatus() == TicketStatus.CANCELLED) {
                cancelledCount++;
            } else if (ticket.getStatus() == TicketStatus.USED) {
                // Only paid (USED) tickets contribute to revenue.
                totalRevenue += ticket.getFare();
            }
        }

        // ---- Overall tickets sold ----
        System.out.println("Total tickets sold      : " + totalTickets);

        // ---- Tickets sold by type ----
        System.out.println("\nTickets sold by type:");
        System.out.printf("  %-10s : %d%n", TicketType.SINGLE, singleCount);
        System.out.printf("  %-10s : %d%n", TicketType.DAILY, dailyCount);
        System.out.printf("  %-10s : %d%n", TicketType.MONTHLY, monthlyCount);

        // ---- Revenue ----
        System.out.println("\nTotal revenue (USED)    : RM "
                + String.format("%.2f", totalRevenue));

        // ---- Cancelled tickets ----
        System.out.println("Cancelled tickets       : " + cancelledCount);

        System.out.println("================================");
    }
}
