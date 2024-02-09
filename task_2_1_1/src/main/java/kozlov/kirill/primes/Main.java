package kozlov.kirill.primes;

import java.lang.reflect.Array;
import java.text.Collator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
//        for (int i = 0; i < 1000000; i++) {
//            list.add(100000007);
//        }
        list.add(2);
        list.add(3);
//        list.add(4);
        list.add(5);
        list.add(7);
        System.out.println(new ThreadUnprimeChecker(4).setNumbers(list).isAnyUnprime());
        System.out.println(new ThreadUnprimeChecker(8).setNumbers(list).isAnyUnprime());
        System.out.println(new ThreadUnprimeChecker(12).setNumbers(list).isAnyUnprime());
//        for (int i = 1; i <= 64; ++i) {
//            startTime = LocalDateTime.now();
//            new ThreadUnprimeChecker(i).isAnyUnprime();
//            System.out.println(i);
//            System.out.println(
//                    startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS)
//            );
//        }
    }
}

/*
Results:

10 million primes:
Simple: 149854
PS: 26566
Threads: 25254

100 million primes:
Simple: 1497101
PS: 261411
Threads: 255805
 */