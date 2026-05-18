package com.javatraining.m08.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * EXERCISE M08-T2: JWT Authentication Service
 *
 * Implement a JWT token service using the JJWT library.
 *
 * Requirements:
 *  - Sign tokens with HS256 using the provided secret key
 *  - Access token expiry: configurable (default 15 minutes)
 *  - Refresh token expiry: configurable (default 7 days)
 *  - Claims must include: sub (username), roles (Set<String>), iat, exp, type ("access"/"refresh")
 *  - Tokens must be verifiable and parseable
 *  - Expired tokens must throw ExpiredJwtException (from JJWT)
 *  - Tampered tokens must throw JwtException
 *
 * DO NOT hardcode any secret key — accept it via constructor.
 * The secret must be at least 32 characters (256 bits for HS256).
 */
@Service
public class JwtService {

    // TODO: store secretKey, accessTokenDuration, refreshTokenDuration
    // Use io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes())

    public JwtService(String secret, Duration accessTokenDuration, Duration refreshTokenDuration) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("Secret must be at least 32 characters");
        }
        // TODO: initialize
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Generate a signed access token for the given user.
     *
     * @param username non-null username
     * @param roles    set of role strings (e.g. "ROLE_USER", "ROLE_ADMIN")
     * @return compact JWT string
     */
    public String generateAccessToken(String username, Set<String> roles) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Generate a signed refresh token.
     * Refresh tokens contain only: sub, iat, exp, type="refresh"
     * (no roles — refresh tokens are used only to get new access tokens)
     */
    public String generateRefreshToken(String username) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Validate token signature and expiry.
     *
     * @return true if valid; false if signature invalid
     * @throws io.jsonwebtoken.ExpiredJwtException if token is expired but otherwise valid
     */
    public boolean isValid(String token) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Extract the username (subject) from a token.
     * Token must be valid (call isValid() first).
     */
    public String extractUsername(String token) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Extract roles claim from an access token.
     *
     * @return set of role strings
     * @throws IllegalArgumentException if this is a refresh token (type != "access")
     */
    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Return true if the token is a refresh token (type == "refresh").
     */
    public boolean isRefreshToken(String token) {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Extract the expiration date from a token.
     */
    public Date extractExpiration(String token) {
        throw new UnsupportedOperationException("Not implemented");
    }

    // ── helper ───────────────────────────────────────────────────────────────

    private Claims extractAllClaims(String token) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
