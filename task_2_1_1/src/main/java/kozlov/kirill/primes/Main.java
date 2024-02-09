package kozlov.kirill.primes;

import java.util.ArrayList;

/**
 * Main class.
 */
public class Main {
    /**
     * Entry point function.
     *
     * @param args cmds' args
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(100000007);
        }
        list.add(8);
        System.out.println(new SimpleUnprimeChecker().setNumbers(list).isAnyUnprime());
        System.out.println(new ParallelStreamsUnprimeChecker().setNumbers(list).isAnyUnprime());
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