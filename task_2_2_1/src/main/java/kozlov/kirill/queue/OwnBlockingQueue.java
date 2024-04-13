package kozlov.kirill.queue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Own blocking queue with locks.
 *
 * @param <T> element's type
 */
public class OwnBlockingQueue<T> {
    private final int maxSize;
    private final LinkedList<T> list = new LinkedList<>();

    private final ReentrantLock queueLock = new ReentrantLock();
    private final Condition notEmpty = queueLock.newCondition();
    private final Condition notFull = queueLock.newCondition();

    private volatile boolean pollingpPohibited = false;
    private volatile boolean addingPohibited = false;

    /**
     * Max size constructor.
     *
     * @param maxSize queue size
     */
    public OwnBlockingQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Pre-defined data constructor.
     * <br>
     * Max size is assigned as data list size
     *
     * @param data data for queue
     */
    public OwnBlockingQueue(ArrayList<T> data) {
        this.maxSize = data.size();
        list.addAll(data);
    }

    /**
     * Max size getter.
     *
     * @return max size
     */
    public int maxSize() {
        return maxSize;
    }

    /**
     * UNRELIABLE getter for current temporal queue size.
     *
     * @return current temporal size
     */
    public int sizeUnreliable() {
        int size;
        queueLock.lock();
        size = list.size();
        queueLock.unlock();
        return size;
    }

    /**
     * UNRELIABLE predicate for checking queue is empty.
     *
     * @return true when queue is empty at this moment
     */
    public boolean isEmptyUnreliable() {
        return sizeUnreliable() == 0;
    }

    /**
     * UNRELIABLE method for getting temporal queue copy
     *
     * @return list temporal shallow copy
     */
    public ArrayList<T> getListCopyUnreliable() {
        queueLock.lock();
        ArrayList<T> copyList = new ArrayList<>(list);
        queueLock.unlock();
        return copyList;
    }

    /**
     * Prohibits next poll requests.
     */
    public void prohibitPolling() {
        queueLock.lock();
        pollingpPohibited = true;
        notEmpty.signalAll();
        queueLock.unlock();
    }

    /**
     * Simple blocking poll.
     *
     * @return the first queue element
     *
     * @throws ProhibitedQueueActionException when polling was prohibited
     * @throws InterruptedException when thread was interrupted
     */
    public T poll(
    ) throws ProhibitedQueueActionException, InterruptedException {
        if (pollingpPohibited) {
            throw new ProhibitedQueueActionException();
        }
        try {
            queueLock.lock();
            while (!pollingpPohibited && list.isEmpty()) {
                notEmpty.await();
            }
            if (pollingpPohibited) {
                throw new ProhibitedQueueActionException();
            }
            notFull.signal();
            return list.poll();
        } finally {
            queueLock.unlock();
        }
    }

    /**
     * Timeout blocking poll.
     *
     * @param timeoutMs time in ms which thread will wait for element
     *     when there are no elements now
     *
     * @return the first queue element
     *
     * @throws TimeoutException when time's up
     * @throws ProhibitedQueueActionException when polling was prohibited
     * @throws InterruptedException when thread was interrupted
     */
    public T poll(
        long timeoutMs
    ) throws TimeoutException, ProhibitedQueueActionException, InterruptedException {
        if (pollingpPohibited) {
            throw new ProhibitedQueueActionException();
        }
        try {
            queueLock.lock();
            while (list.isEmpty()) {
                if (!notEmpty.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException("Timeout while polling");
                }
            }
            if (pollingpPohibited) {
                throw new ProhibitedQueueActionException();
            }
            notFull.signal();
            return list.poll();
        } finally {
            queueLock.unlock();
        }
    }

    /**
     * Prohibits next add requests.
     */
    public void prohibitAdding() {
        queueLock.lock();
        addingPohibited = true;
        notFull.signalAll();
        queueLock.unlock();
    }

    /**
     * Simple blocking add.
     *
     * @param element element for adding
     *
     * @throws ProhibitedQueueActionException when adding was prohibited
     * @throws InterruptedException when thread was interrupted
     */
    public void add(
        T element
    ) throws ProhibitedQueueActionException, InterruptedException {
        if (addingPohibited) {
            throw new ProhibitedQueueActionException();
        }
        try {
            queueLock.lock();
            while (!addingPohibited && list.size() == maxSize) {
                notFull.await();
            }
            if (addingPohibited) {
                throw new ProhibitedQueueActionException();
            }
            list.add(element);
            notEmpty.signal();
        } finally {
            queueLock.unlock();
        }
    }

    /**
     * Timeout blocking adding.
     *
     * @param element element for adding
     * @param timeoutMs time in ms which thread will wait for
     *     available place in queue
     *
     * @throws TimeoutException when time's up
     * @throws ProhibitedQueueActionException when adding was prohibited
     * @throws InterruptedException when thread was interrupted
     */
    public void add(
        T element, long timeoutMs
    ) throws TimeoutException, ProhibitedQueueActionException, InterruptedException {
        if (addingPohibited) {
            throw new ProhibitedQueueActionException();
        }
        try {
            queueLock.lock();
            while (list.size() == maxSize) {
                if (!notFull.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException("Timeout while adding");
                }
            }
            if (addingPohibited) {
                throw new ProhibitedQueueActionException();
            }
            list.add(element);
            notEmpty.signal();
        } finally {
            queueLock.unlock();
        }
    }
}
