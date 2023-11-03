package kozlov.kirill.finders;

/**
 * StringFinder child class with simple finding method.
 */
public class SimpleStringFinder extends StringFinder {
    private String previousFragmentPostfix = "";

    public SimpleStringFinder() {
        super();
    }

    public SimpleStringFinder(String filename) {
        super(filename);
    }

    public SimpleStringFinder(String filename, String target) {
        super(filename, target);
    }

    @Override
    void find() {
        long filePositions = 0;
        int bufferIndex;
        targetsFoundPositions.clear();
        while (readFragment() != -1) {
            buffer.insert(0, previousFragmentPostfix);
            if (buffer.length() < searchTarget.length()) {
                break;
            }
            bufferIndex = buffer.indexOf(searchTarget);
            while (bufferIndex != -1) {
                targetsFoundPositions.add(filePositions + (long) bufferIndex);
                if (bufferIndex + 1 < buffer.length()) {
                    bufferIndex = buffer.indexOf(searchTarget, bufferIndex + 1);
                }
            }
            previousFragmentPostfix = buffer.substring(
                buffer.length() - searchTarget.length() + 1, buffer.length()
            );
            filePositions += buffer.length() - previousFragmentPostfix.length();
        }
    }
}
