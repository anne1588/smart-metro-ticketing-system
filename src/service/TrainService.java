package service;

import java.util.ArrayList;
import java.util.List;

import model.Train;

/**
 * Provides train-related operations: add and view all trains.
 */
public class TrainService {

    private final List<Train> trains;

    /**
     * Creates a train service backed by the given train list.
     *
     * @param trains the shared train list
     */
    public TrainService(List<Train> trains) {
        this.trains = trains;
    }

    /**
     * Adds a new train with the next available generated ID.
     *
     * @param name     the train name
     * @param capacity the maximum passenger capacity
     * @return the newly added train
     */
    public Train addTrain(String name, int capacity) {
        Train train = new Train(generateNextTrainId(), name.trim(), capacity);
        trains.add(train);
        return train;
    }

    /**
     * Generates the next train ID by scanning existing train IDs, so deleted
     * records or non-sequential data never cause a duplicate ID.
     *
     * @return the next train ID (e.g. TR06)
     */
    private String generateNextTrainId() {
        int nextNumber = 0;
        for (Train train : trains) {
            if (train.getTrainId() != null) {
                nextNumber = Math.max(nextNumber, extractNumber(train.getTrainId()));
            }
        }
        return String.format("TR%02d", nextNumber + 1);
    }

    /**
     * Extracts the trailing number from an ID such as "TR05".
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
     * Returns a copy of the train list.
     *
     * @return a new list of trains
     */
    public List<Train> getTrains() {
        return new ArrayList<>(trains);
    }

    /**
     * Displays all trains in the console.
     */
    public void viewAllTrains() {
        if (trains.isEmpty()) {
            System.out.println("\n[Info] No trains available yet.");
            return;
        }
        System.out.println("\n============== ALL TRAINS ==============");
        int index = 1;
        for (Train train : trains) {
            System.out.println(index++ + ". " + train);
        }
        System.out.println("========================================");
    }
}
