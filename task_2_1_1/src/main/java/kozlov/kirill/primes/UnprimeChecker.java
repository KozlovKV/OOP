package kozlov.kirill.primes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Abstract class of unprime numbers checker class.
 */
public abstract class UnprimeChecker {
    protected ArrayList<Integer> numbers = new ArrayList<>();

    /**
     * Numbers' list setter.
     *
     * @param numbers numbers list
     * @return link to class's instance for convenient chain usage
     */
    public UnprimeChecker setNumbers(ArrayList<Integer> numbers) {
        this.numbers = numbers;
        return this;
    }

    /**
     * Predicate for unprime number.
     *
     * @param number number for checking
     * @return true weather num isn't prime
     */
    protected static boolean isNumUnprime(Integer number) {
        int num = Math.abs(number);
        for (int i = 2; i * i <= num; ++i) {
            if (num % i == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Abstract method for checker's implementation.
     *
     * @return true weather numbers' list has at least one unprime number
     */
    public abstract boolean isAnyUnprime();

    public static final int BILLION_PRIME = 1000000007;

    /**
     * Entry point function.
     *
     * @param args cmds' args
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(BILLION_PRIME);
        }
        ArrayList<Integer> list2 = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            list2.add(BILLION_PRIME);
        }
        LocalDateTime startTime;
        UnprimeChecker[] checkers = new UnprimeChecker[]{
                new SimpleUnprimeChecker(),
                new ParallelStreamsUnprimeChecker(),
                new ThreadUnprimeChecker(12)
        };
        try {
            OutputStream out = new FileOutputStream("types.csv");
            OutputStreamWriter writer = new OutputStreamWriter(out);
            for (var checker : checkers) {
                startTime = LocalDateTime.now();
                checker.setNumbers(list).isAnyUnprime();
                long t1 = startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);
                startTime = LocalDateTime.now();
                checker.setNumbers(list2).isAnyUnprime();
                long t2 = startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);
                String str = String.format(
                        "%d; %d\n", t1, t2
                );
                writer.write(str);
                writer.flush();
                System.out.print(str);
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
