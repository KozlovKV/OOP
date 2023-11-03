package kozlov.kirill.finders;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        StringFinder finder = new SimpleStringFinder("бра", "./input.txt");
        finder.find();
        System.out.println(finder.getTargetsFoundPositions());
    }
}