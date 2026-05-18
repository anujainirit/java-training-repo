package com.javatraining.m11;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 *
 *  These tests BUILD your Dockerfile and run the container.
 *  Prerequisites:
 *    1. mvn package -DskipTests   (build the fat jar first)
 *    2. Docker must be running
 * ══════════════════════════════════════════════════════════════
 */
@Testcontainers
@DisplayName("M11-T1: Docker Image — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DockerImageTest {

    @Container
    static GenericContainer<?> app = new GenericContainer<>(
        new ImageFromDockerfile("cloud-native-training:test", false)
            .withDockerfile(Path.of("Dockerfile"))
    )
        .withExposedPorts(8080)
        .waitingFor(Wait.forHttp("/api/health").forStatusCode(200))
        .withStartupTimeout(Duration.ofSeconds(60));

    private static HttpClient httpClient;
    private static String baseUrl;

    @BeforeAll
    static void setup() {
        httpClient = HttpClient.newHttpClient();
        baseUrl = "http://localhost:" + app.getMappedPort(8080);
    }

    @Test @Order(1)
    @DisplayName("Container starts successfully and /api/health returns 200")
    void containerStartsAndHealthy() throws Exception {
        HttpResponse<String> resp = httpClient.send(
            HttpRequest.newBuilder(URI.create(baseUrl + "/api/health")).GET().build(),
            HttpResponse.BodyHandlers.ofString()
        );
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(resp.body()).contains("UP");
    }

    @Test @Order(2)
    @DisplayName("/api/info returns correct app name and version")
    void infoEndpointReturnsCorrectData() throws Exception {
        HttpResponse<String> resp = httpClient.send(
            HttpRequest.newBuilder(URI.create(baseUrl + "/api/info")).GET().build(),
            HttpResponse.BodyHandlers.ofString()
        );
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(resp.body())
            .contains("cloud-native-service")
            .contains("1.0.0")
            .contains("21");  // Java 21
    }

    @Test @Order(3)
    @DisplayName("Image runs as non-root user (security requirement)")
    void containerRunsAsNonRoot() throws Exception {
        // exec 'id' inside the running container and verify UID != 0
        var result = app.execInContainer("id");
        String output = result.getStdout();
        assertThat(output)
            .as("Container must NOT run as root (uid=0)")
            .doesNotContain("uid=0(root)");
    }

    @Test @Order(4)
    @DisplayName("Image size is under 200MB (multi-stage build required)")
    void imageSizeUnder200MB() {
        // Inspect image size via Docker client
        var dockerClient = app.getDockerClient();
        var images = dockerClient.listImagesCmd()
            .withImageNameFilter("cloud-native-training:test")
            .exec();

        assertThat(images).isNotEmpty();
        long sizeBytes = images.get(0).getSize();
        long sizeMB = sizeBytes / (1024 * 1024);

        assertThat(sizeMB)
            .as("Image size should be < 200MB but was %dMB. Use multi-stage build!", sizeMB)
            .isLessThan(200);
    }

    @Test @Order(5)
    @DisplayName("Container has HEALTHCHECK instruction defined")
    void dockerfileHasHealthcheck() throws Exception {
        var dockerClient = app.getDockerClient();
        var inspect = dockerClient.inspectImageCmd("cloud-native-training:test").exec();
        var healthcheck = inspect.getConfig().getHealthcheck();

        assertThat(healthcheck)
            .as("Dockerfile must include a HEALTHCHECK instruction")
            .isNotNull();
        assertThat(healthcheck.getTest())
            .as("HEALTHCHECK must call the /api/health endpoint")
            .isNotEmpty();
    }

    @Test @Order(6)
    @DisplayName("Spring Boot Actuator /actuator/health is accessible")
    void actuatorHealthAccessible() throws Exception {
        HttpResponse<String> resp = httpClient.send(
            HttpRequest.newBuilder(URI.create(baseUrl + "/actuator/health")).GET().build(),
            HttpResponse.BodyHandlers.ofString()
        );
        assertThat(resp.statusCode()).isEqualTo(200);
        assertThat(resp.body()).contains("\"status\":\"UP\"");
    }

    @Test @Order(7)
    @DisplayName("APP_ENV environment variable is read from container env")
    void envVariableInjected() throws Exception {
        // The app returns APP_ENV in /api/info — default is "local"
        HttpResponse<String> resp = httpClient.send(
            HttpRequest.newBuilder(URI.create(baseUrl + "/api/info")).GET().build(),
            HttpResponse.BodyHandlers.ofString()
        );
        assertThat(resp.body()).contains("local");
    }
}
