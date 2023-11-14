package kozlov.kirill.creditBook;

/**
 * Learning subject class.
 */
public enum Subject {
    FIZRA("Physical culture"),
    IMPERATIVE("Imperative programming"),
    DECLARATIVE("Declarative programming"),
    DIFF("Differential equations"),
    PAK("Team project"),
    AI("Introduction to an artificial intelligence"),
    OOP("Object oriented programming"),
    OS("Operating systems"),
    MODELS("Computations models"),
    DIPLOMA("Final qualifying work");

    private final String subjectName;

    private Subject(String subj) {
        subjectName = subj;
    }

    /**
     * Getter for enum's human name.
     *
     * @return value from brackets after enum's field
     */
    public String getSubjectName() {
        return subjectName;
    }
}
