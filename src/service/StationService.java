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
     * Adds a new station with the next available generated ID.
     *
     * @param name     the station name
     * @param location the station location
     * @return the newly added station
     */
    public Station addStation(String name, String location) {
        Station station = new Station(generateNextStationId(), name.trim(), location.trim());
        stations.add(station);
        return station;
    }

    /**
     * Generates the next station ID by scanning existing station IDs, so
     * deleted records or non-sequential data never cause a duplicate ID.
     *
     * @return the next station ID (e.g. ST08)
     */
    public String generateNextStationId() {
        int nextNumber = 0;
        for (Station station : stations) {
            if (station.getStationId() != null) {
                nextNumber = Math.max(nextNumber, extractNumber(station.getStationId()));
            }
        }
        return String.format("ST%02d", nextNumber + 1);
    }

    /**
     * Extracts the trailing number from an ID such as "ST07".
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
     * @return a list of matching stations (empty if keyword is null)
     */
    public List<Station> searchByName(String keyword) {
        List<Station> results = new ArrayList<>();
        if (keyword == null) {
            return results;
        }
        String key = keyword.trim().toLowerCase();
        if (key.isEmpty()) {
            return results;
        }
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
     * @return the station, or null if not found or the ID is null
     */
    public Station findById(String stationId) {
        if (stationId == null) {
            return null;
        }
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
