package kozlov.kirill.creditbook;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Student class.
 */
public class Student {
    private final String surname;
    private final String name;
    static final int SEMESTER_NUMBER_BASE = 1;
    private int semesterNumber = SEMESTER_NUMBER_BASE;
    private final CreditBook creditBook;

    /**
     * Student constructor.
     * Binds credit book for new student
     *
     * @param surname student's surname
     * @param name student's name
     */
    public Student(String surname, String name) {
        this.surname = surname;
        this.name = name;
        this.creditBook = new CreditBook(this);
    }

    /**
     * Credit book getter.
     *
     * @return CreditBook instance
     */
    public CreditBook getCreditBook() {
        return creditBook;
    }

    /**
     * Semester number getter.
     *
     * @return semesterNumber value
     */
    public int getSemesterNumber() {
        return semesterNumber;
    }

    /**
     * Semester finisher.
     *
     * @return new semester number
     */
    public int finishSemester() {
        return ++semesterNumber;
    }

    /**
     * CreditBooks override comparator.
     *
     * @param o other object for comparing
     * @return true weather Students instances has the same name, surname
     * and credit books (their ids)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Student student = (Student) o;
        return Objects.equals(name, student.name)
                && Objects.equals(surname, student.surname)
                && Objects.equals(creditBook, student.creditBook);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, creditBook);
    }

    @ExcludeFromJacocoGeneratedReport
    @Override
    public String toString() {
        return "Student " + surname + " " + name
                + " on semester " + semesterNumber;
    }

    /**
     * Credit book class.
     * The reason for inner class is the idea that we cannot create credit book without student
     */
    public static class CreditBook {
        private static int freeId = 0;
        private static final double MIN_AVG_FOR_RED_DIPLOMA = 4.75 - 0.000000001;
        private final int id;
        private final Student student;
        private final Set<Mark> marks = new HashSet<>();

        /**
         * FreeId incrementing getter.
         * This method allows create unique credit books
         *
         * @return new value for CreditBook id
         */
        private static int getFreeId() {
            return freeId++;
        }

        /**
         * CreditBook constructor.
         * Can be called only from Student class
         *
         * @param student Student class instance
         */
        private CreditBook(Student student) {
            this.id = CreditBook.getFreeId();
            this.student = student;
        }

        /**
         * New mark adder.
         * We cannot add new Mark weather we have already added Mark
         * with the same semester and subject
         *
         * @param value value of mark
         * @param subject learning subject
         * @return Mark object weather new mark has been added and null otherwise
         */
        public Mark addMark(int value, Subject subject) {
            Mark mark = new Mark(value, student.getSemesterNumber(), subject);
            if (marks.add(mark)) {
                return mark;
            }
            return null;
        }

        public Set<Mark> getMarks() {
            return marks;
        }

        /**
         * Average mark estimator.
         *
         * @return average value for all marks.
         */
        public double averageMark() {
            if (marks.isEmpty()) {
                return 0;
            }
            return ((double) marks.stream().mapToInt(Mark::getValue).sum()) / marks.size();
        }

        /**
         * Predicate for obtaining increased scholarship.
         *
         * @return true weather student has only 5 marks in previous semester
         *     (so it's always false for the first semester)
         */
        public boolean canObtainIncreasedScholarship() {
            int semesterNumber = student.getSemesterNumber();
            if (semesterNumber == Student.SEMESTER_NUMBER_BASE) {
                return false;
            }
            return marks.stream().filter(
                    mark -> mark.getSemester() == semesterNumber - 1
            ).allMatch(mark -> mark.getValue() == Mark.HIGHEST_VALUE);
        }

        private Set<Mark> getLastSubjectMarks() {
            HashMap<Subject, Mark> lastMarks = new HashMap<>();
            for (var mark : marks) {
                Mark prevMark = lastMarks.putIfAbsent(mark.getSubject(), mark);
                if (prevMark != null && prevMark.getSemester() < mark.getSemester()
                        && mark.getSubject().equals(prevMark.getSubject())) {
                    lastMarks.replace(mark.getSubject(), prevMark, mark);
                }
            }
            return new HashSet<>(lastMarks.values());
        }

        /**
         * Predicate for obtaining red diploma.
         *
         * @return true weather student can obtain red diploma so:<ol>
         * <li>Last marks greater or equal to 4
         * <li>Average value of last marks greater or equal to 4.75
         * <li>Diploma has mark 5
         * </ol>
         */
        public boolean canObtainRedDiploma() {
            var lastMarks = getLastSubjectMarks();
            if (lastMarks.isEmpty()) {
                return false;
            }
            boolean containsDiploma = false;
            double avg = 0;
            for (var mark : lastMarks) {
                if (mark.getValue() <= 3) {
                    return false;
                }
                if (mark.getSubject().equals(Subject.DIPLOMA)) {
                    if (mark.getValue() < Mark.HIGHEST_VALUE) {
                        return false;
                    }
                    containsDiploma = true;
                }
                avg += mark.getValue();
            }
            avg /= lastMarks.size();
            return containsDiploma && avg >= MIN_AVG_FOR_RED_DIPLOMA;
        }

        /**
         * CreditBooks override comparator.
         *
         * @param o other object for comparing
         * @return true weather objects of class CreditBook has the same id
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CreditBook that = (CreditBook) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @ExcludeFromJacocoGeneratedReport
        @Override
        public String toString() {
            return "Credit book " + id +
                    "; student: " + student.toString();
        }
    }

    /**
     * Program entry point.
     *
     * @param args cmds' args
     */
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        Student biba = new Student("Biba", "Test");
        biba.getCreditBook().addMark(13, Subject.DIFF);
        biba.getCreditBook().addMark(3, Subject.DIPLOMA);
        System.out.println(biba.getCreditBook());
        System.out.println(biba.getCreditBook().averageMark());
        System.out.println(biba.getCreditBook().canObtainIncreasedScholarship());
        biba.finishSemester();
        biba.getCreditBook().addMark(5, Subject.DIPLOMA);
        biba.getCreditBook().addMark(3, Subject.DIFF);
        System.out.println(biba.getCreditBook());
        System.out.println(biba.getCreditBook().averageMark());
        System.out.println(biba.getCreditBook().canObtainIncreasedScholarship());
        biba.finishSemester();
        biba.getCreditBook().addMark(5, Subject.DIFF);
        biba.getCreditBook().addMark(3, Subject.DIFF);
        biba.getCreditBook().addMark(3, Subject.DIFF);
        System.out.println(biba.getCreditBook().canObtainIncreasedScholarship());
        System.out.println(biba.getCreditBook().canObtainRedDiploma());
    }
}
