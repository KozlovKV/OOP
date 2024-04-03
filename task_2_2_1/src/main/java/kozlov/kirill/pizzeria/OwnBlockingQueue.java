package kozlov.kirill.pizzeria;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class OwnBlockingQueue<T> {
    private int maxSize;
    private final LinkedList<T> list = new LinkedList<>();

    private final ReentrantLock queueLock = new ReentrantLock();
    private final Condition notEmpty = queueLock.newCondition();
    private final Condition notFull = queueLock.newCondition();

    public OwnBlockingQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    Optional<T> poll() {
        T result = null;
        try {
            queueLock.lock();
            while (list.isEmpty())
                notEmpty.await();
            result = list.poll();
            notFull.signal();
        } catch (InterruptedException e) {
            System.err.println("Polling interrupted");
        } finally {
            queueLock.unlock();
        }
        if (result == null)
            return Optional.empty();
        return Optional.of(result);
    }

    void add(T element) {
        try {
            queueLock.lock();
            while (list.size() == maxSize)
                notFull.await();
            list.add(element);
            notEmpty.signal();
        } catch (InterruptedException e) {
            System.err.println("Adding interrupted");
        } finally {
            queueLock.unlock();
        }
    }
}
