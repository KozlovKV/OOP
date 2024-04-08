package kozlov.kirill.pizzeria.employees;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class EmployeesManager<T extends ManagedRunnableEmployee> {
    private final ArrayList<T> employees;

    private final CountDownLatch latch;

    public EmployeesManager(ArrayList<T> employees) {
        this.employees = employees;
        latch = new CountDownLatch(employees.size());
    }

    public void startEmployees() {
        for (T employee : employees) {
            employee.setFinishLatch(latch);
            new Thread(employee).start();
        }
    }

    public void offerEmployeesFinishJob() {
        for (T employee : employees) {
            employee.offerToFinishJob();
        }
    }

    public void waitForAllEmployeesFinished() throws InterruptedException {
        latch.await();
    }
}
