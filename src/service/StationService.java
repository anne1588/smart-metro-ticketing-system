package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Station;

/**
 * Provides station-related operations: add, view (sorted by name using
 * the {@link Comparable} implementation on {@link Station}) and search.
 */
public class StationService {

    private final List<Station> stations;

    /**
     * Creates a station service backed by the given station list.
     *
     * @param stations the shared station list
     */
    public StationService(List<Station> stations) {
        this.stations = stations;
    }

    /**
     * Adds a new station. The station ID must be unique.
     *
     * @param stationId the station ID (e.g. ST01)
     * @param name      the station name
     * @param location  the station location
     * @return true if added successfully, false if the ID already exists
     */
    /*public boolean addStation(String stationId, String name, String location) {
        // Validate unique ID (case-insensitive)
        for (Station station : stations) {
            if (station.getStationId().equalsIgnoreCase(stationId.trim())) {
                return false;
            }
        }
        stations.add(new Station(stationId.trim(), name.trim(), location.trim()));
        return true;
    }*/
    
    public String addStation(int size, String name, String location) {
    	String stationId = generateNextStationId(size);
    	stations.add(new Station(stationId, name, location));
    	return stationId;
    }
    
    public String generateNextStationId(int size) {
    	//int nextNumber = stations.size();
    	int nextNumber = size + 1;
    	return String.format("ST%02d", nextNumber);
    }

    /**
     * Returns all stations sorted alphabetically by name.
     * Sorting is done via the {@code Comparable<Station>} implementation.
     *
     * @return a new sorted list of stations
     */
    public List<Station> getStationsSortedByName() {
        List<Station> sorted = new ArrayList<>(stations);
        Collections.sort(sorted); // uses Station.compareTo()
        return sorted;
    }

    /**
     * Displays all stations in the console.
     */
    public void viewAllStations() {
        if (stations.isEmpty()) {
            System.out.println("\n[Info] No stations available yet.");
            return;
        }
        List<Station> sorted = getStationsSortedByName();
        System.out.println("\n============= ALL STATIONS =============");
        int index = 1;
        for (Station station : sorted) {
            System.out.println(index++ + ". " + station);
        }
        System.out.println("========================================");
    }

    /**
     * Searches for stations whose name contains the given keyword
     * (case-insensitive).
     *
     * @param keyword the search keyword
     * @return a list of matching stations
     */
    public List<Station> searchByName(String keyword) {
        List<Station> results = new ArrayList<>();
        String key = keyword.trim().toLowerCase();
        for (Station station : stations) {
            if (station.getName().toLowerCase().contains(key)) {
                results.add(station);
            }
        }
        return results;
    }

    /**
     * Finds a station by its exact ID.
     *
     * @param stationId the station ID
     * @return the station, or null if not found
     */
    public Station findById(String stationId) {
        for (Station station : stations) {
            if (station.getStationId().equalsIgnoreCase(stationId.trim())) {
                return station;
            }
        }
        return null;
    }

    /**
     * @return the raw station list
     */
    public List<Station> getStations() {
        return stations;
    }
}
