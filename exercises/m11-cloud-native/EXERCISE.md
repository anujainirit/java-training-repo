# M11 — Cloud Native & Kubernetes Exercises

---

## Topic 1: Multi-Stage Docker Build

### Objective
Reduce the Spring Boot Docker image from ~500MB to under 200MB using a multi-stage build with layered JARs and a non-root user.

### Files
| File | Your Action |
|---|---|
| `Dockerfile` | ✅ Replace single-stage with multi-stage build |
| `src/test/.../DockerImageTest.java` | 🚫 DO NOT MODIFY (7 Testcontainers tests) |

### Requirements
1. **Stage 1 — Builder**: use `maven:3.9-eclipse-temurin-21` to compile and extract layers
2. **Stage 2 — Runtime**: use `eclipse-temurin:21-jre-alpine` (JRE, not JDK)
3. **Layered JARs** — extract layers in builder, COPY each layer separately so Docker cache works
4. **Non-root user** — create `appuser` in Alpine, run as that user
5. **JVM container flags** — add `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0`
6. **HEALTHCHECK** — `CMD curl -f http://localhost:8080/api/health || exit 1`

### Solution Template
```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -q      # cache deps separately
COPY src ./src
RUN mvn package -DskipTests -q
RUN java -Djarmode=layertools -jar target/*.jar extract

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /build/dependencies/          ./
COPY --from=builder /build/spring-boot-loader/    ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/           ./
HEALTHCHECK --interval=10s --timeout=3s \
  CMD wget -qO- http://localhost:8080/api/health || exit 1
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "org.springframework.boot.loader.launch.JarLauncher"]
```

### Running the Grading Tests
```bash
# 1. Build the fat jar first
mvn package -DskipTests

# 2. Run Docker tests (builds image and starts container)
mvn test -Dtest=DockerImageTest

# 3. Check image size manually
docker images cloud-native-training:test
```

---

## Topic 2: Kubernetes Manifests

### Objective
Complete the Kubernetes YAML manifests and deploy to minikube. A shell script grades your work automatically.

### Files
| File | Your Action |
|---|---|
| `k8s/manifests.yaml` | ✅ Fix all FIXME values |
| `scripts/verify-k8s.sh` | 🚫 DO NOT MODIFY (grading script) |

### Steps
```bash
# Start minikube
minikube start

# Load local Docker image into minikube
eval $(minikube docker-env)
docker build -t cloud-native-training:latest .

# Create namespace
kubectl create namespace training

# Apply manifests
kubectl apply -f k8s/

# Watch pods start
kubectl get pods -n training -w

# Run grading script
chmod +x scripts/verify-k8s.sh
./scripts/verify-k8s.sh

# Access via Ingress (add to /etc/hosts first)
echo "$(minikube ip) cloud-native.training.local" | sudo tee -a /etc/hosts
curl http://cloud-native.training.local/api/info
```

### Checklist
- [ ] Namespace `training` exists
- [ ] Deployment has 2 replicas
- [ ] Both pods are Ready
- [ ] APP_ENV injected from ConfigMap = "staging"
- [ ] DB_PASSWORD injected from Secret = "supersecret123"
- [ ] readinessProbe → `/api/health`
- [ ] livenessProbe → `/actuator/health`
- [ ] Resource requests and limits set
- [ ] HPA: min=2, max=5, CPU=70%
- [ ] Ingress host = `cloud-native.training.local`

---

## Topic 3: Helm Chart

### Objective
Package the Kubernetes manifests as a Helm chart so the app can be deployed to multiple environments by just changing `values.yaml`.

### Files
| File | Your Action |
|---|---|
| `helm/cloud-native-service/values.yaml` | ✅ Fill in all FIXME values |
| `helm/cloud-native-service/templates/deployment.yaml` | ✅ Add Service + Ingress templates |
| `helm/cloud-native-service/Chart.yaml` | 🚫 Do not modify |

### Steps
```bash
# 1. Fix values.yaml

# 2. Add Service template (helm/cloud-native-service/templates/service.yaml)

# 3. Add Ingress template (helm/cloud-native-service/templates/ingress.yaml)

# 4. Lint the chart
helm lint helm/cloud-native-service/

# 5. Dry-run render to check output
helm template cloud-native-service helm/cloud-native-service/ \
  | kubectl apply --dry-run=client -f -

# 6. Install to minikube
helm install cloud-native-service helm/cloud-native-service/ -n training

# 7. Upgrade with new replica count
helm upgrade cloud-native-service helm/cloud-native-service/ \
  --set replicaCount=3 -n training

# 8. Rollback
helm rollback cloud-native-service -n training
```

### Acceptance Criteria for All M11 Topics
- [ ] Docker image < 200MB, runs as non-root, has HEALTHCHECK
- [ ] All 7 DockerImageTest tests pass
- [ ] verify-k8s.sh: 0 failures
- [ ] `helm lint` passes with 0 errors
- [ ] helm dry-run produces valid K8s YAML
- [ ] Coverage ≥ 85% on Java code
- [ ] Zero SonarQube issues
