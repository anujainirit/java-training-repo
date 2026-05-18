# ☕ Java Backend & Microservices — Training Repository

> **Trainer-managed repository** for the new joiner batch.  
> All student exercise submissions happen via **Pull Requests** to this repo.

---

## 📐 How This Repo Is Organised

```
java-training-repo/
├── exercises/
│   ├── m01-java-core/          ← Starter code + pre-written test cases
│   ├── m02-advanced-java/
│   ├── ...
│   └── m15-capstone/
├── grading/                    ← Grading rubrics & scripts
├── docs/                       ← Setup guides, architecture diagrams
├── scripts/                    ← Helper scripts for local setup
└── .github/
    ├── workflows/              ← CI: test + coverage + SonarQube
    └── ISSUE_TEMPLATE/         ← Bug / question templates
```

---

## 🚀 Student Workflow

### 1. Fork this repo
```bash
# Click "Fork" on GitHub, then clone YOUR fork
git clone https://github.com/<YOUR_GITHUB_USERNAME>/java-training-repo.git
cd java-training-repo
```

### 2. Create a branch for each exercise
```bash
# Branch naming convention: <module>/<topic>/<your-name>
git checkout -b m01/topic1-jvm-basics/john-doe
```

### 3. Navigate to the exercise folder
```bash
cd exercises/m01-java-core/topic1-jvm-basics
```
Read the `EXERCISE.md` inside that folder. It contains:
- The problem statement
- Acceptance criteria
- Hints

### 4. Write your solution
All exercises have a `src/main/java` stub with `TODO` markers.  
**Do NOT modify** `src/test/java` — those are the grading tests.

### 5. Run tests locally before pushing
```bash
mvn verify         # runs tests + JaCoCo coverage report
mvn sonar:sonar    # optional: run SonarQube locally (see docs/local-sonar.md)
```

### 6. Open a Pull Request
- **Base branch:** `main` (this repo)  
- **Compare branch:** your fork's feature branch  
- Fill in the **PR template** (auto-loaded)  
- Wait for CI to go ✅ green

> ⚠️ PRs with failing tests, coverage < 85 %, or SonarQube issues **will be auto-blocked** from merging.

---

## ✅ CI Gates (auto-enforced on every PR)

| Gate | Threshold | Blocks Merge? |
|---|---|---|
| Unit & Integration Tests | All must pass | ✅ Yes |
| JaCoCo Line Coverage | ≥ 85 % | ✅ Yes |
| JaCoCo Branch Coverage | ≥ 80 % | ✅ Yes |
| SonarQube Bugs | 0 | ✅ Yes |
| SonarQube Vulnerabilities | 0 | ✅ Yes |
| SonarQube Code Smells | 0 (blocker/critical) | ✅ Yes |
| SonarQube Quality Gate | Passed | ✅ Yes |

---

## 🛠 Prerequisites (Local Setup)

```bash
# Java 21
sdk install java 21.0.3-tem      # via SDKMan
java -version

# Maven
sdk install maven 3.9.6
mvn -version

# Docker (for integration tests with Testcontainers)
docker --version

# Optional: local SonarQube
docker run -d --name sonarqube -p 9000:9000 sonarqube:community
```

Full local setup guide → [`docs/local-setup.md`](docs/local-setup.md)

---

## 📋 Module Index

| # | Module | Exercises | Difficulty |
|---|---|---|---|
| M01 | Java Core Fundamentals | 8 exercises | Beginner |
| M02 | Advanced Java | 6 exercises | Intermediate |
| M03 | Build Tools & Git | 3 exercises | Beginner |
| M04 | Spring Framework Core | 6 exercises | Intermediate |
| M05 | Spring Boot | 5 exercises | Intermediate |
| M06 | Persistence & Databases | 6 exercises | Intermediate |
| M07 | REST API Design | 4 exercises | Intermediate |
| M08 | Security | 5 exercises | Intermediate |
| M09 | Microservices Architecture | 6 exercises | Advanced |
| M10 | Service Communication | 5 exercises | Advanced |
| M11 | Cloud Native | 5 exercises | Advanced |
| M12 | Observability | 4 exercises | Advanced |
| M13 | Testing Strategy | 4 exercises | Intermediate |
| M14 | CI/CD & DevOps | 3 exercises | Intermediate |
| M15 | Capstone Project | 4 exercises | Advanced |

---

## 🏅 Grading

Each PR is auto-scored. Trainer reviews are triggered only after CI passes.

| Criterion | Weight |
|---|---|
| All tests pass | 40 % |
| Coverage ≥ 85 % | 20 % |
| SonarQube clean | 20 % |
| Code readability & design | 20 % |

Detailed rubric → [`grading/RUBRIC.md`](grading/RUBRIC.md)

---

## 🙋 Getting Help

- Open a GitHub Issue using the **"Question / Help"** template
- Tag your trainer: `@trainer`
- Do **not** paste solutions in Issues — ask for hints only


