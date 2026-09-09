package app;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import enums.TicketStatus;
import enums.TicketType;
import exception.FileProcessingException;
import exception.InvalidLoginException;
import exception.NoDataFoundException;
import exception.TicketNotFoundException;
import fare.FareCalculator;
import fare.StandardFareCalculator;
import model.Admin;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;
import payment.CardPayment;
import payment.CashPayment;
import payment.Payment;
import repository.FileManager;
import repository.TXTFileManager;
import service.PaymentService;
import service.ReportService;
import service.RouteService;
import service.StationService;
import service.TicketService;
import service.TrainService;
import service.UserService;

/**
 * Entry point of the Smart Metro Ticketing System.
 * <p>
 * The application is fully menu-driven and demonstrates the required OOP
 * principles: inheritance (User -> Passenger/Admin), polymorphism
 * (Payment, FareCalculator, FileManager), abstraction (abstract class User,
 * interfaces), encapsulation (private fields + getters/setters),
 * exception handling, collections (ArrayList + HashMap), enums and packages.
 * </p>
 */
public class Main {

    // ---- Shared collections (per assignment requirement) ----
    private static final HashMap<String, User> USERS = new HashMap<>();
    private static final List<Station> STATIONS = new ArrayList<>();
    private static final List<Train> TRAINS = new ArrayList<>();
    private static final List<Route> ROUTES = new ArrayList<>();
    private static final List<Ticket> TICKETS = new ArrayList<>();

    private static final Scanner SCANNER = new Scanner(System.in);
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ---- Polymorphism: FileManager fm = new TXTFileManager(); ----
    private static final FileManager FILE_MANAGER = new TXTFileManager();
    // ---- Polymorphism: FareCalculator calc = new StandardFareCalculator(); ----
    private static final FareCalculator FARE_CALCULATOR = new StandardFareCalculator();

    private static UserService userService;
    private static StationService stationService;
    private static TrainService trainService;
    private static RouteService routeService;
    private static TicketService ticketService;
    private static PaymentService paymentService;
    private static ReportService reportService;

    /**
     * Application entry point.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        try {
            loadData();
        } catch (FileProcessingException e) {
            System.out.println("[Startup] Warning: " + e.getMessage());
            System.out.println("[Startup] Starting with empty data...");
        }

        initServices();

        boolean running = true;
        while (running) {
            try {
                running = showMainMenu();
            } catch (NoSuchElementException e) {
                // End of input (e.g. redirected file / Ctrl+Z): exit gracefully.
                System.out.println("\n[System] Input closed. Exiting system...");
                running = false;
            } catch (Exception e) {
                // Generic safety net: never crash on unexpected errors.
                System.out.println("[System] Unexpected error: " + e.getMessage());
            }
        }

        // ---- Save data before exiting ----
        try {
            saveData();
            System.out.println("[System] All data saved successfully. Goodbye!");
        } catch (FileProcessingException e) {
            System.out.println("[System] ERROR while saving data: " + e.getMessage());
        }

        SCANNER.close();
    }

    // ------------------------------------------------------------------
    // Initialization
    // ------------------------------------------------------------------

    /**
     * Wires up all services. Each service receives the shared collection
     * so every operation works on the same live data.
     */
    private static void initServices() {
        userService = new UserService(USERS);
        stationService = new StationService(STATIONS);
        trainService = new TrainService(TRAINS);
        routeService = new RouteService(ROUTES);
        ticketService = new TicketService(TICKETS, FARE_CALCULATOR);
        paymentService = new PaymentService();
        reportService = new ReportService(TICKETS);
    }

