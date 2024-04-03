package kozlov.kirill.pizzeria;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.IntBinaryOperator;

public class QueueTest {
    @Test
    void pipeIncrementing() {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(10);
        AtomicIntegerArray array = new AtomicIntegerArray(100);
        myQueue.add(0);
        for (int i = 0; i < 100; ++i) {
            new Thread(() ->
                myQueue.poll().ifPresent(
                    num -> {
                        myQueue.add(num + 1);
                        array.accumulateAndGet(num, 1, Integer::sum);
                    }
                )
            ).start();
        }
        for (int i = 0; i < 100; i++) {
            System.out.println(i + ": " + array.get(i));
            Assertions.assertEquals(1, array.get(i));
        }
    }
}
