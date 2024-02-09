package kozlov.kirill.primes;

import java.util.ArrayList;
import java.util.List;

public class SimpleUnprimeChecker extends UnprimeChecker {
    @Override
    public boolean isAnyUnprime() {
        return numbers.stream().anyMatch(this::isNumUnprime);
    }
}
