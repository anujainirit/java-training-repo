package com.javatraining.m01.collections;

import java.util.*;

/**
 * EXERCISE M01-T3: Student Grade Tracker
 *
 * Implement a grade tracker for a classroom.
 * Read the EXERCISE.md for the full problem statement and constraints.
 *
 * DO NOT change method signatures or package names.
 * The grading tests depend on them exactly as written here.
 */
public class GradeTracker {

    // TODO: choose an appropriate data structure to store student grades
    // Key: student name (String), Value: list of grades (List<Integer>)

    /**
     * Add a grade for a student.
     * If the student already exists, append the grade to their list.
     * If not, create a new entry.
     *
     * @param studentName non-null, non-blank student name
     * @param grade       integer grade between 0 and 100 (inclusive)
     * @throws IllegalArgumentException if name is null/blank or grade is out of range
     */
    public void addGrade(String studentName, int grade) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Return the average grade for a student, rounded to 2 decimal places.
     *
     * @param studentName the student's name
     * @return average as a double
     * @throws NoSuchElementException if the student is not found
     */
    public double getAverage(String studentName) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Return the top-N students by average grade, sorted descending.
     * In case of a tie, sort alphabetically ascending by name.
     *
     * @param n number of top students to return (must be > 0)
     * @return immutable list of student names
     * @throws IllegalArgumentException if n <= 0
     */
    public List<String> getTopStudents(int n) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Return all unique grades ever recorded across all students, sorted ascending.
     *
     * @return sorted set of unique grades
     */
    public SortedSet<Integer> getAllUniqueGrades() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Remove a student and all their grades.
     *
     * @param studentName the student's name
     * @return true if the student existed and was removed, false otherwise
     */
    public boolean removeStudent(String studentName) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Return the number of students currently tracked.
     */
    public int studentCount() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
