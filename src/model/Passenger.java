package model;

import java.util.ArrayList;
import java.util.List;

import enums.UserRole;

/**
 * Represents a passenger of the metro system.
 * <p>A passenger can top up his/her e-wallet balance, buy tickets,
 * cancel tickets and view a history of all owned tickets.</p>
 */
public class Passenger extends User {

    private double balance;
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
        super(email, name, password, UserRole.PASSENGER);
        this.balance = initialBalance;
        this.tickets = new ArrayList<>();
    }

    /**
     * @return the current e-wallet balance in RM
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set (used by the file loader)
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Adds money to the passenger's e-wallet.
     *
     * @param amount the amount to add (must be > 0)
     */
    public void topUp(double amount) {
        this.balance += amount;
    }

    /**
     * Deducts money from the passenger's e-wallet.
     *
     * @param amount the amount to deduct (must be > 0)
     * @return true if the deduction succeeded, false if insufficient balance
     */
    public boolean deduct(double amount) {
        if (amount <= 0) {
            return false;
        }
        if (this.balance < amount) {
            return false;
        }
        this.balance -= amount;
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
        return tickets;
    }

    /**
     * Returns a summary of the passenger's profile.
     *
     * @return formatted profile string
     */
    @Override
    public String toString() {
        return super.toString()
                + " | Balance: RM " + String.format("%.2f", balance);
    }
}
