package com.javatraining.m11;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Simple Spring Boot application used as the target for M11 containerisation exercises.
 * Students do NOT modify this Java code — the exercises are about Docker, K8s, and Helm.
 */
@SpringBootApplication
public class CloudNativeApp {
    public static void main(String[] args) {
        SpringApplication.run(CloudNativeApp.class, args);
    }
}

@RestController
@RequestMapping("/api")
class InfoController {

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "app",       "cloud-native-service",
            "version",   "1.0.0",
            "timestamp", Instant.now().toString(),
            "javaVersion", System.getProperty("java.version"),
            "env",       System.getenv().getOrDefault("APP_ENV", "local")
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
