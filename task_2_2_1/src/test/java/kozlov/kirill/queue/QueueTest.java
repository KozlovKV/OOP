package kozlov.kirill.queue;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for OwnBlockingQueue class.
 */
public class QueueTest {
    @Test
    void pipeIncrementing() {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(1);
        AtomicIntegerArray array = new AtomicIntegerArray(100);
        try {
            myQueue.add(0);
        } catch (ProhibitedQueueActionException | InterruptedException e) {
            Assertions.fail();
        }
        for (int i = 0; i < 100; ++i) {
            new Thread(() -> {
                try {
                    var num = myQueue.poll();
                    Assertions.assertNotNull(num);
                    myQueue.add(num + 1);
                    array.accumulateAndGet(num, 1, Integer::sum);
                } catch (ProhibitedQueueActionException | InterruptedException e) {
                    Assertions.fail();
                }
            }).start();
        }
        for (int i = 0; i < 100; i++) {
            System.out.println(i + ": " + array.get(i));
            Assertions.assertEquals(1, array.get(i));
        }
    }

    @Test
    void prohibitedPollingTest() {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(10);
        try {
            myQueue.add(1);
            myQueue.add(2);
            myQueue.prohibitAdding();
            Assertions.assertEquals(1, myQueue.poll());
        } catch (ProhibitedQueueActionException | InterruptedException e) {
            Assertions.fail();
        }
        myQueue.prohibitPolling();
        Assertions.assertThrows(
            ProhibitedQueueActionException.class,
            myQueue::poll
        );
    }

    @Test
    void prohibitedAddingTest() {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(10);
        try {
            myQueue.add(1);
            myQueue.add(2);
        } catch (ProhibitedQueueActionException | InterruptedException e) {
            Assertions.fail();
        }
        myQueue.prohibitAdding();
        Assertions.assertThrows(
            ProhibitedQueueActionException.class, () -> myQueue.add(3)
        );
        try {
            Assertions.assertEquals(1, myQueue.poll());
            Assertions.assertEquals(2, myQueue.poll());
        } catch (ProhibitedQueueActionException | InterruptedException e) {
            Assertions.fail();
        }
    }

    @Test
    void timeoutPollingTest() {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(2);
        try {
            myQueue.add(1, 1000);
            myQueue.poll(1000);
        } catch (
            TimeoutException | ProhibitedQueueActionException | InterruptedException e
        ) {
            Assertions.fail();
        }
        Assertions.assertThrows(
            TimeoutException.class,
            () -> myQueue.poll(1000)
        );
    }

    @Test
    void timeoutAddingTest() {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(1);
        try {
            myQueue.add(1, 1000);
        } catch (TimeoutException | ProhibitedQueueActionException | InterruptedException e) {
            Assertions.fail();
        }
        Assertions.assertThrows(
            TimeoutException.class,
            () -> myQueue.add(2, 1000)
        );
    }
}
