package kozlov.kirill.snake.viewmodel;

import kozlov.kirill.snake.ExcludeClassFromJacocoGeneratedReport;
import kozlov.kirill.snake.view.SceneManager;

/**
 * Interface for view-models linked with scene manager.
 */
@ExcludeClassFromJacocoGeneratedReport
public interface SceneManagerAccessible {
    /**
     * Start scene's view-model method.
     * <br>
     * Used in SceneManager to link view-model with it and maybe perform some additional actions
     *
     * @param manager scene manager link
     */
    void setSceneManager(SceneManager manager);
}
