package kozlov.kirill.primes;

import java.util.ArrayList;

public class ParallelStreamsUnprimeChecker implements UnprimeChecker {
    ArrayList<Integer> numbers;

    public ParallelStreamsUnprimeChecker(ArrayList<Integer> numbers) {
        this.numbers = new ArrayList<>(numbers);
    }

    @Override
    public boolean isAnyUnprime() {
        return numbers.parallelStream().anyMatch(this::isNumUnprime);
    }
}
