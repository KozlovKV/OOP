package kozlov.kirill.sockets;

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
}
