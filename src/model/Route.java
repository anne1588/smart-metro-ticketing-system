package model;

/**
 * Represents a route between two metro stations.
 * <p>A route links a source station to a destination station and stores
 * the distance in kilometres, which is used by the fare calculator.</p>
 */
public class Route {

    private String routeId;
    private Station source;
    private Station destination;
    private double distance;

    /**
     * Default constructor required by the file loader.
     */
    public Route() {
    }

    /**
     * Creates a new route.
     *
     * @param routeId     unique route ID (e.g. R01)
     * @param source      the origin station
     * @param destination the destination station
     * @param distance    the distance in kilometres
     */
    public Route(String routeId, Station source, Station destination, double distance) {
        this.routeId = routeId;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    /**
     * @return the route ID
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * @param routeId the route ID to set
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    /**
     * @return the source station
     */
    public Station getSource() {
        return source;
    }

    /**
     * @param source the source station to set
     */
    public void setSource(Station source) {
        this.source = source;
    }

    /**
     * @return the destination station
     */
    public Station getDestination() {
        return destination;
    }

    /**
     * @param destination the destination station to set
     */
    public void setDestination(Station destination) {
        this.destination = destination;
    }

    /**
     * @return the distance in kilometres
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance in kilometres to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns a human-readable summary of the route.
     *
     * @return formatted route string
     */
    @Override
    public String toString() {
        return "Route ID: " + routeId
                + " | From: " + source.getName()
                + " -> To: " + destination.getName()
                + " | Distance: " + distance + " km";
    }
}
