package repository;

import enums.TicketStatus;
import enums.TicketType;
import enums.UserRole;
import exception.FileProcessingException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.Admin;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;

/**
 * TXT implementation of the {@link FileManager} interface.
 * <p>
 * All data files use a simple pipe-separated format ({@code |}) so that
 * names and locations may contain spaces. Every method wraps any
 * {@link IOException} into a {@link FileProcessingException}.
 * </p>
 * <p>Files are stored inside the {@code data} folder of the project. The
 * folder is located automatically so that the application works regardless
 * of the working directory.</p>
 */
public class TXTFileManager implements FileManager {

    private static final String DATA_DIR = resolveDataDir();

    private static final String USERS_FILE    = DATA_DIR + File.separator + "users.txt";
    private static final String STATIONS_FILE = DATA_DIR + File.separator + "stations.txt";
    private static final String TRAINS_FILE   = DATA_DIR + File.separator + "trains.txt";
    private static final String ROUTES_FILE   = DATA_DIR + File.separator + "routes.txt";
    private static final String TICKETS_FILE  = DATA_DIR + File.separator + "tickets.txt";

    /**
     * Locates the {@code data} folder of the project by walking up from the
     * current working directory until a folder that contains the data files
     * is found. This prevents running the app from another directory from
     * loading an empty dataset or overwriting the wrong files.
     *
     * @return an absolute path to the data folder, or the relative
     *         {@code data} folder if none can be located yet
     */
    private static String resolveDataDir() {
        String fallback = "data";
        File current = new File(System.getProperty("user.dir", "."));
        for (File dir = current; dir != null; dir = dir.getParentFile()) {
            File candidate = new File(dir, "data");
            if (new File(candidate, "users.txt").exists()
                    && new File(candidate, "stations.txt").exists()) {
                return candidate.getAbsolutePath();
            }
        }
        return fallback;
    }

    // ------------------------------------------------------------------
    // 1. USERS
    // ------------------------------------------------------------------

    @Override
    public HashMap<String, User> loadUsers() throws FileProcessingException {
        HashMap<String, User> users = new HashMap<>();
        for (String line : readAllLines(USERS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length != 5) {
                throw new FileProcessingException("Malformed line in " + USERS_FILE + ": " + line);
            }
            UserRole role = parseUserRole(p[4], USERS_FILE);
            if (role == UserRole.ADMIN) {
                Admin admin = new Admin(p[0].trim(), p[1].trim(), p[2].trim());
                users.put(admin.getEmail(), admin);
            } else {
                BigDecimal balance = parseMoney(p[3], USERS_FILE);
                Passenger passenger = new Passenger(
                        p[0].trim(),            // email
                        p[1].trim(),            // name
                        p[2].trim(),            // password
                        balance);
                users.put(passenger.getEmail(), passenger);
            }
        }
        return users;
    }

    @Override
    public void saveUsers(HashMap<String, User> users) throws FileProcessingException {
        List<String> lines = new ArrayList<>();
        lines.add("# Format: email|name|password|balance|role");
        for (User user : users.values()) {
            String line = user.getEmail() + "|" + user.getName() + "|" + user.getPassword();
            if (user.getRole() == UserRole.ADMIN) {
                line += "|0.00|ADMIN";
            } else {
                Passenger passenger = (Passenger) user;
                line += "|" + formatMoney(passenger.getBalance()) + "|PASSENGER";
            }
            lines.add(line);
        }
        writeAllLines(USERS_FILE, lines);
    }

    // ------------------------------------------------------------------
    // 2. STATIONS
    // ------------------------------------------------------------------

    @Override
    public List<Station> loadStations() throws FileProcessingException {
        List<Station> stations = new ArrayList<>();
        for (String line : readAllLines(STATIONS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length != 3) {
                throw new FileProcessingException("Malformed line in " + STATIONS_FILE + ": " + line);
            }
            stations.add(new Station(p[0].trim(), p[1].trim(), p[2].trim()));
        }
        return stations;
    }

