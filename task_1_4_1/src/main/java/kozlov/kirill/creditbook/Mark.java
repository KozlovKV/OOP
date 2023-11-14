package kozlov.kirill.creditbook;

import java.util.Objects;

/**
 * Mark class.
 */
public final class Mark {
    public static final int LOWEST_VALUE = 2;
    public static final int HIGHEST_VALUE = 5;
    private final int value;
    private final int semester;
    private final Subject subject;


    public Mark(int value, int semester, Subject subject) {
        if (value > HIGHEST_VALUE) {
            value = HIGHEST_VALUE;
        } else {
            value = Math.max(value, LOWEST_VALUE);
        }
        this.value = value;
        this.semester = semester;
        this.subject = subject;
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public String toString() {
        return subject.getSubjectName() + " in semester "
                + semester + ": " + value;
    }

    /**
     * Marks override comparator.
     * Mark's value is ignored because we cannot have to Marks with the same semester and subject
     * and different mark's values
     *
     * @param o the reference object with which to compare
     * @return true weather objects have equal semesters and subjects
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mark mark = (Mark) o;
        return semester == mark.semester
                && subject.equals(mark.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(semester, subject);
    }

    public int getValue() {
        return value;
    }

    public int getSemester() {
        return semester;
    }

    public Subject getSubject() {
        return subject;
    }

}
