package kozlov.kirill.snake.model;

/**
 * Interface for classes which will be integrated to model enum.
 */
public interface ModelFragment {
    /**
     * Restart-getter.
     * <br>
     * This method must perform (if necessary) some additional logic
     * and return model fragment instance,
     * so it CAN be used for new working cycle with model fragment
     *
     * @param <T> model fragment's type
     *
     * @return implementor of ModelFragment interface
     */
    <T extends ModelFragment> T restartModel();
}
