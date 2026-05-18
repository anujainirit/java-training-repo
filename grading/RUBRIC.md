# 🏅 Grading Rubric

Every PR submission is scored out of **100 points** across four dimensions.

---

## Automated Gates (80 points total)

These are checked automatically by CI on every PR push.  
A failing gate **blocks merging** until fixed.

### 1. Tests Pass — 40 points

| Result | Points |
|---|---|
| All grading tests pass | 40 |
| ≥ 80% tests pass | 30 |
| ≥ 60% tests pass | 20 |
| < 60% tests pass | 0 |

> Artificially making tests pass (hardcoding values, modifying test files) = automatic 0 for the entire module and a mandatory re-submission.

### 2. Code Coverage — 20 points

| JaCoCo Line Coverage | Points |
|---|---|
| ≥ 90% | 20 |
| 85–89% | 15 |
| 75–84% | 8 |
| < 75% | 0 (blocks merge) |

### 3. SonarQube Quality — 20 points

| SonarQube Result | Points |
|---|---|
| Zero issues, Quality Gate = Passed | 20 |
| Zero bugs/vulnerabilities, ≤ 2 code smells | 15 |
| Zero bugs/vulnerabilities, > 2 code smells | 10 |
| Any bug or vulnerability | 0 (blocks merge) |

---

## Trainer Code Review (20 points)

Trainer reviews run **after** CI passes. Scored on:

### Code Design (10 points)

| Criterion | Max Points |
|---|---|
| Appropriate data structure choice | 3 |
| Single Responsibility per class/method | 3 |
| Clear naming (classes, methods, variables) | 2 |
| Avoids code duplication (DRY) | 2 |

### Java Best Practices (10 points)

| Criterion | Max Points |
|---|---|
| Proper exception handling (not swallowing) | 2 |
| Correct use of generics and Collections API | 2 |
| Streams/functional style where appropriate | 2 |
| Constructor injection over field injection | 2 |
| Javadoc on public methods | 2 |

---

## Score Thresholds

| Score | Grade |
|---|---|
| 90–100 | ⭐ Excellent |
| 75–89  | ✅ Pass |
| 60–74  | ⚠️ Needs improvement — rework & resubmit |
| < 60   | ❌ Fail — mandatory re-submission |

---

## Common Deductions

These apply regardless of test results:

| Issue | Deduction |
|---|---|
| Modified test file(s) | −40 (and mandatory re-submission) |
| TODO comments left in code | −5 |
| Unused imports | −2 |
| Magic numbers without constants | −3 |
| System.out.println in production code | −3 |
| Empty catch blocks | −5 |
| Field injection (@Autowired on field) in Spring code | −5 |

---

## Resubmission Policy

- You may resubmit **unlimited times** before the module deadline
- Simply push new commits to your PR branch — CI re-runs automatically
- Trainer reviews update when you request a re-review

---

## Appeals

If you believe a test case is incorrect or the grading is unfair:
1. Open a GitHub Issue with label `grading-appeal`
2. Include: module, test name, your reasoning
3. Trainer will respond within 48 hours
