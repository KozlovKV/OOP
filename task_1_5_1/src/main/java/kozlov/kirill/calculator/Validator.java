package kozlov.kirill.calculator;

/**
 * Validator lambda-interface for checking function's arguments.
 */
public interface Validator {
    /**
     * Validation method.
     *
     * @param values arguments of validated function
     * @return error message or null if all arguments are correct
     */
    String validate(double[] values);
}
