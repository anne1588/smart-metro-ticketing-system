package model;

/**
 * Represents a metro station.
 * <p>Implements {@link Comparable} so that stations can be sorted
 * alphabetically by name (bonus feature required by the assignment).</p>
 */
public class Station implements Comparable<Station> {

    private String stationId;
    private String name;
    private String location;

    /**
     * Default constructor required by the file loader.
     */
    public Station() {
    }

    /**
     * Creates a new station.
     *
     * @param stationId unique station ID (e.g. ST01)
     * @param name      station name (e.g. KL Sentral)
     * @param location  physical location / zone (e.g. Kuala Lumpur)
     */
    public Station(String stationId, String name, String location) {
        this.stationId = stationId;
        this.name = name;
        this.location = location;
    }

    /**
     * @return the station ID
     */
    public String getStationId() {
        return stationId;
    }

    /**
     * @param stationId the station ID to set
     */
    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    /**
     * @return the station name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the station name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the station location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the station location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Compares this station with another station by name (case-insensitive),
     * which allows {@code Collections.sort()} to order stations alphabetically.
     *
     * @param other the station to compare against
     * @return negative / zero / positive as per String comparison
     */
    @Override
    public int compareTo(Station other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    /**
     * Returns a human-readable summary of the station.
     *
     * @return formatted station string
     */
    @Override
    public String toString() {
        return "Station ID: " + stationId
                + " | Name: " + name
                + " | Location: " + location;
    }
}
