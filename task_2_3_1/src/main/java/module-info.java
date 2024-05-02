module kozlov.kirill.snake {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.apache.logging.log4j.core;
    requires static lombok;
    requires com.fasterxml.jackson.databind;

    exports kozlov.kirill.snake;
    opens kozlov.kirill.snake to javafx.fxml;
    exports kozlov.kirill.snake.view;
    opens kozlov.kirill.snake.view to javafx.fxml;
    exports kozlov.kirill.snake.model_view;
    opens kozlov.kirill.snake.model_view to javafx.fxml;

    exports kozlov.kirill.util to com.fasterxml.jackson.databind;
    exports kozlov.kirill.snake.model.settings to com.fasterxml.jackson.databind;
}