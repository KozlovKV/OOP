package kozlov.kirill.finders;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public abstract class StringFinder {
    protected final int CAPACITY = 1048577; // 1 MB
    protected StringBuffer buffer = new StringBuffer();
    protected BufferedReader reader = null;
    protected String searchTarget = "";
    protected LinkedList<Long> targetsFoundPositions = new LinkedList<>();

    public StringFinder() {}

    public StringFinder(String filename) {
        openFile(filename);
    }

    public StringFinder(String target, String filename) {
        setSearchTarget(target);
        openFile(filename);
    }

    public void setSearchTarget(String searchTarget) {
        ByteBuffer utf8Buffer = StandardCharsets.UTF_8.encode(searchTarget);
        this.searchTarget = StandardCharsets.UTF_8.decode(utf8Buffer).toString();
    }

    public void openFile(String filename) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    fileInputStream, StandardCharsets.UTF_8
            );
            this.reader = new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int readFragment() {
        char[] charBuffer = new char[CAPACITY];
        int readCharsCount = 0;
        try {
            readCharsCount = reader.read(charBuffer);
            if (readCharsCount == -1) {
                return readCharsCount;
            }
        } catch (IOException e) {
            return -1;
        }
        if (!buffer.isEmpty()) {
            buffer.delete(0, buffer.length());
        }
        buffer.append(charBuffer, 0, readCharsCount);
        return readCharsCount;
    }

    abstract void find();

    public LinkedList<Long> getTargetsFoundPositions() {
        return targetsFoundPositions;
    }
}