    @Override
    public void saveStations(List<Station> stations) throws FileProcessingException {
        List<String> lines = new ArrayList<>();
        lines.add("# Format: stationId|name|location");
        for (Station station : stations) {
            lines.add(station.getStationId() + "|" + station.getName() + "|" + station.getLocation());
        }
        writeAllLines(STATIONS_FILE, lines);
    }

    // ------------------------------------------------------------------
    // 3. TRAINS
    // ------------------------------------------------------------------

    @Override
    public List<Train> loadTrains() throws FileProcessingException {
        List<Train> trains = new ArrayList<>();
        for (String line : readAllLines(TRAINS_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length != 3) {
                throw new FileProcessingException("Malformed line in " + TRAINS_FILE + ": " + line);
            }
            int capacity = parsePositiveInteger(p[2], TRAINS_FILE);
            trains.add(new Train(p[0].trim(), p[1].trim(), capacity));
        }
        return trains;
    }

    @Override
    public void saveTrains(List<Train> trains) throws FileProcessingException {
        List<String> lines = new ArrayList<>();
        lines.add("# Format: trainId|name|capacity");
        for (Train train : trains) {
            lines.add(train.getTrainId() + "|" + train.getName() + "|" + train.getCapacity());
        }
        writeAllLines(TRAINS_FILE, lines);
    }

    // ------------------------------------------------------------------
    // 4. ROUTES
    // ------------------------------------------------------------------

    @Override
    public List<Route> loadRoutes(List<Station> stations) throws FileProcessingException {
        List<Route> routes = new ArrayList<>();
        for (String line : readAllLines(ROUTES_FILE)) {
            String[] p = line.split("\\|", -1);
            if (p.length != 4) {
                throw new FileProcessingException("Malformed line in " + ROUTES_FILE + ": " + line);
            }
            String[] src = p[1].split(",", -1);
            String[] dst = p[2].split(",", -1);
            if (src.length < 1 || dst.length < 1) {
                throw new FileProcessingException("Malformed line in " + ROUTES_FILE + ": " + line);
            }
            // Reuse the station objects from the main station list so that any
            // later change to a station is reflected consistently in routes.
            Station source = findStationById(stations, src[0].trim());
            Station destination = findStationById(stations, dst[0].trim());
            if (source == null || destination == null) {
                throw new FileProcessingException("Route " + p[0].trim()
                        + " in " + ROUTES_FILE + " references an unknown station.");
            }
            double distance = parsePositiveDouble(p[3], ROUTES_FILE);
            routes.add(new Route(p[0].trim(), source, destination, distance));
        }
        return routes;
    }

    @Override
    public void saveRoutes(List<Route> routes) throws FileProcessingException {
        List<String> lines = new ArrayList<>();
        lines.add("# Format: routeId|sourceId,sourceName,sourceLocation|destId,destName,destLocation|distanceKm");
        for (Route route : routes) {
            Station src = route.getSource();
            Station dst = route.getDestination();
            String srcPart = src.getStationId() + "," + src.getName() + "," + src.getLocation();
            String dstPart = dst.getStationId() + "," + dst.getName() + "," + dst.getLocation();
            lines.add(route.getRouteId() + "|" + srcPart + "|" + dstPart + "|"
                    + route.getDistance());
        }
        writeAllLines(ROUTES_FILE, lines);
    }

    // ------------------------------------------------------------------
    // 5. TICKETS
    // ------------------------------------------------------------------

