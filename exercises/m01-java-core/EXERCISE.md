# M01 — Topic 3: Student Grade Tracker

## Objective
Implement a `GradeTracker` class that manages student grades using the Java Collections Framework.

## Problem Statement
You are building the backend for a school's grade management system.  
The `GradeTracker` must efficiently store, query, and rank student grades.

## Files
| File | Your Action |
|---|---|
| `src/main/java/.../GradeTracker.java` | ✅ Implement all TODO methods |
| `src/test/java/.../GradeTrackerTest.java` | 🚫 DO NOT MODIFY |

## Method Specifications

### `addGrade(String studentName, int grade)`
- Creates a new student entry if they don't exist yet
- Appends the grade if they already exist
- Valid grade range: **0–100 inclusive**
- Throws `IllegalArgumentException` if name is null/blank or grade is out of range (message must contain the word "grade")

### `getAverage(String studentName) → double`
- Returns average rounded to **exactly 2 decimal places**  
  *(hint: `Math.round(avg * 100.0) / 100.0`)*
- Throws `NoSuchElementException` if student not found

### `getTopStudents(int n) → List<String>`
- Sorted **descending** by average grade
- Ties broken **alphabetically ascending** by name
- Returns all students if `n >= studentCount()`
- Returns an **immutable** list
- Throws `IllegalArgumentException` if `n <= 0`

### `getAllUniqueGrades() → SortedSet<Integer>`
- All unique grades across all students, **ascending order**
- Returns empty set if no students

### `removeStudent(String studentName) → boolean`
- Returns `true` if removed, `false` if not found
- After removal, `getAverage()` for that student must throw

### `studentCount() → int`
- Current number of tracked students

## Hints
- `HashMap<String, List<Integer>>` is a natural fit for storage
- For sorting by average, consider `Comparator.comparingDouble(...).thenComparing(...)`
- Use `Collections.unmodifiableList()` for the immutable return
- `TreeSet` gives you sorted uniqueness for free

## Constraints
- No loops in `getTopStudents()` — use Streams
- Thread safety is **not** required

## Running Locally
```bash
cd exercises/m01-java-core
mvn test -Dtest=GradeTrackerTest
mvn verify   # also runs coverage gate
open target/site/jacoco/index.html
```

## Acceptance Criteria
- [ ] All 22 test cases pass
- [ ] Line coverage ≥ 85% (verified by CI)
- [ ] Zero SonarQube issues
