# ══════════════════════════════════════════════════════════════
# EXERCISE M11-T1: Multi-Stage Docker Build
#
# This Dockerfile currently uses a single stage and produces a
# HUGE image (~500MB+). Your task is to:
#
#   1. Convert to a multi-stage build:
#        Stage 1 (builder): use maven:3.9-eclipse-temurin-21 to compile
#        Stage 2 (runtime): use eclipse-temurin:21-jre-alpine as the base
#
#   2. Use Spring Boot layered JARs for efficient layer caching:
#        RUN java -Djarmode=layertools -jar app.jar extract
#        Then COPY each layer separately (dependencies first, app last)
#
#   3. Add a non-root user for security:
#        RUN addgroup -S appgroup && adduser -S appuser -G appgroup
#        USER appuser
#
#   4. Set JVM memory flags appropriate for a container:
#        -XX:+UseContainerSupport
#        -XX:MaxRAMPercentage=75.0
#
#   5. Add HEALTHCHECK instruction
#
# Target: final image must be < 200MB
# Run the grading test to verify: mvn test -Dtest=DockerImageTest
# ══════════════════════════════════════════════════════════════

# TODO: Replace this naive single-stage build with a proper multi-stage build

FROM eclipse-temurin:21-jdk

WORKDIR /app

# This copies the entire fat jar — inefficient, no layer caching
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
