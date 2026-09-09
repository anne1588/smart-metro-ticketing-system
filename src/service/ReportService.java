package service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import enums.TicketStatus;
import enums.TicketType;
import exception.NoDataFoundException;
import model.Ticket;

public class ReportService {

    private final List<Ticket> tickets;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReportService(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    /**
     * Generates a report for a specific month and year (e.g., Sep 2024).
     *
     * @param year  the target year (e.g., 2024)
     * @param month the target month value (1 for Jan, 12 for Dec)
     * @throws NoDataFoundException if no tickets exist for that month and year
     */
    public void generateMonthlyReport(int year, int month) throws NoDataFoundException {
        if (month < 1 || month > 12) {
            throw new NoDataFoundException(
                    "Invalid month: " + month + ". Month must be between 1 and 12.");
        }
        List<Ticket> filtered = new ArrayList<>();
        for (Ticket ticket : tickets) {
            LocalDate date = parseDate(ticket.getDateOfPurchase());
            if (date != null && date.getYear() == year && date.getMonthValue() == month) {
                filtered.add(ticket);
            }
        }

        if (filtered.isEmpty()) {
            String monthName = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            throw new NoDataFoundException("No ticket records found for " + monthName + " " + year + ".");
        }

        String monthName = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        printReportDetails(filtered, monthName.toUpperCase() + " " + year + " REPORT");
    }

    /**
     * Generates a report for an entire year (e.g., 2026).
     *
     * @param year the target year (e.g., 2026)
     * @throws NoDataFoundException if no tickets exist for that year
     */
    public void generateYearlyReport(int year) throws NoDataFoundException {
        List<Ticket> filtered = new ArrayList<>();
        for (Ticket ticket : tickets) {
            LocalDate date = parseDate(ticket.getDateOfPurchase());
            if (date != null && date.getYear() == year) {
                filtered.add(ticket);
            }
        }

        if (filtered.isEmpty()) {
            throw new NoDataFoundException("No ticket records found for the year " + year + ".");
        }

        printReportDetails(filtered, "YEAR " + year + " REPORT");
    }

    /**
     * Internal printer method shared by monthly and yearly calculations.
     */
    private void printReportDetails(List<Ticket> ticketList, String title) {
        System.out.println("\n=========== " + title + " ===========");

        int totalTickets = ticketList.size();
        int singleCount = 0;
        int dailyCount = 0;
        int monthlyCount = 0;
        int activeCount = 0;
        int usedCount = 0;
        int expiredCount = 0;
        int cancelledCount = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Ticket ticket : ticketList) {
            switch (ticket.getTicketType()) {
                case SINGLE -> singleCount++;
                case DAILY -> dailyCount++;
                case MONTHLY -> monthlyCount++;
            }
            switch (ticket.getStatus()) {
                case ACTIVE -> activeCount++;
                case USED -> usedCount++;
                case EXPIRED -> expiredCount++;
                case CANCELLED -> cancelledCount++;
            }
            // Revenue is earned for every ticket that was paid for and not
            // refunded: consumed singles (USED), valid/used passes (ACTIVE),
            // and passes that expired unused (EXPIRED). Only CANCELLED
            // tickets are refunded and therefore excluded.
            if (ticket.getStatus() != TicketStatus.CANCELLED) {
                totalRevenue = totalRevenue.add(scaleMoney(ticket.getFare()));
            }
        }

        System.out.println("Total tickets sold      : " + totalTickets);

        System.out.println("\nTickets sold by type:");
        System.out.printf("  %-10s : %d%n", TicketType.SINGLE, singleCount);
        System.out.printf("  %-10s : %d%n", TicketType.DAILY, dailyCount);
        System.out.printf("  %-10s : %d%n", TicketType.MONTHLY, monthlyCount);

        System.out.println("\nTicket status breakdown:");
        System.out.printf("  %-10s : %d%n", "ACTIVE", activeCount);
        System.out.printf("  %-10s : %d%n", "USED (consumed single trips)", usedCount);
        System.out.printf("  %-10s : %d%n", "EXPIRED", expiredCount);
        System.out.printf("  %-10s : %d%n", "CANCELLED", cancelledCount);

        System.out.println("\nTotal revenue (non-cancelled tickets) : RM " + formatMoney(totalRevenue));
        System.out.println("Cancelled tickets (refunded)          : " + cancelledCount);
        System.out.println("==========================================");
    }

    /**
     * Safely parses the purchase date string into a LocalDate.
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateStr.trim(), DATE_FORMATTER).toLocalDate();
        } catch (Exception e) {
            try {
                return LocalDate.parse(dateStr.trim());
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * Rounds a money value to 2 decimal places (half-up).
     *
     * @param value the raw money value
     * @return the scaled value
     */
    private static BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Formats a money value as a plain string with 2 decimal places.
     *
     * @param value the money value
     * @return e.g. "12.50"
     */
    private static String formatMoney(BigDecimal value) {
        return value == null ? "0.00" : scaleMoney(value).toPlainString();
    }
}