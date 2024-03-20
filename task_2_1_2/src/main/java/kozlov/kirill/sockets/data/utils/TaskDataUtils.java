package kozlov.kirill.sockets.data.utils;

import java.util.ArrayList;
import kozlov.kirill.sockets.data.TaskData;

/**
 * Static class with utils for working with TaskData's instances.
 */
public final class TaskDataUtils {
    private TaskDataUtils() {}

    /**
     * TaskData splitter.
     * <br>
     * Splits list from input TaskData.
     * <br>
     * When list size is divided by count without rest function returns exactly
     * `count` TaskDatas with `input size / count` numbers in each one.
     * <br>
     * When list size is divided by count has some rest function places
     * `input size / count + 1` in each one Taskdata excluding
     * last one - it can be smaller (or even empty)
     *
     *
     * @param taskData input task data which will be splitted
     * @param count desired count of elements in result list
     * @return list of TaskData instances with length from count - 1 to count
     */
    public static ArrayList<TaskData> splitTaskData(TaskData taskData, int count) {
        if (count <= 0)
            return new ArrayList<>();
        final int numbersSize = taskData.numbers().size();
        int subtaskNumbersSize = numbersSize / count;
        if (numbersSize % count != 0) {
            subtaskNumbersSize++;
        }
        ArrayList<TaskData> tasks = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            final int threadNumbersStartIndex = i * subtaskNumbersSize;
            final int threadNumbersEndIndex =
                    Math.min(threadNumbersStartIndex + subtaskNumbersSize, numbersSize);
            if (threadNumbersEndIndex <= threadNumbersStartIndex) {
                break;
            }
            ArrayList<Integer> sublist = new ArrayList<>();
            for (int j = threadNumbersStartIndex; j < threadNumbersEndIndex; ++j)
                sublist.add(taskData.numbers().get(j));
            tasks.add(new TaskData(sublist));
        }
        return tasks;
    }
}
