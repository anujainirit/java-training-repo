package com.javatraining.m08.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@DisplayName("M08-T2: JwtService — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtServiceTest {

    private static final String SECRET = "super-secret-key-for-training-repo-32chars!";
    private static final Set<String> ROLES = Set.of("ROLE_USER", "ROLE_ADMIN");

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, Duration.ofMinutes(15), Duration.ofDays(7));
    }

    // ── Constructor validation ────────────────────────────────────────────────

    @ParameterizedTest @Order(1)
    @DisplayName("Constructor rejects secrets shorter than 32 characters")
    @ValueSource(strings = {"short", "only-31-characters-long-here!!!", ""})
    void shortSecretThrows(String shortSecret) {
        assertThatThrownBy(() ->
            new JwtService(shortSecret, Duration.ofMinutes(15), Duration.ofDays(7)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // ── Access token generation ───────────────────────────────────────────────

    @Test @Order(2)
    @DisplayName("generateAccessToken() returns non-blank token")
    void generateAccessTokenNotBlank() {
        String token = jwtService.generateAccessToken("alice", ROLES);
        assertThat(token).isNotBlank();
    }

    @Test @Order(3)
    @DisplayName("Access token is a valid 3-part JWT")
    void accessTokenHasThreeParts() {
        String token = jwtService.generateAccessToken("alice", ROLES);
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test @Order(4)
    @DisplayName("extractUsername() returns correct subject from access token")
    void extractUsernameFromAccessToken() {
        String token = jwtService.generateAccessToken("alice", ROLES);
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
    }

    @Test @Order(5)
    @DisplayName("extractRoles() returns correct roles from access token")
    void extractRolesFromAccessToken() {
        String token = jwtService.generateAccessToken("alice", ROLES);
        assertThat(jwtService.extractRoles(token)).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test @Order(6)
    @DisplayName("isRefreshToken() returns false for access token")
    void accessTokenIsNotRefresh() {
        String token = jwtService.generateAccessToken("alice", ROLES);
        assertThat(jwtService.isRefreshToken(token)).isFalse();
    }

    @Test @Order(7)
    @DisplayName("Access token expiry is within expected range of 15 minutes")
    void accessTokenExpiry() {
        long before = System.currentTimeMillis();
        String token = jwtService.generateAccessToken("alice", ROLES);
        Date exp = jwtService.extractExpiration(token);

        long expectedExp = before + Duration.ofMinutes(15).toMillis();
        // Allow 5 second tolerance
        assertThat(exp.getTime()).isBetween(expectedExp - 5000, expectedExp + 5000);
    }

    // ── Refresh token ────────────────────────────────────────────────────────

    @Test @Order(8)
    @DisplayName("generateRefreshToken() returns valid token")
    void generateRefreshToken() {
        String token = jwtService.generateRefreshToken("alice");
        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
    }

    @Test @Order(9)
    @DisplayName("isRefreshToken() returns true for refresh token")
    void refreshTokenIdentified() {
        String token = jwtService.generateRefreshToken("alice");
        assertThat(jwtService.isRefreshToken(token)).isTrue();
    }

    @Test @Order(10)
    @DisplayName("extractRoles() throws for refresh token")
    void extractRolesFromRefreshTokenThrows() {
        String token = jwtService.generateRefreshToken("alice");
        assertThatThrownBy(() -> jwtService.extractRoles(token))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @Order(11)
    @DisplayName("Refresh token expiry is within expected range of 7 days")
    void refreshTokenExpiry() {
        long before = System.currentTimeMillis();
        String token = jwtService.generateRefreshToken("alice");
        Date exp = jwtService.extractExpiration(token);

        long expectedExp = before + Duration.ofDays(7).toMillis();
        assertThat(exp.getTime()).isBetween(expectedExp - 5000, expectedExp + 5000);
    }

    // ── Validation ───────────────────────────────────────────────────────────

    @Test @Order(12)
    @DisplayName("isValid() returns true for freshly generated token")
    void validTokenReturnsTrue() {
        String token = jwtService.generateAccessToken("alice", ROLES);
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test @Order(13)
    @DisplayName("isValid() returns false for tampered token")
    void tamperedTokenReturnsFalse() {
        String token = jwtService.generateAccessToken("alice", ROLES);
        String tampered = token.substring(0, token.lastIndexOf('.') + 1) + "invalidsignature";
        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test @Order(14)
    @DisplayName("Expired token throws ExpiredJwtException")
    void expiredTokenThrows() {
        JwtService shortLivedService = new JwtService(
            SECRET, Duration.ofMillis(1), Duration.ofMillis(1));
        String token = shortLivedService.generateAccessToken("alice", ROLES);

        // Wait for expiry
        try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        assertThatThrownBy(() -> shortLivedService.isValid(token))
            .isInstanceOf(ExpiredJwtException.class);
    }

    @Test @Order(15)
    @DisplayName("Token signed by different secret fails validation")
    void differentSecretFails() {
        JwtService otherService = new JwtService(
            "completely-different-secret-key-here-32chars",
            Duration.ofMinutes(15), Duration.ofDays(7));
        String token = otherService.generateAccessToken("alice", ROLES);

        // Validate with original service — should fail
        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test @Order(16)
    @DisplayName("Two tokens for same user are different (unique iat)")
    void tokensAreUnique() throws InterruptedException {
        String t1 = jwtService.generateAccessToken("alice", ROLES);
        Thread.sleep(10);
        String t2 = jwtService.generateAccessToken("alice", ROLES);
        assertThat(t1).isNotEqualTo(t2);
    }

    @Test @Order(17)
    @DisplayName("Token does not contain plain-text password or secret")
    void tokenDoesNotLeakSecret() {
        String token = jwtService.generateAccessToken("alice", ROLES);
        // The JWT is base64url-encoded, so secret must not appear in plain
        assertThat(token).doesNotContain(SECRET);
    }
}
