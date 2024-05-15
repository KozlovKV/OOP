package kozlov.kirill.snake.view;

import java.util.ArrayList;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import lombok.Getter;

/**
 * Game view utils class.
 */
@ExcludeClassFromJacocoGeneratedReport
public class GameView {
    /**
     * Fields' colors enum class.
     */
    public enum Color {
        FIELD("#7CFF7F"),
        SNAKE("#72ADFF"),
        SNAKE_HEAD("#284CFF"),
        APPLE("#FF5B4C"),
        ENEMY("#FFAC63"),
        ENEMY_HEAD("#FF8A23");

        @Getter
        private final String hex;

        Color(String hex) {
            this.hex = hex;
        }
    }

    private final GridPane fieldGrid;
    private ArrayList<ArrayList<Rectangle>> fieldRects;
    private final int rowsCnt;
    private final int colsCnt;

    /**
     * Constructor.
     * <br>
     * Initialized field of specified sizes using
     * `GridPane` as an FXML container for `Rectangle` instances
     *
     * @param fieldGrid link to GridPane
     * @param rowsCnt rows count
     * @param colsCnt cols count
     */
    public GameView(GridPane fieldGrid, int rowsCnt, int colsCnt) {
        this.fieldGrid = fieldGrid;
        this.rowsCnt = rowsCnt;
        this.colsCnt = colsCnt;
        constructField();
    }

    private static Rectangle createCell() {
        Rectangle rectangle = new Rectangle(
            30, 30, Paint.valueOf(Color.FIELD.hex)
        );
        rectangle.getStyleClass().clear();
        rectangle.getStyleClass().add("cell");
        return rectangle;
    }

    private void constructField() {
        fieldRects = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < rowsCnt; rowIndex++) {
            ArrayList<Rectangle> fieldRow = new ArrayList<>();
            for (int colIndex = 0; colIndex < colsCnt; colIndex++) {
                Rectangle rectangle = createCell();
                fieldRow.add(rectangle);
                fieldGrid.add(rectangle, colIndex, rowIndex);
            }
            fieldRects.add(fieldRow);
        }
    }

    /**
     * Color changer for specified cell.
     *
     * @param x aka column
     * @param y aka row
     * @param color color from enum
     */
    public void setCellColor(int x, int y, Color color) {
        fieldRects.get(y).get(x).fillProperty().set(Paint.valueOf(color.hex));
    }

    /**
     * All field coloring.
     *
     * @param color color for filling
     */
    public void fillAllCells(Color color) {
        for (var row : fieldRects) {
            for (var cell : row) {
                cell.fillProperty().set(Paint.valueOf(color.hex));
            }
        }
    }
}
