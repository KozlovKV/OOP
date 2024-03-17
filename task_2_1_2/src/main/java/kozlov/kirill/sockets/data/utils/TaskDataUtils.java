package kozlov.kirill.sockets.data.utils;

import java.util.ArrayList;
import kozlov.kirill.sockets.data.TaskData;

final public class TaskDataUtils {
    private TaskDataUtils() {}

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
