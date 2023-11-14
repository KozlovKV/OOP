package kozlov.kirill.creditbook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreditBookTest {
    @Test
    void testNamesake() {
        var s1 = new Student("Ivanov", "Ivan");
        var s2 = new Student("Ivanov", "Ivan");
        Assertions.assertNotEquals(s1, s2);
    }

    @Test
    void testMarkValueBounds() {
        var s1 = new Student("Ivanov", "Ivan");
        Assertions.assertEquals(
                s1.getCreditBook().addMark(0, Subject.DIFF),
                new Mark(2, Student.SEMESTER_NUMBER_BASE, Subject.DIFF)
        );
        Assertions.assertEquals(
                s1.getCreditBook().addMark(10, Subject.OOP),
                new Mark(5, Student.SEMESTER_NUMBER_BASE, Subject.OOP)
        );
    }

    @Test
    void testMarkOverridingBlocking() {
        var s1 = new Student("Ivanov", "Ivan");
        Assertions.assertEquals(
                s1.getCreditBook().addMark(3, Subject.DIFF),
                new Mark(3, Student.SEMESTER_NUMBER_BASE, Subject.DIFF)
        );
        Assertions.assertNull(s1.getCreditBook().addMark(5, Subject.DIFF));
    }

    @Test
    void testCommonAvgMarkInOneSemester() {
        var s1 = new Student("Ivanov", "Ivan");
        s1.getCreditBook().addMark(3, Subject.DIFF);
        s1.getCreditBook().addMark(4, Subject.OOP);
        s1.getCreditBook().addMark(5, Subject.AI);
        Assertions.assertEquals(s1.getCreditBook().averageMark(), 4.0);
    }

    @Test
    void testCommonAvgMarkWithDifferentSemesters() {
        var s1 = new Student("Ivanov", "Ivan");
        s1.getCreditBook().addMark(3, Subject.DIFF);
        s1.finishSemester();
        s1.getCreditBook().addMark(4, Subject.DIFF);
        s1.finishSemester();
        s1.getCreditBook().addMark(5, Subject.DIFF);
        Assertions.assertEquals(s1.getCreditBook().averageMark(), 4.0);
    }

    @Test
    void testIncreasedScholarshipObtaining() {
        var s1 = new Student("Ivanov", "Ivan");
        Assertions.assertFalse(s1.getCreditBook().canObtainIncreasedScholarship());
        s1.getCreditBook().addMark(5, Subject.DIFF);
        s1.getCreditBook().addMark(5, Subject.AI);
        Assertions.assertFalse(s1.getCreditBook().canObtainIncreasedScholarship());
        s1.finishSemester();
        Assertions.assertTrue(s1.getCreditBook().canObtainIncreasedScholarship());
        s1.getCreditBook().addMark(4, Subject.OOP);
        Assertions.assertTrue(s1.getCreditBook().canObtainIncreasedScholarship());
        s1.finishSemester();
        Assertions.assertFalse(s1.getCreditBook().canObtainIncreasedScholarship());
    }

    @Test
    void testRedDiplomaObtaining() {
        var s1 = new Student("Ivanov", "Ivan");
        Assertions.assertFalse(s1.getCreditBook().canObtainRedDiploma());
        s1.getCreditBook().addMark(3, Subject.FIZRA);
        s1.getCreditBook().addMark(5, Subject.IMPERATIVE);
        s1.getCreditBook().addMark(5, Subject.DECLARATIVE);
        s1.finishSemester();
        s1.getCreditBook().addMark(5, Subject.DIFF);
        s1.getCreditBook().addMark(5, Subject.OOP);
        s1.getCreditBook().addMark(5, Subject.PAK);
        s1.getCreditBook().addMark(5, Subject.OS);
        s1.getCreditBook().addMark(5, Subject.AI);
        s1.finishSemester();
        s1.getCreditBook().addMark(5, Subject.DIPLOMA);
        Assertions.assertFalse(s1.getCreditBook().canObtainRedDiploma());
        s1.getCreditBook().addMark(4, Subject.FIZRA);
        Assertions.assertTrue(s1.getCreditBook().canObtainRedDiploma());
    }

    @Test
    void testRedDiplomaObtainingWithSpecificDiplomaSituations() {
        var s1 = new Student("Ivanov", "Ivan");
        Assertions.assertFalse(s1.getCreditBook().canObtainRedDiploma());
        s1.getCreditBook().addMark(5, Subject.IMPERATIVE);
        s1.getCreditBook().addMark(5, Subject.DECLARATIVE);
        s1.finishSemester();
        s1.getCreditBook().addMark(5, Subject.DIFF);
        s1.getCreditBook().addMark(5, Subject.OOP);
        s1.getCreditBook().addMark(5, Subject.PAK);
        s1.getCreditBook().addMark(5, Subject.OS);
        s1.getCreditBook().addMark(5, Subject.AI);
        s1.finishSemester();
        Assertions.assertFalse(s1.getCreditBook().canObtainRedDiploma());
        s1.getCreditBook().addMark(4, Subject.DIPLOMA);
        Assertions.assertFalse(s1.getCreditBook().canObtainRedDiploma());
        s1.finishSemester();
        s1.getCreditBook().addMark(5, Subject.DIPLOMA);
        Assertions.assertTrue(s1.getCreditBook().canObtainRedDiploma());
    }
}
