package kozlov.kirill.finders;

import java.util.LinkedList;

/**
 * Main class entry point.
 */
public class Main {
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        StringFinder finder = new SimpleStringFinder("aa", "./data/16GB.txt");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long)1048575);
        predictedList.add((long)16 * 1024 * 1024 * 1024 - 1048577);
        System.out.println("Expected:");
        System.out.println(predictedList);
        System.out.println("Got:");
        System.out.println(finder.getTargetsFoundPositions());
    }
}