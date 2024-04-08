package kozlov.kirill.queue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicIntegerArray;

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
                    var optionalInteger = myQueue.poll();
                    if (optionalInteger.isPresent()) {
                        int num = optionalInteger.get();
                        myQueue.add(num + 1);
                        array.accumulateAndGet(num, 1, Integer::sum);
                    } else {
                        Assertions.fail();
                    }
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
            var optionalInteger = myQueue.poll();
            if (optionalInteger.isPresent()) {
                Assertions.assertEquals(1, optionalInteger.get());
            } else {
                Assertions.fail();
            }
        } catch (ProhibitedQueueActionException | InterruptedException e) {
            Assertions.fail();
        }
        myQueue.prohibitPolling();
        try {
            myQueue.poll();
        } catch (ProhibitedQueueActionException prohibited) {
            return;
        } catch (InterruptedException e) {
            Assertions.fail();
        }
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
        try {
            myQueue.add(3);
        } catch (ProhibitedQueueActionException prohibited) {
        } catch (InterruptedException e) {
            Assertions.fail();
        }
        try {
            var optionalInteger = myQueue.poll();
            if (optionalInteger.isPresent()) {
                Assertions.assertEquals(1, optionalInteger.get());
            } else {
                Assertions.fail();
            }
            optionalInteger = myQueue.poll();
            if (optionalInteger.isPresent()) {
                Assertions.assertEquals(2, optionalInteger.get());
            } else {
                Assertions.fail();
            }
        } catch (ProhibitedQueueActionException | InterruptedException e) {
            Assertions.fail();
        }
    }
}
