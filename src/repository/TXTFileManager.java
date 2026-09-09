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
 * <p>Files are stored inside the {@code data} folder of the project.</p>
 */
public class TXTFileManager implements FileManager {

    private static final String USERS_FILE    = DATA_DIR + File.separator + "users.txt";
    private static final String STATIONS_FILE = DATA_DIR + File.separator + "stations.txt";
    private static final String TRAINS_FILE   = DATA_DIR + File.separator + "trains.txt";
    private static final String ROUTES_FILE   = DATA_DIR + File.separator + "routes.txt";
    private static final String TICKETS_FILE  = DATA_DIR + File.separator + "tickets.txt";

    // ------------------------------------------------------------------
    // 1. USERS
    // ------------------------------------------------------------------

    @Override
    public HashMap<String, User> loadUsers() throws FileProcessingException {
        HashMap<String, User> users = new HashMap<>();
        for (String line : readAllLines(USERS_FILE)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] p = line.split("\\|", -1);
            if (p.length < 5) {
                continue; // skip malformed lines
            }
            UserRole role;
            try {
                role = UserRole.valueOf(p[4].trim());
            } catch (IllegalArgumentException e) {
                continue; // unknown role -> skip
            }
            if (role == UserRole.ADMIN) {
                Admin admin = new Admin(p[0].trim(), p[1].trim(), p[2].trim());
                users.put(admin.getEmail(), admin);
            } else {
                Passenger passenger = new Passenger(
                        p[0].trim(),            // email
                        p[1].trim(),            // name
                        p[2].trim(),            // password
                        parseDoubleSafe(p[3])); // balance
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
                line += "|" + String.format("%.2f", passenger.getBalance()) + "|PASSENGER";
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
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] p = line.split("\\|", -1);
            if (p.length < 3) {
                continue;
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
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] p = line.split("\\|", -1);
            if (p.length < 3) {
                continue;
            }
            trains.add(new Train(p[0].trim(), p[1].trim(), (int) parseDoubleSafe(p[2])));
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
    public List<Route> loadRoutes() throws FileProcessingException {
        List<Route> routes = new ArrayList<>();
        for (String line : readAllLines(ROUTES_FILE)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] p = line.split("\\|", -1);
            if (p.length < 4) {
                continue;
            }
            // Source/destination stored as station ID|station name|station location
            String[] src = p[1].split(",", -1);
            String[] dst = p[2].split(",", -1);
            if (src.length < 3 || dst.length < 3) {
                continue;
            }
            Station source = new Station(src[0].trim(), src[1].trim(), src[2].trim());
            Station destination = new Station(dst[0].trim(), dst[1].trim(), dst[2].trim());
            routes.add(new Route(p[0].trim(), source, destination, parseDoubleSafe(p[3])));
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
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] p = line.split("\\|", -1);
            // p = [ticketId, passengerEmail, srcId, dstId, type, status, fare,
            //      dateOfPurchase, expiryDate(optional)]
            if (p.length < 8) {
                continue;
            }
            User user = users.get(p[1].trim());
            if (!(user instanceof Passenger)) {
                continue; // ticket belongs to an unknown passenger -> skip
            }
            Station source = findStationById(stations, p[2].trim());
            Station destination = findStationById(stations, p[3].trim());
            if (source == null || destination == null) {
                continue; // cannot resolve stations -> skip
            }
            TicketType type = parseTicketType(p[4].trim());
            TicketStatus status = parseTicketStatus(p[5].trim());
            double fare = parseDoubleSafe(p[6]);
            String dateOfPurchase = p[7].trim();

            Ticket ticket = new Ticket(p[0].trim(), (Passenger) user,
                    source, destination, type, fare);
            ticket.setDateOfPurchase(dateOfPurchase);
            ticket.setStatus(status);
            if (p.length >= 9) {
                ticket.setExpiryDate(p[8].trim());
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
        lines.add("# Format: ticketId|passengerEmail|sourceId|destId|type|status|fare|dateOfPurchase|expiryDate");
        for (Ticket ticket : tickets) {
            lines.add(ticket.getTicketId() + "|"
                    + ticket.getPassenger().getEmail() + "|"
                    + ticket.getSource().getStationId() + "|"
                    + ticket.getDestination().getStationId() + "|"
                    + ticket.getTicketType() + "|"
                    + ticket.getStatus() + "|"
                    + String.format("%.2f", ticket.getFare()) + "|"
                    + ticket.getDateOfPurchase() + "|"
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
     * Safely parses a double; invalid values fall back to 0.
     *
     * @param value the string to parse
     * @return the parsed value, or 0.0 if unparseable
     */
    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Finds a station by its ID.
     *
     * @param stations the station list
     * @param id       the station ID to look for
     * @return the station, or null if not found
     */
    private Station findStationById(List<Station> stations, String id) {
        for (Station station : stations) {
            if (station.getStationId().equalsIgnoreCase(id)) {
                return station;
            }
        }
        return null;
    }

    /**
     * Safely parses a ticket type; invalid values fall back to SINGLE.
     *
     * @param value the string to parse
     * @return the parsed ticket type
     */
    private TicketType parseTicketType(String value) {
        try {
            return TicketType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TicketType.SINGLE;
        }
    }

    /**
     * Safely parses a ticket status; invalid values fall back to ACTIVE.
     *
     * @param value the string to parse
     * @return the parsed ticket status
     */
    private TicketStatus parseTicketStatus(String value) {
        try {
            return TicketStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TicketStatus.ACTIVE;
        }
    }
}
