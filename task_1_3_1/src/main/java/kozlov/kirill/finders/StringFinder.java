package kozlov.kirill.finders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
     */
    public StringFinder() {}

    /**
     * Search string setter.
     *
     * @param searchTarget string for searching
     * @throws UnsupportedOperationException throws exception weather search target is empty
     */
    private void setSearchTarget(String searchTarget) throws UnsupportedOperationException {
        if (searchTarget.isEmpty()) {
            throw new UnsupportedOperationException("Empty string isn't allowed");
        }
        ByteBuffer utf8Buffer = StandardCharsets.UTF_8.encode(searchTarget);
        this.searchTarget = StandardCharsets.UTF_8.decode(utf8Buffer).toString();
    }

    /**
     * Opener file for reading.
     * Opens file from specified path in resources.
     * After that saves BufferedReader with encoding UTF-8 for it
     *
     * @param filename path to file for finding
     */
    private boolean openFileFromResources(String filename) {
        try {
            InputStream resourceInputStream =
                    getClass().getClassLoader().getResourceAsStream(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    resourceInputStream, StandardCharsets.UTF_8
            );
            this.reader = new BufferedReader(inputStreamReader, capacity);
            return true;
        } catch (Exception e) {
            System.err.println("File from resources wasn't opened\n" + e);
            return false;
        }
    }


    /**
     * Opener file for reading.
     * Opens file from specified path.
     * After that saves BufferedReader with encoding UTF-8 for it
     *
     * @param filename path to file for finding
     * @return true weather file was opened successfully
     */
    private boolean openFile(String filename) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream, StandardCharsets.UTF_8
            );
            this.reader = new BufferedReader(inputStreamReader, capacity);
            return true;
        } catch (Exception e) {
            System.err.println("File wasn't opened\n" + e);
            return false;
        }
    }

    /**
     * File reader closer.
     */
    private void closeFile() {
        try {
            reader.close();
            reader = null;
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
    protected int readFragment() {
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
     * in file by reader.
     * If file wasn't opened result's list targetsFoundPositions will be empty.
     */
    public void find(String filename, String target, boolean isResource) {
        setSearchTarget(target);
        targetsFoundPositions.clear();
        boolean isOpened;
        if (isResource) {
            isOpened = openFileFromResources(filename);
        } else {
            isOpened = openFile(filename);
        }
        if (!isOpened) {
            return;
        }
        findingCycle();
        closeFile();
    }

    protected abstract void findingCycle();

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
     * @param args args from shell
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        StringFinder finder = new SimpleStringFinder();
        // WE ASSUME THAT FILE ALREADY EXIST
        finder.find("16GB.txt", "aa", false);
        LinkedList<Long> predictedList = new LinkedList<>();
        predictedList.add((long) 1048575);
        predictedList.add((long) 16 * 1024 * 1024 * 1024 - 1048577);
        System.out.println("Expected:");
        System.out.println(predictedList);
        System.out.println("Got:");
        System.out.println(finder.getTargetsFoundPositions());
    }
}
