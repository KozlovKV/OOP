package kozlov.kirill.pizzeria.queue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class OwnBlockingQueue<T> {
    private final int maxSize;
    private final LinkedList<T> list = new LinkedList<>();

    private final ReentrantLock queueLock = new ReentrantLock();
    private final Condition notEmpty = queueLock.newCondition();
    private final Condition notFull = queueLock.newCondition();

    public OwnBlockingQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public OwnBlockingQueue(ArrayList<T> data) {
        this.maxSize = data.size();
        list.addAll(data); // Лучше добавить полноценное копирование
    }

    public int maxSize() {
        return maxSize;
    }

    public int size() {
        int size;
        queueLock.lock();
        size = list.size();
        queueLock.unlock();
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public ArrayList<T> getListCopy() {
        queueLock.lock();
        ArrayList<T> copyList = new ArrayList<>(list);
        queueLock.unlock();
        return copyList;
    }

    public Optional<T> poll() throws InterruptedException {
        T result = null;
        try {
            queueLock.lock();
            while (list.isEmpty()) {
                notEmpty.await();
            }
            result = list.poll();
            notFull.signal();
        } finally {
            queueLock.unlock();
        }
        if (result == null)
            return Optional.empty();
        return Optional.of(result);
    }

    public Optional<T> poll(
        long timeoutMs
    ) throws TimeoutException, InterruptedException {
        T result = null;
        try {
            queueLock.lock();
            while (list.isEmpty()) {
                if (!notEmpty.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException("Timeout while polling");
                }
            }
            result = list.poll();
            notFull.signal();
        } finally {
            queueLock.unlock();
        }
        if (result == null)
            return Optional.empty();
        return Optional.of(result);
    }

    public void add(T element) throws InterruptedException {
        try {
            queueLock.lock();
            while (list.size() == maxSize) {
                notFull.await();
            }
            list.add(element);
            notEmpty.signal();
        } finally {
            queueLock.unlock();
        }
    }

    public void add(
        T element, long timeoutMs
    ) throws TimeoutException, InterruptedException {
        try {
            queueLock.lock();
            while (list.size() == maxSize) {
                if (!notFull.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                    throw new TimeoutException("Timeout while adding");
                }
            }
            list.add(element);
            notEmpty.signal();
        } finally {
            queueLock.unlock();
        }
    }
}
