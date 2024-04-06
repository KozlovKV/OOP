package kozlov.kirill.pizzeria;

import kozlov.kirill.pizzeria.queue.OwnBlockingQueue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class QueueTest {
    @Test
    void pipeIncrementing() {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(10);
        AtomicIntegerArray array = new AtomicIntegerArray(100);
        try {
            myQueue.add(0);
        } catch (InterruptedException interruptedException) {
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
                } catch (InterruptedException interruptedException) {
                    Assertions.fail();
                }
            }).start();
        }
        for (int i = 0; i < 100; i++) {
            System.out.println(i + ": " + array.get(i));
            Assertions.assertEquals(1, array.get(i));
        }
    }
}
