package model;

import enums.UserRole;

/**
 * Represents an administrator of the metro system.
 * <p>An admin manages stations, trains, routes, users and reports.
 * An admin has no e-wallet balance and cannot buy tickets.</p>
 */
public class Admin extends User {

    /**
     * Default constructor required by the file loader.
     */
    public Admin() {
    }

    /**
     * Creates a new admin.
     *
     * @param email    unique email address
     * @param name     admin name
     * @param password login password
     */
    public Admin(String email, String name, String password) {
        super(email, name, password, UserRole.ADMIN);
    }

    /**
     * Returns a summary of the admin.
     *
     * @return formatted admin string
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