    /**
     * Loads all data from the TXT files on startup.
     *
     * @throws FileProcessingException if any file cannot be read
     */
    private static void loadData() throws FileProcessingException {
        HashMap<String, User> loadedUsers = FILE_MANAGER.loadUsers();
        List<Station> loadedStations = FILE_MANAGER.loadStations();
        List<Train> loadedTrains = FILE_MANAGER.loadTrains();
        List<Route> loadedRoutes = FILE_MANAGER.loadRoutes();
        List<Ticket> loadedTickets = FILE_MANAGER.loadTickets(loadedUsers, loadedStations);

        USERS.putAll(loadedUsers);
        STATIONS.addAll(loadedStations);
        TRAINS.addAll(loadedTrains);
        ROUTES.addAll(loadedRoutes);
        TICKETS.addAll(loadedTickets);

        System.out.println("[System] Data loaded successfully:");
        System.out.println("  - " + USERS.size() + " user(s)");
        System.out.println("  - " + STATIONS.size() + " station(s)");
        System.out.println("  - " + TRAINS.size() + " train(s)");
        System.out.println("  - " + ROUTES.size() + " route(s)");
        System.out.println("  - " + TICKETS.size() + " ticket(s)");
    }

    /**
     * Saves all data back to the TXT files on exit.
     *
     * @throws FileProcessingException if any file cannot be written
     */
    private static void saveData() throws FileProcessingException {
        FILE_MANAGER.saveUsers(USERS);
        FILE_MANAGER.saveStations(STATIONS);
        FILE_MANAGER.saveTrains(TRAINS);
        FILE_MANAGER.saveRoutes(ROUTES);
        FILE_MANAGER.saveTickets(TICKETS);
    }
    
