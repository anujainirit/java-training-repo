# 🛠 Local Setup Guide

Follow this guide **once** before starting your first exercise.

---

## 1. Install Java 21

### Option A — SDKMan (recommended for all OS)
```bash
# Install SDKMan
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 21 (Temurin / Eclipse)
sdk install java 21.0.3-tem
sdk use java 21.0.3-tem
java -version   # should show: openjdk 21
```

### Option B — Direct download
Download from https://adoptium.net/temurin/releases/?version=21

---

## 2. Install Maven
```bash
sdk install maven 3.9.6
mvn -version   # should show: Apache Maven 3.9.x
```

---

## 3. Install Docker Desktop
Required for **Testcontainers** (integration tests spin up real PostgreSQL / Kafka).

- **Windows/Mac**: https://www.docker.com/products/docker-desktop/
- **Linux**: https://docs.docker.com/engine/install/

Verify:
```bash
docker run hello-world
```

---

## 4. Clone and Build
```bash
# Fork this repo on GitHub first, then clone YOUR fork
git clone https://github.com/<YOUR_USERNAME>/java-training-repo.git
cd java-training-repo

# Build all modules (downloads dependencies, runs no tests yet)
mvn clean compile -DskipTests

# Expected output: BUILD SUCCESS
```

---

## 5. IntelliJ IDEA Setup

1. Open IntelliJ IDEA → **File → Open** → select `pom.xml` at the root
2. Choose **"Open as Project"**
3. IntelliJ will import all Maven modules automatically
4. Go to **File → Project Structure → Project SDK** → select Java 21

### Recommended plugins:
- **SonarLint** — highlights Sonar issues as you type
- **JaCoCo** — coverage overlay in editor

---

## 6. Run Your First Exercise

```bash
cd exercises/m01-java-core

# Run only the tests for this module
mvn test

# Run tests + coverage check
mvn verify

# View coverage report
open target/site/jacoco/index.html     # macOS
xdg-open target/site/jacoco/index.html # Linux
start target/site/jacoco/index.html    # Windows
```

---

## 7. Optional — Local SonarQube

Run SonarQube locally to pre-check before pushing:

```bash
# Start SonarQube (first run downloads ~500MB)
docker run -d --name sonarqube \
  -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  sonarqube:10-community

# Wait ~60 seconds, then open: http://localhost:9000
# Default login: admin / admin (change on first login)

# Run analysis (from module root)
mvn sonar:sonar \
  -Dsonar.projectKey=m01-java-core \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<your_local_token>
```

---

## 8. Git Configuration

```bash
git config --global user.name "Your Full Name"
git config --global user.email "your.email@example.com"

# Create branch for your first exercise
git checkout -b m01/topic3-grade-tracker/your-name
```

---

## Troubleshooting

### `JAVA_HOME not set`
```bash
export JAVA_HOME=$(sdk home java 21.0.3-tem)
export PATH=$JAVA_HOME/bin:$PATH
```

### `Docker daemon not running`
Start Docker Desktop, or on Linux: `sudo systemctl start docker`

### `Port 9000 already in use` (SonarQube)
```bash
docker stop sonarqube && docker start sonarqube
```

### Tests pass locally but fail in CI
- Check that you haven't modified any test file
- Run `mvn verify` (not just `mvn test`) locally — this also runs the coverage gate
- Check the CI logs in the GitHub Actions tab of your PR
