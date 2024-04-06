package kozlov.kirill.pizzeria.workers;

public interface RunnableWorker extends Runnable {
    void offerEndJob();

    boolean hasEndedJob();
}
