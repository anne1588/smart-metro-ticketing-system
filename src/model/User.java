package model;

import enums.UserRole;

/**
 * Abstract base class for every user of the system.
 * <p>
 * Both {@link Passenger} and {@link Admin} inherit from this class.
 * The class is abstract, so it cannot be instantiated directly —
 * it only defines common behaviour shared by all users.
 * </p>
 * <p>Encapsulation: all fields are private and only reachable
 * through public getters/setters.</p>
 */
public abstract class User {

    private String email;
    private String name;
    private String password;
    private UserRole role;

    /**
     * Default constructor (used by subclasses / file loader).
     */
    protected User() {
    }

    /**
     * Creates a user with the given details.
     *
     * @param email    the unique email address (also the HashMap key)
     * @param name     the display name
     * @param password the login password
     * @param role     the user role (PASSENGER or ADMIN)
     */
    protected User(String email, String name, String password, UserRole role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the display name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the display name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the login password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the login password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the user role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * @param role the user role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Returns a human-readable summary of the user.
     *
     * @return formatted string of common user fields
     */
    @Override
    public String toString() {
        return "Name: " + name
                + " | Email: " + email
                + " | Role: " + role;
    }
}
