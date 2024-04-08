package kozlov.kirill.pizzeria.employees;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Interface for employees.
 * <br>
 * It requires to implement contract of verified job's ending.
 */
public interface ManagedRunnableEmployee extends Runnable {
    /**
     * Sets finish semaphore which employee will release when finished job.
     *
     * @param finishLatch condition for releasing
     */
    void setFinishLatch(CountDownLatch finishLatch);

    /**
     * Job finishing starter.
     * <br>
     * Sends signal to employee to show them that it should end its job
     */
    void offerToFinishJob();
}
