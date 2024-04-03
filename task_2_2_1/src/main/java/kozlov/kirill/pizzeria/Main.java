package kozlov.kirill.pizzeria;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(10);
        System.out.println("My blocking queue:");
        myQueue.add(0);
        for (int i = 0; i < 100; ++i) {
            new Thread(() -> {
                myQueue.poll().ifPresentOrElse(
                    num -> {
                        System.out.println(num);
                        myQueue.add(num + 1);
                    },
                    () -> System.out.println("Empty")
                );
            }).start();
        }
    }
}