    // Calling this generates the live time at the moment it is called
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(FORMATTER);
    }

    // ------------------------------------------------------------------
    // Main menu
    // ------------------------------------------------------------------

    /**
     * Displays the main menu and dispatches the selected action.
     *
     * @return false when the user chooses to exit, true otherwise
     */
    private static boolean showMainMenu() {
        System.out.println("\n=== SMART METRO TICKETING SYSTEM ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = readInt();
        switch (choice) {
            case 1:
                registerPassenger();
                return true;
            case 2:
                login();
                return true;
            case 3:
                return false;
            default:
                System.out.println("[Error] Invalid option. Please choose 1 - 3.");
                return true;
        }
    }

    /**
     * Reads an integer from the console with validation.
     *
     * @return the parsed integer, or -1 if the input was not a number
     */
    private static int readInt() {
        try {
            return Integer.parseInt(SCANNER.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static int readPositiveInt(String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				int value = Integer.parseInt(SCANNER.nextLine().trim());
				if (value <= 0) {
					System.out.println("[Error] Value must be greater than 0.");
					continue;
				}
				return value;
			} catch (NumberFormatException e) {
				System.out.println("[Error] Please enter a valid integer.");
			}
		}
	}

    /**
     * Reads a positive double from the console with validation.
     *
     * @param prompt the prompt to show
     * @return a positive double value
     */
    private static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(SCANNER.nextLine().trim());
                if (value <= 0) {
                    System.out.println("[Error] Amount must be greater than 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a top-up amount where entering zero aborts the flow.
     *
     * @param prompt the prompt to show
     * @return the entered amount, or {@code null} if the user aborts
     */
    private static Double readTopUpAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(SCANNER.nextLine().trim());
                if (value < 0) {
                    System.out.println("[Error] Amount must be greater than 0.");
                    continue;
                }
                if (value == 0) {
                    return null;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid number.");
            }
        }
    }

    /**
     * Reads a non-blank string from the console.
     *
     * @param prompt the prompt to show
     * @return the trimmed string
     */
    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("[Error] Input cannot be empty.");
                continue;
            }
            return input;
        }
    }

    /**
     * Reads a password from the console with character masking (asterisks).
     *
     * @param prompt the prompt to show
     * @return the entered password
     */
    private static String readPassword(String prompt) {
        System.out.print(prompt);
        Console console = System.console();
        if (console == null) {
            // Fallback for environments where System.console() is null (e.g., IDEs)
            return SCANNER.nextLine().trim();
        }
        char[] passwordArray = console.readPassword();
        return (passwordArray != null) ? new String(passwordArray) : "";
    }
    
    private static String passwordValidation(String prompt) {
    	while (true) {
    		System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("[Error] Input cannot be empty.");
                continue;
            }
            if(input.length() < 8) {
            	System.out.println("[Error] Password must be at least 8 characters long.");
            	continue;
            }
            if(!(input.matches(".*[A-Z].*")) || !(input.matches(".*[0-9].*"))) {
            	System.out.println("[Error] Password must contain at least one uppercase letter and one number.");
            	continue;
            }
            
            return input;
        }
    }

    // ------------------------------------------------------------------
    // Registration & login
    // ------------------------------------------------------------------

    /**
     * Registers a new passenger with validated inputs.
     */
    private static void registerPassenger() {
        System.out.println("\n-------- PASSENGER REGISTRATION --------");
        String name = readNonEmpty("Name            : ");

        String email;
        while (true) {
            System.out.print("Email           : ");
            email = SCANNER.nextLine().trim().toLowerCase();
            if (email.isEmpty()) {
                System.out.println("[Error] Email cannot be empty.");
                continue;
            }
            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("[Error] Invalid email format (must contain @ and .com/my/org).");
                continue;
            }
            if (userService.isEmailTaken(email)) {
                System.out.println("[Error] This email is already registered.");
                continue;
            }
            break;
        }

        String password = passwordValidation("Password        : ");
        double balance = readPositiveDouble("How much do you want to Top-up? : RM ");
        
        

        boolean ok = userService.registerPassenger(email, name, password, balance);
        if (ok) {
        	try {
        		FILE_MANAGER.saveUsers(USERS);
        		System.out.println("\n[Success] Registration complete. You can now log in.");
        	}catch(FileProcessingException e) {
				System.out.println("\n[Error] Registration failed while saving data: " + e.getMessage());
			}
        } else {
            System.out.println("\n[Error] Registration failed (email already exists).");
        }
    }

    /**
     * Handles the login flow and opens the correct role menu.
     */
    private static void login() {
        System.out.println("\n----------------- LOGIN -----------------");
        String email = readNonEmpty("Email    : ");
        String password = readPassword("Password : ");

        try {
            User user = userService.login(email, password);
            System.out.println("\n[Success] Welcome back, " + user.getName() + "!");

            if (user instanceof Admin) {
                adminMenu();
            } else if (user instanceof Passenger) {
                passengerMenu((Passenger) user);
            } else {
                System.out.println("[Error] Unknown user role.");
            }
        } catch (InvalidLoginException e) {
            System.out.println("\n[Error] Login failed: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Passenger menu
    // ------------------------------------------------------------------

    /**
     * Displays and processes the passenger menu until logout.
     *
     * @param passenger the logged-in passenger
     */
    private static void passengerMenu(Passenger passenger) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n------------ Passenger Menu ------------");
            System.out.println("1. View Profile");
            System.out.println("2. Top Up Balance");
            System.out.println("3. Search Station");
            System.out.println("4. Buy Ticket");
            System.out.println("5. Use Ticket");
            System.out.println("6. Cancel Ticket");
            System.out.println("7. View My Tickets");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");

            switch (readInt()) {
                case 1:
                    userService.viewProfile(passenger);
                    break;
                case 2:
                    topUpBalance(passenger);
                    break;
                case 3:
                    searchStationFlow();
                    break;
                case 4:
                    buyTicketFlow(passenger);
                    break;
                case 5:
                    useTicketFlow(passenger);
                    break;
                case 6:
                    cancelTicketFlow(passenger);
                    break;
                case 7:
                    ticketService.displayTickets(
                            ticketService.getTicketsForPassenger(passenger), "MY TICKETS");
                    break;
                case 8:
                    System.out.println("\n[Info] Logged out. See you soon!");
                    inMenu = false;
                    break;
                default:
                    System.out.println("[Error] Invalid option. Please choose 1 - 8.");
            }
        }
    }

    /**
     * Top-up flow for a passenger.
     *
     * @param passenger the passenger
     */
    private static void topUpBalance(Passenger passenger) {
    	System.out.println("\n============ TOP-UP BALANCE ===========");
    	System.out.println("Current balance: RM " + String.format("%.2f", passenger.getBalance()));
        Double amount = readTopUpAmount("Enter top-up amount : RM ");
        if (amount == null) {
            System.out.println("[Info] Top-up cancelled.");
            return;
        }

        if (userService.topUpBalance(passenger, amount)) {
        	try {
        		FILE_MANAGER.saveUsers(USERS);
                System.out.println("[Success] Top-up complete. New balance: RM "
                        + String.format("%.2f", passenger.getBalance()));
        	}catch(FileProcessingException e) {
				System.out.println("[Error] Top-up failed while saving data: " + e.getMessage());
			}    
        } else {
            System.out.println("[Error] Top-up failed. Amount must be positive.");
        }
    }

    // ------------------------------------------------------------------
    // Buy ticket flow
    // ------------------------------------------------------------------

    /**
     * Full ticket-buying flow: View routes, choose source & destination station,
     * choose ticket type, review fare, then pay by cash or card.
     *
     * @param passenger the passenger buying the ticket
     */
    private static void buyTicketFlow(Passenger passenger) {
        System.out.println("\n------------ BUY TICKET ------------");
        
        routeService.viewAllRoutesForTicket();

        if (STATIONS.size() < 2) {
            System.out.println("[Error] At least 2 stations are required to buy a ticket.");
            return;
        }
        if (ROUTES.isEmpty()) {
            System.out.println("[Error] No routes available. Ask an admin to create routes first.");
            return;
        }

        Station source = chooseStation("Select SOURCE station:", "Enter station ID: ");
        if (source == null) {
            return;
        }
        Station destination = chooseStation("Select DESTINATION station:", "Enter station ID: ");
        if (destination == null) {
            return;
        }
        if (source.getStationId().equalsIgnoreCase(destination.getStationId())) {
            System.out.println("[Error] Source and destination must be different.");
            return;
        }

        Route route = routeService.findRoute(source, destination);
        if (route == null) {
            System.out.println("[Error] No route exists between "
                    + source.getName() + " and " + destination.getName() + ".");
            return;
        }

        TicketType type = chooseTicketType();
        if (type == null) {
            return;
        }

        double fare = FARE_CALCULATOR.calculateFare(route.getDistance(), type);
        System.out.println("\n--- Ticket Summary ---");
        System.out.println("Source      : " + source.getName());
        System.out.println("Destination : " + destination.getName());
        System.out.println("Distance    : " + route.getDistance() + " km");
        System.out.println("Ticket type : " + type);
        System.out.println("Fare        : RM " + String.format("%.2f", fare));

        if (passenger.getBalance() < fare) {
            System.out.println("[Error] Insufficient balance (RM "
                    + String.format("%.2f", passenger.getBalance())
                    + "). Please top up first.");
            return;
        }

        System.out.print("\nConfirm purchase? (Y/N): ");
        if (!SCANNER.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("[Info] Purchase cancelled.");
            return;
        }

        // Create the ticket (status ACTIVE) and process the payment.
        Ticket ticket = ticketService.buyTicket(passenger, route, type);
        ticket.setDateOfPurchase(getCurrentDateTime());

        if (processPaymentForTicket(passenger, ticket)) {
        	try {
        		FILE_MANAGER.saveTickets(TICKETS);
        		System.out.println("\n[Success] Ticket booked and paid successfully!");
                System.out.println("  " + ticket);
        	}catch(FileProcessingException e) {}
            
        } else {
            // Payment failed: remove the ticket so nothing is kept unpaid.
            TICKETS.remove(ticket);
            passenger.removeTicket(ticket);
            System.out.println("\n[Info] Booking cancelled because payment failed.");
        }
    }

    /**
     * Lets the user pick a station from the list by entering its ID.
     *
     * @param title     the heading to show
     * @param prompt    the prompt text
     * @return the chosen station, or null if the user aborts
     */
    private static Station chooseStation(String title, String prompt) {
        System.out.println("\n" + title);
        for (Station s : STATIONS) {
            System.out.println("  " + s.getStationId() + " - " + s.getName()
                    + " (" + s.getLocation() + ")");
        }
        System.out.println("  (Type 'exit' to abort)");
        while (true) {
            String input = readNonEmpty(prompt);
            if (input.equalsIgnoreCase("exit")) {
                return null;
            }
            Station station = stationService.findById(input);
            if (station == null) {
                System.out.println("[Error] Unknown station ID. Try again.");
                continue;
            }
            return station;
        }
    }

    /**
     * Lets the user choose a ticket type.
     *
     * @return the chosen ticket type, or null if the user aborts
     */
    private static TicketType chooseTicketType() {
        System.out.println("\nSelect ticket type:");
        System.out.println("  1. SINGLE   (base fare = distance x RM 0.50)");
        System.out.println("  2. DAILY    (base fare x 2)");
        System.out.println("  3. MONTHLY  (base fare x 20)");
        while(true) {
        	System.out.print("Enter 1 - 3 (or 0 to abort): ");
            switch (readInt()) {
                case 1:
                    return TicketType.SINGLE;
                case 2:
                    return TicketType.DAILY;
                case 3:
                    return TicketType.MONTHLY;
                case 0:
                	System.out.println("Aborted from buying ticket.");
                    return null;
                default:
                    System.out.println("[Error] Invalid choice. Try again.");
            }
        }
        
    }

    /**
     * Runs the payment sub-flow for a ticket.
     *
     * @param passenger the passenger
     * @param ticket    the ticket to pay for
     * @return true if payment succeeded
     */
    private static boolean processPaymentForTicket(Passenger passenger, Ticket ticket) {
        System.out.println("\nSelect payment method:");
        System.out.println("  1. Cash Payment");
        System.out.println("  2. Card Payment");
        System.out.print("Enter 1 or 2: ");
        int method = readInt();

        Payment payment;
        if (method == 1) {
            payment = new CashPayment();
        } else if (method == 2) {
            System.out.print("Card number   : ");
            String cardNumber = SCANNER.nextLine().trim();
            System.out.print("Cardholder name: ");
            String cardHolder = SCANNER.nextLine().trim();
            payment = new CardPayment(cardNumber, cardHolder);
        } else {
            System.out.println("[Error] Invalid payment method.");
            return false;
        }

        return paymentService.processPayment(payment, passenger, ticket);
    }

    // ------------------------------------------------------------------
    // Cancel ticket flow
    // ------------------------------------------------------------------

    /**
     * Cancels an ACTIVE ticket belonging to the passenger.
     *
     * @param passenger the passenger
     */
    private static void cancelTicketFlow(Passenger passenger) {
        System.out.println("\n------------ CANCEL TICKET ------------");
        List<Ticket> myActive = new ArrayList<>();
        for (Ticket t : ticketService.getTicketsForPassenger(passenger)) {
            if (t.getStatus() == TicketStatus.ACTIVE) {
                myActive.add(t);
            }
        }
        if (myActive.isEmpty()) {
            System.out.println("[Info] You have no ACTIVE tickets to cancel.");
            return;
        }
        System.out.println("Your ACTIVE tickets:");
        for (Ticket t : myActive) {
            System.out.println("  " + t.getTicketId() + " - "
                    + t.getSource().getName() + " -> " + t.getDestination().getName()
                    + " | RM " + String.format("%.2f", t.getFare()));
        }

        System.out.print("Enter ticket ID to cancel: ");
        String ticketId = SCANNER.nextLine().trim();
        try {
            Ticket cancelled = ticketService.cancelTicket(ticketId);
            System.out.println("[Success] Ticket " + cancelled.getTicketId()
                    + " cancelled. RM " + String.format("%.2f", cancelled.getFare())
                    + " refunded to your balance. New balance: RM "
                    + String.format("%.2f", passenger.getBalance()));
            FILE_MANAGER.saveTickets(TICKETS);
        } catch (TicketNotFoundException e) {
            System.out.println("[Error] " + e.getMessage());
        }
        catch(FileProcessingException e) {}
    }
    
    
    // ------------------------------------------------------------------
    // Use ticket flow
    // ------------------------------------------------------------------
    /**
     * Uses an ACTIVE ticket belonging to the passenger and changes its status to USED.
     *
     * @param passenger the passenger
     */
    private static void useTicketFlow(Passenger passenger) {
        System.out.println("\n------------ USE TICKET ------------");
        List<Ticket> myActive = new ArrayList<>();
        for (Ticket t : ticketService.getTicketsForPassenger(passenger)) {
            if (t.getStatus() == TicketStatus.ACTIVE) {
                myActive.add(t);
            }
        }
        if (myActive.isEmpty()) {
            System.out.println("[Info] You have no ACTIVE tickets to use.");
            return;
        }
        System.out.println("Your ACTIVE tickets:");
        for (Ticket t : myActive) {
            System.out.println("  " + t.getTicketId() + " - "
                    + t.getSource().getName() + " -> " + t.getDestination().getName()
                    + " | RM " + String.format("%.2f", t.getFare()));
        }

        System.out.print("Enter ticket ID to use: ");
        String ticketId = SCANNER.nextLine().trim();
        try {
            Ticket used = ticketService.useTicket(ticketId);
            used.setDateOfUsed(getCurrentDateTime());
            System.out.println("[Success] Ticket " + used.getTicketId()
                    + " (" + used.getSource().getName() + " -> " + used.getDestination().getName()
                    + ") has been successfully used.");
            FILE_MANAGER.saveTickets(TICKETS);
        } catch (TicketNotFoundException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (FileProcessingException e) {}
    }

    // ------------------------------------------------------------------
    // Admin menu
    // ------------------------------------------------------------------

    /**
     * Displays and processes the admin menu until logout.
     */
    private static void adminMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n------------- Admin Menu -------------");
            System.out.println("1.  Add Station");
            System.out.println("2.  View Stations");
            System.out.println("3.  Search Station");
            System.out.println("4.  Add Train");
            System.out.println("5.  View Trains");
            System.out.println("6.  Create Route");
            System.out.println("7.  View Routes");
            System.out.println("8.  View All Users");
            System.out.println("9.  Generate Report");
            System.out.println("10. Logout");
            System.out.print("Choose an option: ");

            switch (readInt()) {
                case 1:
                    addStationFlow();
                    break;
                case 2:
                    stationService.viewAllStations();
                    break;
                case 3:
                    searchStationFlow();
                    break;
                case 4:
                	addTrainFlow();
                    break;
                case 5:
                    trainService.viewAllTrains();
                    break;
                case 6:
                	createRouteFlow();  
                    break;
                case 7:
                    routeService.viewAllRoutes();
                    break;
                case 8:
                    userService.viewAllUsers();
                    break;
                case 9:
                	generateReportMenu();
                    break;
                case 10:
                    System.out.println("\n[Info] Logged out. See you soon!");
                    inMenu = false;
                    break;
                default:
                    System.out.println("[Error] Invalid option. Please choose 1 - 10.");
            }
        }
    }

    /**
     * Add-station flow for the admin.
     */
    private static void addStationFlow(){
 	
    	System.out.println("\n------------ ADD STATION ------------");
        String name = readNonEmpty("Station name : ");
        String location = readNonEmpty("Location     : ");
        
        stationService.addStation(STATIONS.size(), name, location);
        
        try {
        	FILE_MANAGER.saveStations(STATIONS);
        	System.out.println("Station added successfully!");
        }catch(FileProcessingException e) {
        	System.out.println("Station was not added!");
        }
    }

    /**
     * Search-station flow (used by both the admin and passenger menus).
     */
    private static void searchStationFlow() {
        System.out.println("\n------------ SEARCH STATION ------------");
        String keyword = readNonEmpty("Enter station name (or part of it): ");
        List<Station> results = stationService.searchByName(keyword);
        if (results.isEmpty()) {
            System.out.println("[Info] No station found matching: " + keyword);
            return;
        }
        System.out.println("[Info] " + results.size() + " station(s) found:");
        int index = 1;
        for (Station station : results) {
            System.out.println(index++ + ". " + station);
        }
    }

    /**
     * Add-train flow for the admin.
     */
    private static void addTrainFlow(){
    	
        System.out.println("\n------------ ADD TRAIN ------------");
        String name = readNonEmpty("Train name  : ");
        int capacity;
        while (true) {
            System.out.print("Capacity    : ");
            try {
                capacity = Integer.parseInt(SCANNER.nextLine().trim());
                if (capacity <= 0) {
                    System.out.println("[Error] Capacity must be positive.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid number.");
            }
        }
        
        trainService.addTrain(TRAINS.size(), name, capacity);
        
        try {
        	FILE_MANAGER.saveTrains(TRAINS);
        	System.out.println("Train added successfully!");
        }catch(FileProcessingException e) {
        	System.out.println("Train was not added!");
        }
    }

    /**
     * Create-route flow for the admin.
     */
    private static void createRouteFlow(){
    	
    	int size = ROUTES.size();
        System.out.println("\n------------ CREATE ROUTE ------------");
        if (STATIONS.size() < 2) {
            System.out.println("[Error] At least 2 stations are needed to create a route.");
            return;
        }

        //String routeId = readNonEmpty("Route ID       : ");

        Station source = chooseStation("Select SOURCE station:", "Enter station ID: ");
        if (source == null) {
            return;
        }
        Station destination = chooseStation("Select DESTINATION station:", "Enter station ID: ");
        if (destination == null) {
            return;
        }
        if (source.getStationId().equalsIgnoreCase(destination.getStationId())) {
            System.out.println("[Error] Source and destination must be different.");
            return;
        }

        double distance = readPositiveDouble("Distance (km)  : ");

        if (routeService.createRoute(size, source, destination, distance)) {
        	 try {
             	FILE_MANAGER.saveRoutes(ROUTES);
             	 System.out.println("[Success] Route created: " + routeService.generateNextRouteId(size) + " ("
                          + source.getName() + " -> " + destination.getName() + ", "
                          + distance + " km)");
             	 
             } catch(FileProcessingException e) {
             	System.out.println("[Error] Could not create route. The route ID may already exist.");
             }
        } else {
            System.out.println("[Error] Could not create route. The route ID may already exist.");
        }
        
       
    }
    
    public static void generateReportMenu() {
		while (true) {
			System.out.println("\n------------ GENERATE REPORT ------------");
			System.out.println("1. Monthly Report");
			System.out.println("2. Yearly Report");
			System.out.println("0. Back to Admin Menu");
			System.out.print("Choose an option: ");
			int choice = readInt();
			switch (choice) {
				case 1:
					int year = readPositiveInt("Enter year (e.g., 2026): ");
					int month = readPositiveInt("Enter month (1-12): ");
					try {
						reportService.generateMonthlyReport(year, month);
					} catch (NoDataFoundException e) {
						System.out.println("[Error] " + e.getMessage());
					}
					break;
				case 2:
					year = readPositiveInt("Enter year (e.g., 2026): ");
					try {
						reportService.generateYearlyReport(year);
					} catch (NoDataFoundException e) {
						System.out.println("[Error] " + e.getMessage());
					}
					break;
				case 0:
					System.out.println("[Info] Returning to admin menu.");
					return;
				default:
					System.out.println("[Error] Invalid option. Please choose 1 or 2.");
			}
		}	
    }
}
