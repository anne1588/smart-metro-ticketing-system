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
     * Creates a new route. The route ID must be unique.
     *
     * @param routeId     the route ID (e.g. R01)
     * @param source      the origin station
     * @param destination the destination station
     * @param distance    the distance in kilometres
     * @return true if created successfully, false if the ID already exists
     */
    public boolean createRoute(int size, Station source, Station destination, double distance) {
    	
    	String routeId = generateNextRouteId(size);
    	for (Route route : routes) {
            if (route.getRouteId().equalsIgnoreCase(routeId.trim())) {
                return false;
            }
        }
        if (source == null || destination == null) {
            return false;
        }
        routes.add(new Route(routeId.trim(), source, destination, distance));
        return true;
    }
    
    public String generateNextRouteId(int size) {
    	int nextNumber = size + 1;
    	return String.format("R%02d", nextNumber);
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
     * @return the route, or null if not found
     */
    public Route findById(String routeId) {
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
     * @return the matching route, or null if none exists
     */
    public Route findRoute(Station source, Station destination) {
        for (Route route : routes) {
            if (route.getSource().getStationId().equalsIgnoreCase(source.getStationId())
                    && route.getDestination().getStationId().equalsIgnoreCase(destination.getStationId())) {
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
