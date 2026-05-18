package com.javatraining.m01.collections;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 *  These tests are run by the CI pipeline on every PR.
 *  Altering this file will be detected and result in a grade of 0.
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M01-T3: GradeTracker — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GradeTrackerTest {

    private GradeTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new GradeTracker();
    }

    // ── Happy path ──────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("studentCount() returns 0 for empty tracker")
    void emptyTrackerHasZeroStudents() {
        assertThat(tracker.studentCount()).isZero();
    }

    @Test
    @Order(2)
    @DisplayName("addGrade() creates a new student entry")
    void addGradeCreatesStudent() {
        tracker.addGrade("Alice", 90);
        assertThat(tracker.studentCount()).isEqualTo(1);
    }

    @Test
    @Order(3)
    @DisplayName("addGrade() appends grades for existing student")
    void addGradeAppendsForExistingStudent() {
        tracker.addGrade("Alice", 80);
        tracker.addGrade("Alice", 90);
        tracker.addGrade("Alice", 70);
        assertThat(tracker.getAverage("Alice")).isEqualTo(80.0);
        assertThat(tracker.studentCount()).isEqualTo(1);  // still 1 student
    }

    @ParameterizedTest
    @Order(4)
    @DisplayName("getAverage() computes correct average")
    @CsvSource({
        "Alice, 100, 80, 60 | 80.0",
        "Bob,   95, 85      | 90.0",
        "Carol, 77          | 77.0",
        "Dave,  0, 100      | 50.0"
    })
    void averageIsCorrect(String input) {
        String[] parts = input.split("\\|");
        String[] namePlusGrades = parts[0].trim().split(",\\s*");
        String studentName = namePlusGrades[0].trim();
        double expectedAvg = Double.parseDouble(parts[1].trim());

        for (int i = 1; i < namePlusGrades.length; i++) {
            tracker.addGrade(studentName, Integer.parseInt(namePlusGrades[i].trim()));
        }
        assertThat(tracker.getAverage(studentName)).isEqualTo(expectedAvg);
    }

    @Test
    @Order(5)
    @DisplayName("getAverage() rounds to 2 decimal places")
    void averageRoundsToTwoDecimals() {
        tracker.addGrade("Alice", 100);
        tracker.addGrade("Alice", 0);
        tracker.addGrade("Alice", 1);  // avg = 33.333...
        double avg = tracker.getAverage("Alice");
        assertThat(avg).isEqualTo(33.33);
    }

    @Test
    @Order(6)
    @DisplayName("getTopStudents() returns correct top-N sorted by avg desc")
    void topStudentsCorrectOrder() {
        tracker.addGrade("Alice", 90);
        tracker.addGrade("Bob", 80);
        tracker.addGrade("Carol", 95);
        tracker.addGrade("Dave", 70);

        List<String> top3 = tracker.getTopStudents(3);
        assertThat(top3).containsExactly("Carol", "Alice", "Bob");
    }

    @Test
    @Order(7)
    @DisplayName("getTopStudents() breaks ties alphabetically")
    void topStudentsTieBreakAlphabetical() {
        tracker.addGrade("Zara", 85);
        tracker.addGrade("Anna", 85);
        tracker.addGrade("Mike", 85);

        List<String> top2 = tracker.getTopStudents(2);
        assertThat(top2).containsExactly("Anna", "Mike");
    }

    @Test
    @Order(8)
    @DisplayName("getTopStudents() returns all students when N >= count")
    void topStudentsReturnsAllWhenNExceedsCount() {
        tracker.addGrade("Alice", 90);
        tracker.addGrade("Bob", 80);

        List<String> top10 = tracker.getTopStudents(10);
        assertThat(top10).hasSize(2);
    }

    @Test
    @Order(9)
    @DisplayName("getTopStudents() returns immutable list")
    void topStudentsListIsImmutable() {
        tracker.addGrade("Alice", 90);
        List<String> result = tracker.getTopStudents(1);
        assertThatThrownBy(() -> result.add("Hacker"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @Order(10)
    @DisplayName("getAllUniqueGrades() returns sorted set of all distinct grades")
    void uniqueGradesSortedAndDistinct() {
        tracker.addGrade("Alice", 90);
        tracker.addGrade("Alice", 80);
        tracker.addGrade("Bob", 90);   // duplicate — should appear once
        tracker.addGrade("Carol", 70);

        SortedSet<Integer> unique = tracker.getAllUniqueGrades();
        assertThat(unique).containsExactly(70, 80, 90);
    }

    @Test
    @Order(11)
    @DisplayName("removeStudent() returns true and removes the student")
    void removeExistingStudent() {
        tracker.addGrade("Alice", 90);
        boolean removed = tracker.removeStudent("Alice");
        assertThat(removed).isTrue();
        assertThat(tracker.studentCount()).isZero();
    }

    @Test
    @Order(12)
    @DisplayName("removeStudent() returns false for unknown student")
    void removeNonExistentStudent() {
        boolean removed = tracker.removeStudent("Nobody");
        assertThat(removed).isFalse();
    }

    @Test
    @Order(13)
    @DisplayName("getAverage() throws after student is removed")
    void getAverageAfterRemoveThrows() {
        tracker.addGrade("Alice", 90);
        tracker.removeStudent("Alice");
        assertThatThrownBy(() -> tracker.getAverage("Alice"))
            .isInstanceOf(NoSuchElementException.class);
    }

    // ── Edge cases & validation ─────────────────────────────────────────────

    @ParameterizedTest
    @Order(14)
    @DisplayName("addGrade() throws IllegalArgumentException for invalid grades")
    @ValueSource(ints = {-1, 101, -100, Integer.MIN_VALUE, Integer.MAX_VALUE})
    void addGradeRejectsOutOfRangeGrade(int invalidGrade) {
        assertThatThrownBy(() -> tracker.addGrade("Alice", invalidGrade))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("grade");
    }

    @ParameterizedTest
    @Order(15)
    @DisplayName("addGrade() accepts boundary grades 0 and 100")
    @ValueSource(ints = {0, 100})
    void addGradeAcceptsBoundaryValues(int boundaryGrade) {
        assertThatNoException().isThrownBy(() -> tracker.addGrade("Alice", boundaryGrade));
    }

    @Test
    @Order(16)
    @DisplayName("addGrade() throws for null student name")
    void addGradeRejectsNullName() {
        assertThatThrownBy(() -> tracker.addGrade(null, 90))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @Order(17)
    @DisplayName("addGrade() throws for blank student name")
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    void addGradeRejectsBlankName(String blank) {
        assertThatThrownBy(() -> tracker.addGrade(blank, 90))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Order(18)
    @DisplayName("getAverage() throws NoSuchElementException for unknown student")
    void getAverageUnknownStudentThrows() {
        assertThatThrownBy(() -> tracker.getAverage("Ghost"))
            .isInstanceOf(NoSuchElementException.class);
    }

    @ParameterizedTest
    @Order(19)
    @DisplayName("getTopStudents() throws for n <= 0")
    @ValueSource(ints = {0, -1, -100})
    void getTopStudentsRejectsNonPositiveN(int invalidN) {
        tracker.addGrade("Alice", 90);
        assertThatThrownBy(() -> tracker.getTopStudents(invalidN))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Order(20)
    @DisplayName("getAllUniqueGrades() returns empty set for empty tracker")
    void uniqueGradesEmptyTracker() {
        assertThat(tracker.getAllUniqueGrades()).isEmpty();
    }

    @Test
    @Order(21)
    @DisplayName("Student names are case-sensitive")
    void studentNamesCaseSensitive() {
        tracker.addGrade("alice", 80);
        tracker.addGrade("Alice", 90);
        assertThat(tracker.studentCount()).isEqualTo(2);
        assertThat(tracker.getAverage("alice")).isEqualTo(80.0);
        assertThat(tracker.getAverage("Alice")).isEqualTo(90.0);
    }

    @Test
    @Order(22)
    @DisplayName("Handles large number of students efficiently")
    void handlesLargeInput() {
        for (int i = 0; i < 10_000; i++) {
            tracker.addGrade("Student" + i, i % 101);
        }
        assertThat(tracker.studentCount()).isEqualTo(10_000);
        List<String> top5 = tracker.getTopStudents(5);
        assertThat(top5).hasSize(5);
    }
}
