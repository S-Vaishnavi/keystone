package com.fieldservicemanagement.keystone.security;

import com.fieldservicemanagement.keystone.domain.User;
import com.fieldservicemanagement.keystone.domain.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "bXlTdXBlclNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlcw==";
    private static final long EXPIRATION_MS = 3600000;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
    }

    @Test
    void generateToken_shouldReturnNonBlankJwt() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@keystone.io")
                .passwordHash("hashed")
                .role(Role.MANAGER)
                .build();

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnEmailFromToken() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@keystone.io")
                .role(Role.MANAGER)
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("admin@keystone.io");
    }

    @Test
    void extractRole_shouldReturnRoleFromToken() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("tech@keystone.io")
                .role(Role.TECHNICIAN)
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractRole(token)).isEqualTo(Role.TECHNICIAN);
    }

    @Test
    void extractExpiration_shouldReturnFutureInstant() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@keystone.io")
                .role(Role.MANAGER)
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractExpiration(token)).isAfter(Instant.now());
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@keystone.io")
                .role(Role.MANAGER)
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() throws InterruptedException {
        JwtService shortLivedJwtService = new JwtService(SECRET, 1);
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@keystone.io")
                .role(Role.MANAGER)
                .build();

        String token = shortLivedJwtService.generateToken(user);
        
        Thread.sleep(50);

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForTamperedToken() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@keystone.io")
                .role(Role.MANAGER)
                .build();

        String token = jwtService.generateToken(user);
        
        String tamperedToken = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

        assertThat(jwtService.isTokenValid(tamperedToken)).isFalse();
    }

    @Test
    void isTokenValid_shouldReturnFalseForTokenSignedWithDifferentKey() {
        JwtService differentKeyJwtService = new JwtService("YW5vdGhlclN1cGVyU2VjcmV0S2V5Rm9yRGlmZmVyZW50U2lnbmF0dXJl", EXPIRATION_MS);
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("admin@keystone.io")
                .role(Role.MANAGER)
                .build();

        String token = differentKeyJwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }
}
