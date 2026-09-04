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
     * Adds a new train. The train ID must be unique.
     *
     * @param trainId  the train ID (e.g. TR01)
     * @param name     the train name
     * @param capacity the maximum passenger capacity
     * @return true if added successfully, false if the ID already exists
     */
    /*public boolean addTrain(String trainId, String name, int capacity) {
        for (Train train : trains) {
            if (train.getTrainId().equalsIgnoreCase(trainId.trim())) {
                return false;
            }
        }
        trains.add(new Train(trainId.trim(), name.trim(), capacity));
        return true;
    }*/
    
    public String addTrain(int size, String name, int capacity) {
        String trainId = generateNextTrainId(size);
        trains.add(new Train(trainId, name.trim(), capacity));
        return trainId;
    }
    
    private String generateNextTrainId(int size) {
        int nextNumber = size + 1;
        return String.format("TR%02d", nextNumber);
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
