module kozlov.kirill.snake {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.logging.log4j.core;
    requires static lombok;
    requires com.fasterxml.jackson.databind;

    exports kozlov.kirill.snake;
    opens kozlov.kirill.snake to javafx.fxml;
    exports kozlov.kirill.snake.view;
    opens kozlov.kirill.snake.view to javafx.fxml;
    exports kozlov.kirill.snake.view_model;
    opens kozlov.kirill.snake.view_model to javafx.fxml;

    exports kozlov.kirill.util to com.fasterxml.jackson.databind;
    exports kozlov.kirill.snake.model.settings to com.fasterxml.jackson.databind;
    exports kozlov.kirill.snake.view_model.settings;
    opens kozlov.kirill.snake.view_model.settings to javafx.fxml;
}