package kozlov.kirill.pizzeria;

import kozlov.kirill.ExcludeClassFromJacocoGeneratedReport;

import java.io.FileNotFoundException;
import java.io.IOException;

@ExcludeClassFromJacocoGeneratedReport
public class Main {
    public static void main(String[] args) {
        OwnBlockingQueue<Integer> myQueue = new OwnBlockingQueue<>(10);
        System.out.println("My blocking queue:");
        try {
            myQueue.add(0);
        } catch (InterruptedException ignored) {

        }
        for (int i = 0; i < 100; ++i) {
            new Thread(() -> {
                try {
                    var optionalInteger = myQueue.poll();
                    if (optionalInteger.isPresent()) {
                        int num = optionalInteger.get();
                        myQueue.add(num + 1);
                        System.out.println(num);
                    } else {
                        System.out.println("Empty");
                    }
                } catch (InterruptedException ignored) {

                }
            }).start();
        }
        try {
            Pizzeria p = new Pizzeria(5, "setup.json");
        } catch (FileNotFoundException ignored) {}

    }
}