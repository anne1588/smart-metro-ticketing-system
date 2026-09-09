package service;

import java.util.ArrayList;
import java.util.List;

import model.Route;
import model.Station;

/**
 * Provides route-related operations: create and view routes.
 * <p>A route is created from an existing source and destination station,
 * together with the distance in kilometres between them.</p>
 */
public class RouteService {

    private final List<Route> routes;

    /**
     * Creates a route service backed by the given route list.
     *
     * @param routes the shared route list
     */
    public RouteService(List<Route> routes) {
        this.routes = routes;
    }

    /**
     * Creates a new route with the next available generated ID.
     *
     * @param source      the origin station
     * @param destination the destination station
     * @param distance    the distance in kilometres
     * @return the newly created route, or null if a station is missing
     */
    public Route createRoute(Station source, Station destination, double distance) {
        if (source == null || destination == null) {
            return null;
        }
        String routeId = generateNextRouteId();
        Route route = new Route(routeId, source, destination, distance);
        routes.add(route);
        return route;
    }

    /**
     * Generates the next route ID by scanning existing route IDs, so deleted
     * records or non-sequential data never cause a duplicate ID.
     *
     * @return the next route ID (e.g. R08)
     */
    public String generateNextRouteId() {
        int nextNumber = 0;
        for (Route route : routes) {
            if (route.getRouteId() != null) {
                nextNumber = Math.max(nextNumber, extractNumber(route.getRouteId()));
            }
        }
        return String.format("R%02d", nextNumber + 1);
    }

    /**
     * Extracts the trailing number from an ID such as "R07".
     *
     * @param id the ID
     * @return the numeric part, or 0 if none is present
     */
    private int extractNumber(String id) {
        String digits = id.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Returns a copy of the route list.
     *
     * @return a new list of routes
     */
    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }

    /**
     * Finds a route by its ID.
     *
     * @param routeId the route ID
     * @return the route, or null if not found or the ID is null
     */
    public Route findById(String routeId) {
        if (routeId == null) {
            return null;
        }
        for (Route route : routes) {
            if (route.getRouteId().equalsIgnoreCase(routeId.trim())) {
                return route;
            }
        }
        return null;
    }

    /**
     * Finds a route matching the given source and destination stations.
     *
     * @param source      the origin station
     * @param destination the destination station
     * @return the matching route, or null if none exists or an argument is null
     */
    public Route findRoute(Station source, Station destination) {
        if (source == null || destination == null) {
            return null;
        }
        for (Route route : routes) {
            Station src = route.getSource();
            Station dst = route.getDestination();
            if (src != null && dst != null
                    && src.getStationId() != null && dst.getStationId() != null
                    && src.getStationId().equalsIgnoreCase(source.getStationId())
                    && dst.getStationId().equalsIgnoreCase(destination.getStationId())) {
                return route;
            }
        }
        return null;
    }

    /**
     * Displays all routes in the console.
     */
    public void viewAllRoutes() {
        if (routes.isEmpty()) {
            System.out.println("\n[Info] No routes available yet.");
            return;
        }
        System.out.println("\n============== ALL ROUTES ==============");
        int index = 1;
        for (Route route : routes) {
            System.out.println(index++ + ". " + route);
        }
        System.out.println("========================================");
    }
    
    /**
     * Displays all routes except RouteID and will be used for ticket purchase
     */
    public void viewAllRoutesForTicket() {
    	if(routes.isEmpty()) {
    		System.out.println("\n[Info] No routes available yet.");
            return;
    	}
    	System.out.println("\n============== ALL ROUTES ==============");
        int index = 1;
        for (Route route : routes) {
			System.out.println(index++ + ". " + route.getSource().getName() + " -> " + route.getDestination().getName() + " | Distance: " + route.getDistance() + " km");
		}
        System.out.println("========================================");
    }
}
