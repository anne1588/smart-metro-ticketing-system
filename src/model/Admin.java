package model;

import enums.UserRole;

/**
 * Represents an administrator of the metro system.
 * An admin manages stations, trains, routes, users and reports.
 * An admin has no e-wallet balance and cannot buy tickets.
 */
public class Admin extends User {

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
