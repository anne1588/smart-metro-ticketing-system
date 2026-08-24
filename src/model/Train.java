package model;

/**
 * Represents a train in the metro system.
 */
public class Train {

    private String trainId;
    private String name;
    private int capacity;

    /**
     * Default constructor required by the file loader.
     */
    public Train() {
    }

    /**
     * Creates a new train.
     *
     * @param trainId  unique train ID (e.g. TR01)
     * @param name     train name (e.g. Kelana Jaya Line)
     * @param capacity maximum number of passengers
     */
    public Train(String trainId, String name, int capacity) {
        this.trainId = trainId;
        this.name = name;
        this.capacity = capacity;
    }

    /**
     * @return the train ID
     */
    public String getTrainId() {
        return trainId;
    }

    /**
     * @param trainId the train ID to set
     */
    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    /**
     * @return the train name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the train name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the maximum capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the maximum capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Returns a human-readable summary of the train.
     *
     * @return formatted train string
     */
    @Override
    public String toString() {
        return "Train ID: " + trainId
                + " | Name: " + name
                + " | Capacity: " + capacity;
    }
}
