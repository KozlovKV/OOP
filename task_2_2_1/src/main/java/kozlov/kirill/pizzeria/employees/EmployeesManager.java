package kozlov.kirill.pizzeria.employees;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Manager for thread-safe working with employees' list
 *
 * @param <T> employees' type
 */
public class EmployeesManager<T extends ManagedRunnableEmployee> {
    private final ArrayList<T> employees;

    private final CountDownLatch latch;

    /**
     * Constructor.
     * <br>
     * Initialize count-down latch with count as employees' list size
     *
     * @param employees employees' list for running in separated threads
     *
     */
    public EmployeesManager(ArrayList<T> employees) {
        this.employees = employees;
        latch = new CountDownLatch(employees.size());
    }

    /**
     * Runs employees in separated threads.
     */
    public void startEmployees() {
        for (T employee : employees) {
            employee.setFinishLatch(latch);
            new Thread(employee).start();
        }
    }

    /**
     * Sends to employees signal for finishing job.
     */
    public void offerEmployeesFinishJob() {
        for (T employee : employees) {
            employee.offerToFinishJob();
        }
    }

    /**
     * Waits for all employees finish their job.
     * <br>
     * Waits using count-down latch which will unlock when all employees' threads
     * call .countDown() method
     *
     * @throws InterruptedException when latch's waiting was interrupted
     */
    public void waitForAllEmployeesFinished() throws InterruptedException {
        latch.await();
    }
}
