package kozlov.kirill.primes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

abstract public class UnprimeChecker {
    ArrayList<Integer> numbers = new ArrayList<>();

    public UnprimeChecker setNumbers(ArrayList<Integer> numbers) {
        this.numbers = numbers;
        return this;
    }

    protected boolean isNumUnprime(Integer num) {
        for (int i = 2; i * i <= num; ++i)
            if (num % i == 0)
                return true;
        return false;
    }

    abstract public boolean isAnyUnprime();
}
