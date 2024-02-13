package kozlov.kirill.primes;

/**
 * Unprime checker's implementation with Stream API.
 */
public class SimpleUnprimeChecker extends UnprimeChecker {
    @Override
    public boolean isAnyUnprime() {
        return numbers.stream().anyMatch(UnprimeChecker::isNumUnprime);
    }
}