    @Override
    public List<Ticket> loadTickets(HashMap<String, User> users, List<Station> stations)
            throws FileProcessingException {
        List<Ticket> tickets = new ArrayList<>();
        for (String line : readAllLines(TICKETS_FILE)) {
            String[] p = line.split("\\|", -1);
            // p = [ticketId, passengerEmail, srcId, dstId, type, status, fare,
            //      dateOfPurchase, dateOfUsed, expiryDate(optional)]
            if (p.length < 9) {
                throw new FileProcessingException("Malformed line in " + TICKETS_FILE + ": " + line);
            }
            User user = users.get(p[1].trim());
            if (!(user instanceof Passenger)) {
                throw new FileProcessingException("Ticket " + p[0].trim()
                        + " in " + TICKETS_FILE + " references an unknown passenger: " + p[1].trim());
            }
            Station source = findStationById(stations, p[2].trim());
            Station destination = findStationById(stations, p[3].trim());
            if (source == null || destination == null) {
                throw new FileProcessingException("Ticket " + p[0].trim()
                        + " in " + TICKETS_FILE + " references an unknown station.");
            }
            TicketType type = parseTicketType(p[4], TICKETS_FILE);
            TicketStatus status = parseTicketStatus(p[5], TICKETS_FILE);
            BigDecimal fare = parseMoney(p[6], TICKETS_FILE);
            String dateOfPurchase = p[7].trim();
            String dateOfUsed = p[8].trim();

            Ticket ticket = new Ticket(p[0].trim(), (Passenger) user,
                    source, destination, type, fare);
            ticket.setDateOfPurchase(dateOfPurchase);
            ticket.setDateOfUsed(dateOfUsed);
            ticket.setStatus(status);
            if (p.length >= 10) {
                ticket.setExpiryDate(p[9].trim());
            }
            // Backwards compatibility: if no expiry date is stored, compute it
            // from the date of purchase and the ticket type.
            if (ticket.getExpiryDate() == null || ticket.getExpiryDate().trim().isEmpty()
                    || "-".equals(ticket.getExpiryDate().trim())) {
                ticket.updateExpiryDate();
            }
            tickets.add(ticket);
            ((Passenger) user).addTicket(ticket);
        }
        return tickets;
    }

