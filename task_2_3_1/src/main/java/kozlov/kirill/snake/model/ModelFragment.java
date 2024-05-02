package kozlov.kirill.snake.model;

public interface ModelFragment {
    <T extends ModelFragment> T restartModel();
}
