package service;

import java.math.BigDecimal;
import java.util.HashMap;

import enums.UserRole;
import exception.InvalidLoginException;
import model.Passenger;
import model.User;

/**
 * Provides all user-related operations: registration, login,
 * profile viewing, top-up and admin listing.
 */
public class UserService {

    private final HashMap<String, User> users;

    /**
     * Creates a user service backed by the given user map.
     *
     * @param users the shared user map (email -> User)
     */
    public UserService(HashMap<String, User> users) {
        this.users = users;
    }

    /**
     * Registers a new passenger with an initial e-wallet balance.
     * The email address is used as the unique key, so a duplicate
     * email is rejected.
     *
     * @param email    the email address
     * @param name     the passenger name
     * @param password the login password
     * @param balance  the initial balance (RM)
     * @return true if the registration succeeded, false if the email is taken
     */
    public boolean registerPassenger(String email, String name, String password, double balance) {
        if (email == null || name == null || password == null) {
            return false;
        }
        String key = email.trim().toLowerCase();
        if (key.isEmpty() || users.containsKey(key)) {
            return false;
        }
        Passenger passenger = new Passenger(key, name.trim(), password, BigDecimal.valueOf(balance));
        users.put(key, passenger);
        return true;
    }

    /**
     * Validates a user's credentials and returns the matching user.
     *
     * @param email    the email address
     * @param password the password
     * @return the authenticated User object
     * @throws InvalidLoginException if the credentials do not match any user
     */
    public User login(String email, String password) throws InvalidLoginException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidLoginException("Email must be provided.");
        }
        if (password == null) {
            throw new InvalidLoginException("Password must be provided.");
        }
        String key = email.trim().toLowerCase();
        User user = users.get(key);
        if (user == null) {
            throw new InvalidLoginException("No account found with email: " + email.trim());
        }
        if (!user.getPassword().equals(password)) {
            throw new InvalidLoginException("Incorrect password. Please try again.");
        }
        return user;
    }

    /**
     * Checks whether the given email already belongs to a user.
     *
     * @param email the email to check
     * @return true if the email is already registered
     */
    public boolean isEmailTaken(String email) {
        if (email == null) {
            return false;
        }
        return users.containsKey(email.trim().toLowerCase());
    }

    /**
     * Adds money to a passenger's e-wallet.
     *
     * @param passenger the passenger
     * @param amount    the amount to add
     * @return true if the top-up succeeded
     */
    public boolean topUpBalance(Passenger passenger, double amount) {
        if (passenger == null || Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            return false;
        }
        passenger.topUp(BigDecimal.valueOf(amount));
        return true;
    }

    /**
     * Returns the map of all registered users.
     *
     * @return the user map
     */
    public HashMap<String, User> getAllUsers() {
        return users;
    }

    /**
     * Displays the profile of the given passenger including ticket history.
     *
     * @param passenger the passenger to display
     */
    public void viewProfile(Passenger passenger) {
        System.out.println("\n========== PASSENGER PROFILE ==========");
        System.out.println("Name      : " + passenger.getName());
        System.out.println("Email     : " + passenger.getEmail());
        System.out.println("Balance   : RM " + formatMoney(passenger.getBalance()));
        System.out.println("Role      : " + passenger.getRole());
        System.out.println("---------------------------------------");
        System.out.println("Ticket History:");
        if (passenger.getTickets().isEmpty()) {
            System.out.println("  (No tickets purchased yet)");
        } else {
            int index = 1;
            for (model.Ticket ticket : passenger.getTickets()) {
                System.out.println("  " + index++ + ". " + ticket);
            }
        }
        System.out.println("========================================");
    }

    /**
     * Displays every registered user (admin function).
     * Uses {@code UserRole} to print each user's role.
     */
    public void viewAllUsers() {
        if (users.isEmpty()) {
            System.out.println("\n[Info] No users registered yet.");
            return;
        }
        System.out.println("\n============= ALL USERS =============");
        int index = 1;
        for (User user : users.values()) {
            System.out.printf("%-3d %-30s %-25s %-10s", index++, user.getName(),
                    user.getEmail(), user.getRole());
            if (user.getRole() == UserRole.PASSENGER) {
                System.out.println(" RM " + formatMoney(((Passenger) user).getBalance()));
            } else {
                System.out.println();
            }
        }
        System.out.println("======================================");
    }

    /**
     * Formats a money value as a plain string with 2 decimal places.
     *
     * @param value the money value
     * @return e.g. "12.50"
     */
    private static String formatMoney(BigDecimal value) {
        return value == null ? "0.00"
                : value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}
