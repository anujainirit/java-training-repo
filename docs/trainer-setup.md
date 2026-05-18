# 🔧 Trainer Setup Guide

One-time setup to activate CI, SonarCloud, and branch protection on GitHub.

---

## Step 1 — Create the GitHub Repository

1. Create a **new public** repository: `java-training-repo`
2. Push this code:
   ```bash
   git init
   git add .
   git commit -m "Initial: training repo with all modules"
   git remote add origin https://github.com/<YOUR_ORG>/java-training-repo.git
   git push -u origin main
   ```

---

## Step 2 — SonarCloud Setup

1. Go to https://sonarcloud.io → **Log in with GitHub**
2. Click **+ → Analyze new project → GitHub → select your repo**
3. Choose **"With GitHub Actions"** setup method
4. Copy the generated `SONAR_TOKEN`
5. Note your `Organization Key` and `Project Key`

---

## Step 3 — GitHub Secrets

Go to repo **Settings → Secrets and variables → Actions → New repository secret**:

| Secret Name | Value |
|---|---|
| `SONAR_TOKEN` | Token from SonarCloud |
| `SONAR_HOST_URL` | `https://sonarcloud.io` |
| `SONAR_PROJECT_KEY` | Your SonarCloud project key |
| `SONAR_ORGANIZATION` | Your SonarCloud organization key |

---

## Step 4 — SonarCloud Quality Gate

In SonarCloud UI → **Quality Gates → Create** a new gate named **"Training Zero Tolerance"**:

| Metric | Condition | Value | On New Code |
|---|---|---|---|
| Bugs | Greater Than | 0 | ✅ Yes |
| Vulnerabilities | Greater Than | 0 | ✅ Yes |
| Security Hotspots Reviewed | Less Than | 100% | ✅ Yes |
| Coverage | Less Than | 85% | ✅ Yes |
| Duplicated Lines (%) | Greater Than | 3% | ✅ Yes |
| Reliability Rating | Worse Than | A | ✅ Yes |
| Security Rating | Worse Than | A | ✅ Yes |

Then go to your project → **Administration → Quality Gate → select "Training Zero Tolerance"**

---

## Step 5 — Branch Protection Rules

Go to repo **Settings → Branches → Add rule** for branch `main`:

- [x] Require a pull request before merging
- [x] Require approvals: **1** (trainer must review)
- [x] Require status checks to pass before merging
  - Add: `🧪 Test & Coverage Gate (≥85%)`
  - Add: `🔬 SonarQube Analysis (Zero Issues Gate)`
  - Add: `✅ All Gates Passed`
- [x] Require branches to be up to date before merging
- [x] Do not allow bypassing the above settings

---

## Step 6 — Protect Test Files

Add this to `.github/workflows/ci.yml` to detect test modifications:

```yaml
- name: 🔒 Verify test files not modified
  run: |
    MODIFIED_TESTS=$(git diff --name-only origin/main...HEAD \
      | grep 'src/test/java' | grep -v 'OrderServiceTest.java') # M13 is intentionally modifiable
    if [ -n "$MODIFIED_TESTS" ]; then
      echo "❌ Student modified grading test files:"
      echo "$MODIFIED_TESTS"
      exit 1
    fi
```

---

## Step 7 — Student Onboarding

Send students the `docs/local-setup.md` guide and this message:

> Welcome to the Java Backend & Microservices training!
>
> 1. Fork this repo: [link]
> 2. Follow: `docs/local-setup.md`
> 3. Start with `exercises/m01-java-core/EXERCISE.md`
> 4. Open a PR when ready — CI will grade automatically
>
> Do not modify any file inside `src/test/java` (except M13).

---

## Daily Trainer Workflow

```bash
# Review pending PRs
gh pr list --state open

# Check CI status of a specific PR
gh pr checks <PR_NUMBER>

# Approve a PR after review
gh pr review <PR_NUMBER> --approve

# Merge after all gates pass
gh pr merge <PR_NUMBER> --squash
```

---

## Monitoring Student Progress

SonarCloud Dashboard → your project → **Activity** tab shows:
- Per-PR quality gate results
- Coverage trends over time
- Which students have most issues

GitHub Insights → **Pulse** shows PR activity by contributor.
