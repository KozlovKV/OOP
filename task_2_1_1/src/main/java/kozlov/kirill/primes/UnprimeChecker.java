package kozlov.kirill.primes;

import java.util.ArrayList;
import java.util.function.Predicate;

public interface UnprimeChecker {
    default boolean isNumUnprime(Integer num) {
        for (int i = 2; i * i <= num; ++i)
            if (num % i == 0)
                return true;
        return false;
    }

    boolean isAnyUnprime();
}
