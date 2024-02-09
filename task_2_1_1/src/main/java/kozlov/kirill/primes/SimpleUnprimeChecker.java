package kozlov.kirill.primes;

import java.util.ArrayList;

public class SimpleUnprimeChecker implements UnprimeChecker {
    ArrayList<Integer> numbers;

    public SimpleUnprimeChecker(ArrayList<Integer> numbers) {
        this.numbers = new ArrayList<>(numbers);
    }

    @Override
    public boolean isAnyUnprime() {
        return numbers.stream().anyMatch(this::isNumUnprime);
    }
}
