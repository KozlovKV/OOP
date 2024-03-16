package kozlov.kirill.sockets.data;

import java.util.ArrayList;

public record TaskData(ArrayList<Integer> numbers) implements NetworkSendable {
    public ArrayList<TaskData> splitTaskData(int count) {
        final int numbersSize = numbers.size();
        final int subtaskNumbersSize = numbersSize / count + 1;
        ArrayList<TaskData> tasks = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            final int threadNumbersStartIndex = i * subtaskNumbersSize;
            final int threadNumbersEndIndex =
                    Math.min(threadNumbersStartIndex + subtaskNumbersSize, numbersSize);
            ArrayList<Integer> sublist = new ArrayList<>();
            for (int j = threadNumbersStartIndex; j < threadNumbersEndIndex; ++j)
                sublist.add(numbers().get(j));
            tasks.add(new TaskData(sublist));
        }
        return tasks;
    }
}
