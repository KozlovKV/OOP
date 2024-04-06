package kozlov.kirill.pizzeria.workers;

/**
 * Interface for employees.
 * <br>
 * It requires to implement contract of verified job's ending.
 */
public interface RunnableEmployee extends Runnable {
    /**
     * Job finishing starter.
     * <br>
     * Sends signal to employee to show them that it should end its job
     */
    void offerToFinishJob();

    /**
     * Job finished marker.
     *
     * @return true when employee has finished its job
     */
    boolean hasFinishedJob();
}
