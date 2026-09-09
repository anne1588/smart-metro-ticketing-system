package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import enums.UserRole;

/**
 * Represents a passenger of the metro system.
 * <p>A passenger can top up his/her e-wallet balance, buy tickets,
 * cancel tickets and view a history of all owned tickets.</p>
 */
public class Passenger extends User {

    private static final int MONEY_SCALE = 2;

    private BigDecimal balance;
    private final List<Ticket> tickets;


    /**
     * Creates a new passenger.
     *
     * @param email        unique email address
     * @param name         passenger name
     * @param password     login password
     * @param initialBalance starting e-wallet balance (RM)
     */
    public Passenger(String email, String name, String password, double initialBalance) {
        this(email, name, password, BigDecimal.valueOf(initialBalance));
    }

    /**
     * Creates a new passenger with an exact decimal starting balance.
     *
     * @param email        unique email address
     * @param name         passenger name
     * @param password     login password
     * @param initialBalance starting e-wallet balance (RM)
     */
    public Passenger(String email, String name, String password, BigDecimal initialBalance) {
        super(email, name, password, UserRole.PASSENGER);
        this.balance = scaleMoney(initialBalance == null ? BigDecimal.ZERO : initialBalance);
        this.tickets = new ArrayList<>();
    }

    /**
     * @return the current e-wallet balance in RM
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set (used by the file loader)
     */
    public void setBalance(BigDecimal balance) {
        this.balance = scaleMoney(balance);
    }

    /**
     * Adds money to the passenger's e-wallet.
     *
     * @param amount the amount to add (must be > 0)
     * @throws IllegalArgumentException if the amount is not positive
     */
    public void topUp(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Top-up amount must be greater than 0.");
        }
        this.balance = this.balance.add(scaleMoney(amount));
    }

    /**
     * Deducts money from the passenger's e-wallet.
     *
     * @param amount the amount to deduct (must be > 0)
     * @return true if the deduction succeeded, false if invalid or insufficient
     */
    public boolean deduct(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            return false;
        }
        BigDecimal scaled = scaleMoney(amount);
        if (this.balance.compareTo(scaled) < 0) {
            return false;
        }
        this.balance = this.balance.subtract(scaled);
        return true;
    }

    /**
     * Adds a ticket to the passenger's ticket history.
     *
     * @param ticket the ticket to add
     */
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

    /**
     * Removes a ticket from the passenger's ticket history.
     *
     * @param ticket the ticket to remove
     */
    public void removeTicket(Ticket ticket) {
        this.tickets.remove(ticket);
    }

    /**
     * @return an unmodifiable list of tickets owned by this passenger
     */
    public List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    /**
     * Rounds a money value to 2 decimal places.
     *
     * @param value the raw money value
     * @return the value scaled to 2 decimal places (half-up)
     */
    private static BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(MONEY_SCALE, java.math.RoundingMode.HALF_UP);
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

    /**
     * Returns a summary of the passenger's profile.
     *
     * @return formatted profile string
     */
    @Override
    public String toString() {
        return super.toString()
                + " | Balance: RM " + formatMoney(balance);
    }
}
