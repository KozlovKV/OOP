package kozlov.kirill.snake.viewmodel.settings;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;

/**
 * Extended event handler class.
 */
@ExcludeClassFromJacocoGeneratedReport
public class TypeEventProcessor implements EventHandler<KeyEvent> {
    private final TextInputControl fxmlField;
    private final Validator validator;
    private final Label errorLabel;
    private final String errorMessage;
    private String previousValue;

    /**
     * Constructor.
     *
     * @param fxmlField FXML input field for validation
     * @param initialValue initial field value
     * @param validator implementation of Validator interface
     * @param errorLabel label where will be showed error message
     * @param errorMessage message for showing in case of validation failed
     */
    public TypeEventProcessor(
        TextInputControl fxmlField, String initialValue,
        Validator validator,
        Label errorLabel, String errorMessage
    ) {
        this.fxmlField = fxmlField;
        fxmlField.setText(initialValue);
        previousValue = initialValue;
        this.validator = validator;
        this.errorLabel = errorLabel;
        this.errorMessage = errorMessage;
    }

    @Override
    public void handle(KeyEvent event) {
        if (validator.isValid(fxmlField, event)) {
            previousValue = fxmlField.getText();
            errorLabel.setText("");
            return;
        }
        System.err.println("Error while validating");
        errorLabel.setText(errorMessage);
        fxmlField.setText(previousValue);
    }
}
