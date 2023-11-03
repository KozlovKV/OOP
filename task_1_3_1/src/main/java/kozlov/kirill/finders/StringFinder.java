package kozlov.kirill.finders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/**
 * Abstract string finder class.
 */
public abstract class StringFinder {
    protected final int capacity = 1048576; // 1 MB
    protected StringBuffer buffer = new StringBuffer();
    protected BufferedReader reader = null;
    protected String searchTarget = "";
    protected LinkedList<Long> targetsFoundPositions = new LinkedList<>();

    /**
     * Basic constructor.
     * Before using object call setSearchTarget() and openFile()
     */
    public StringFinder() {}

    /**
     * Constructor with specified file.
     * Before using object call setSearchTarget()
     *
     * @param filename path to file for finding
     */
    public StringFinder(String filename) {
        openFile(filename);
    }

    /**
     * Constructor with specified file and search target.
     *
     * @param filename path to file for finding
     * @param target string for searching
     */
    public StringFinder(String filename, String target) {
        setSearchTarget(target);
        openFile(filename);
    }

    /**
     * Search string setter.
     *
     * @param searchTarget string for searching
     * @throws UnsupportedOperationException throws exception weather search target is empty
     */
    public void setSearchTarget(String searchTarget) throws UnsupportedOperationException {
        if (searchTarget.isEmpty()) {
            throw new UnsupportedOperationException("Empty string isn't allowed");
        }
        ByteBuffer utf8Buffer = StandardCharsets.UTF_8.encode(searchTarget);
        this.searchTarget = StandardCharsets.UTF_8.decode(utf8Buffer).toString();
    }

    /**
     * Opener file for reading.
     * Opens file by specified path and save BufferedReader with encoding
     * UTF-8 for it
     *
     * @param filename path to file for finding
     */
    public void openFile(String filename) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, StandardCharsets.UTF_8
            );
            this.reader = new BufferedReader(inputStreamReader, capacity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fragment reader.
     * Read chars from reader up to CAPACITY field's value
     *
     * @return read chars count or -1 when nothing was read
     */
    int readFragment() {
        char[] charBuffer = new char[capacity];
        int readCharsCount = 0;
        try {
            readCharsCount = reader.read(charBuffer);
            if (readCharsCount == -1) {
                return readCharsCount;
            }
        } catch (IOException e) {
            return -1;
        }
        if (buffer.length() > 0) {
            buffer.delete(0, buffer.length());
        }
        buffer.append(charBuffer, 0, readCharsCount);
        return readCharsCount;
    }

    /**
     * Main finding function.
     * As a result we suppose to get all start indexes of insertions of searchTarget
     * if file by reader
     */
    abstract void find();

    /**
     * targetsFoundPositions getter.
     *
     * @return LinkedList with start indexes of insertions of searchTarget
     * which got after the last call of find()
     */
    public LinkedList<Long> getTargetsFoundPositions() {
        return targetsFoundPositions;
    }

    /**
     * Program entry point.
     *
     * @param args cmds' args
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        StringFinder finder = new SimpleStringFinder("./data/16GB.txt", "aa");
        finder.find();
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1048575);
        predictedList.add((long) 16 * 1024 * 1024 * 1024 - 1048577);
        System.out.println("Expected:");
        System.out.println(predictedList);
        System.out.println("Got:");
        System.out.println(finder.getTargetsFoundPositions());
    }
}
