package kozlov.kirill.pizzeria;

import java.util.*;
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

    public OwnBlockingQueue(ArrayList<T> data) {
        this.maxSize = data.size();
        list.addAll(data); // Лучше добавить полноценное копирование
    }

    int maxSize() {
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

    Optional<T> poll() throws InterruptedException {
        T result = null;
        try {
            queueLock.lock();
            while (list.isEmpty())
                notEmpty.await();
            result = list.poll();
            notFull.signal();
        } finally {
            queueLock.unlock();
        }
        if (result == null)
            return Optional.empty();
        return Optional.of(result);
    }

    void add(T element) throws InterruptedException {
        try {
            queueLock.lock();
            while (list.size() == maxSize)
                notFull.await();
            list.add(element);
            notEmpty.signal();
        } finally {
            queueLock.unlock();
        }
    }
}