    @Override
    public void saveTickets(List<Ticket> tickets) throws FileProcessingException {
        List<String> lines = new ArrayList<>();
        lines.add("# Format: ticketId|passengerEmail|sourceId|destId|type|status|fare|dateOfPurchase|dateOfUsed|expiryDate");
        for (Ticket ticket : tickets) {
            lines.add(ticket.getTicketId() + "|"
                    + ticket.getPassenger().getEmail() + "|"
                    + ticket.getSource().getStationId() + "|"
                    + ticket.getDestination().getStationId() + "|"
                    + ticket.getTicketType() + "|"
                    + ticket.getStatus() + "|"
                    + formatMoney(ticket.getFare()) + "|"
                    + ticket.getDateOfPurchase() + "|"
                    + ticket.getDateOfUsed() + "|"
                    + ticket.getExpiryDate());
        }
        writeAllLines(TICKETS_FILE, lines);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Reads every non-comment line of a file.
     * If the file does not exist yet, an empty list is returned so the
     * application can start with fresh (empty) data.
     *
     * @param path the file path
     * @return list of trimmed lines
     * @throws FileProcessingException if the file cannot be read
     */
    private List<String> readAllLines(String path) throws FileProcessingException {
        List<String> lines = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue; // skip blank lines and comments
                }
                lines.add(trimmed);
            }
        } catch (IOException e) {
            throw new FileProcessingException("Failed to read file: " + path, e);
        }
        return lines;
    }

    /**
     * Writes all lines to a file, creating the folder and file if needed.
     *
     * @param path  the file path
     * @param lines the lines to write
     * @throws FileProcessingException if the file cannot be written
     */
    private void writeAllLines(String path, List<String> lines) throws FileProcessingException {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FileProcessingException("Failed to write file: " + path, e);
        }
    }

    /**
     * Parses a role value; unknown roles are treated as corrupt data.
     *
     * @param value the string to parse
     * @param file  the file being read (for the error message)
     * @return the parsed role
     * @throws FileProcessingException if the value is not a valid role
     */
    private UserRole parseUserRole(String value, String file) throws FileProcessingException {
        try {
            return UserRole.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            throw new FileProcessingException(
                    "Invalid role '" + value.trim() + "' in " + file + ".");
        }
    }

    /**
     * Parses a monetary value (balance or fare). Invalid, non-finite or
     * negative values are treated as corrupt data instead of defaults.
     *
     * @param value the string to parse
     * @param file  the file being read (for the error message)
     * @return the parsed amount (exact decimal, rounded to 2 dp)
     * @throws FileProcessingException if the value is not a valid amount
     */
    private BigDecimal parseMoney(String value, String file) throws FileProcessingException {
        BigDecimal money;
        try {
            money = new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new FileProcessingException(
                    "Invalid amount '" + value.trim() + "' in " + file + ".");
        }
        if (money.signum() < 0) {
            throw new FileProcessingException(
                    "Negative amount '" + value.trim() + "' in " + file + ".");
        }
        return scaleMoney(money);
    }

    /**
     * Parses a positive, finite double (used for route distances).
     *
     * @param value the string to parse
     * @param file  the file being read (for the error message)
     * @return the parsed value
     * @throws FileProcessingException if the value is not a positive number
     */
    private double parsePositiveDouble(String value, String file) throws FileProcessingException {
        double number;
        try {
            number = Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new FileProcessingException(
                    "Invalid number '" + value.trim() + "' in " + file + ".");
        }
        if (Double.isNaN(number) || Double.isInfinite(number) || number <= 0) {
            throw new FileProcessingException(
                    "Invalid number '" + value.trim() + "' in " + file + ".");
        }
        return number;
    }

    /**
     * Parses a positive integer (used for train capacity).
     *
     * @param value the string to parse
     * @param file  the file being read (for the error message)
     * @return the parsed value
     * @throws FileProcessingException if the value is not a positive integer
     */
    private int parsePositiveInteger(String value, String file) throws FileProcessingException {
        int number;
        try {
            number = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new FileProcessingException(
                    "Invalid number '" + value.trim() + "' in " + file + ".");
        }
        if (number <= 0) {
            throw new FileProcessingException(
                    "Invalid number '" + value.trim() + "' in " + file + ".");
        }
        return number;
    }

    /**
     * Finds a station by its ID.
     *
     * @param stations the station list
     * @param id       the station ID to look for
     * @return the station, or null if not found
     */
    private Station findStationById(List<Station> stations, String id) {
        if (id == null) {
            return null;
        }
        for (Station station : stations) {
            if (station.getStationId() != null
                    && station.getStationId().equalsIgnoreCase(id)) {
                return station;
            }
        }
        return null;
    }

    /**
     * Parses a ticket type; unknown types are treated as corrupt data.
     *
     * @param value the string to parse
     * @param file  the file being read (for the error message)
     * @return the parsed ticket type
     * @throws FileProcessingException if the value is not a valid type
     */
    private TicketType parseTicketType(String value, String file) throws FileProcessingException {
        try {
            return TicketType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FileProcessingException(
                    "Invalid ticket type '" + value.trim() + "' in " + file + ".");
        }
    }

    /**
     * Parses a ticket status; unknown statuses are treated as corrupt data.
     *
     * @param value the string to parse
     * @param file  the file being read (for the error message)
     * @return the parsed ticket status
     * @throws FileProcessingException if the value is not a valid status
     */
    private TicketStatus parseTicketStatus(String value, String file) throws FileProcessingException {
        try {
            return TicketStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FileProcessingException(
                    "Invalid ticket status '" + value.trim() + "' in " + file + ".");
        }
    }

    /**
     * Rounds a money value to 2 decimal places (half-up).
     *
     * @param value the raw money value
     * @return the scaled value
     */
    private BigDecimal scaleMoney(BigDecimal value) {
        return value.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Formats a money value as a plain string with 2 decimal places.
     *
     * @param value the money value
     * @return e.g. "12.50"
     */
    private String formatMoney(BigDecimal value) {
        return value == null ? "0.00" : scaleMoney(value).toPlainString();
    }
}
