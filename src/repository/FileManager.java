package repository;

import java.util.HashMap;
import java.util.List;

import exception.FileProcessingException;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;

/**
 * Interface that defines the contract for reading and writing all
 * application data from/to data files.
 * <p>This interface is implemented by {@link TXTFileManager}, so the rest
 * of the application can work polymorphically:
 * {@code FileManager fm = new TXTFileManager();}</p>
 */
public interface FileManager {

    /**
     * Loads all users from the users file.
     *
     * @return a map of email -> User
     * @throws FileProcessingException if the file cannot be read
     */
    HashMap<String, User> loadUsers() throws FileProcessingException;

    /**
     * Saves all users to the users file.
     *
     * @param users the map of email -> User
     * @throws FileProcessingException if the file cannot be written
     */
    void saveUsers(HashMap<String, User> users) throws FileProcessingException;

    /**
     * Loads all stations from the stations file.
     *
     * @return a list of stations
     * @throws FileProcessingException if the file cannot be read
     */
    List<Station> loadStations() throws FileProcessingException;

    /**
     * Saves all stations to the stations file.
     *
     * @param stations the list of stations
     * @throws FileProcessingException if the file cannot be written
     */
    void saveStations(List<Station> stations) throws FileProcessingException;

    /**
     * Loads all trains from the trains file.
     *
     * @return a list of trains
     * @throws FileProcessingException if the file cannot be read
     */
    List<Train> loadTrains() throws FileProcessingException;

    /**
     * Saves all trains to the trains file.
     *
     * @param trains the list of trains
     * @throws FileProcessingException if the file cannot be written
     */
    void saveTrains(List<Train> trains) throws FileProcessingException;

    /**
     * Loads all routes from the routes file.
     * <p>The station list is needed so every route points to the same
     * station objects that are kept in the main station list.</p>
     *
     * @param stations the already-loaded stations
     * @return a list of routes
     * @throws FileProcessingException if the file cannot be read
     */
    List<Route> loadRoutes(List<Station> stations) throws FileProcessingException;

    /**
     * Saves all routes to the routes file.
     *
     * @param routes the list of routes
     * @throws FileProcessingException if the file cannot be written
     */
    void saveRoutes(List<Route> routes) throws FileProcessingException;

    /**
     * Loads all tickets from the tickets file.
     * <p>The users map and station list are needed to re-attach each ticket
     * to its owning passenger and to the source/destination stations.</p>
     *
     * @param users    the already-loaded users (email -> User)
     * @param stations the already-loaded stations
     * @return a list of tickets
     * @throws FileProcessingException if the file cannot be read
     */
    List<Ticket> loadTickets(HashMap<String, User> users, List<Station> stations)
            throws FileProcessingException;


    /**
     * Saves all tickets to the tickets file.
     *
     * @param tickets the list of tickets
     * @throws FileProcessingException if the file cannot be written
     */
    void saveTickets(List<Ticket> tickets) throws FileProcessingException;
}
