package kozlov.kirill.primes;

import java.util.ArrayList;
import java.util.List;

public class ParallelStreamsUnprimeChecker extends UnprimeChecker {
    @Override
    public boolean isAnyUnprime() {
        return numbers.parallelStream().anyMatch(this::isNumUnprime);
    }
}
