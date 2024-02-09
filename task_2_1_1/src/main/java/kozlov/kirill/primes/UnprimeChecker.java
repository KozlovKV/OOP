package kozlov.kirill.primes;

import java.util.ArrayList;

/**
 * Unprime numbers checker class's abstract class
 */
abstract public class UnprimeChecker {
    ArrayList<Integer> numbers = new ArrayList<>();

    /**
     * Numbers' list setter
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
     * @param num number for checking
     * @return true weather num isn't prime
     */
    protected boolean isNumUnprime(Integer num) {
        for (int i = 2; i * i <= num; ++i)
            if (num % i == 0)
                return true;
        return false;
    }

    /**
     * Abstract method for checker's implementation.
     *
     * @return true weather numbers' list has at least one unprime number
     */
    abstract public boolean isAnyUnprime();
}
