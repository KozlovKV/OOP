package kozlov.kirill.snake.viewmodel.settings;

import java.util.Optional;

public final class ImplementedValidators {
    private ImplementedValidators() {}

    public static final Validator<Integer> positiveValidator
        = (field, event) -> {
            try {
                int num = Integer.parseInt(field.getText());
                if (num <= 0) {
                    return Optional.empty();
                }
                return Optional.of(num);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        };

    public static final Validator<Integer> nonNegativeValidator
        = (field, event) -> {
            try {
                int num = Integer.parseInt(field.getText());
                if (num < 0) {
                    return Optional.empty();
                }
                return Optional.of(num);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        };
}
