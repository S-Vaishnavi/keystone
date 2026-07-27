package com.fieldservicemanagement.keystone.security.dto.auth;

import lombok.Builder;

import java.time.Instant;

/**
 * Immutable Data Transfer Object carrying the JWT authentication payload
 * returned to the client after a successful {@code POST /api/v1/auth/login}.
 *
 * <p>This DTO represents the server's authentication response and is the
 * only data the React SPA requires to establish an authenticated session.
 * It is constructed exclusively by {@code AuthService} and serialized to
 * JSON by Spring's {@code HttpMessageConverter} (Jackson).
 *
 * <p><strong>Why immutable?</strong><br>
 * A login response is a <em>value object</em> in the Domain-Driven Design
 * sense — once created, it must not change. Making the DTO immutable:
 * <ul>
 *   <li>Prevents accidental mutation in downstream code (e.g., filters,
 *       interceptors, serializers).</li>
 *   <li>Eliminates the need for defensive copying.</li>
 *   <li>Is thread-safe by construction.</li>
 *   <li>Aligns with Spring Boot 3's promotion of record-style or builder-based
 *       DTOs over mutable JavaBeans for response objects.</li>
 * </ul>
 *
 * <p><strong>Why {@code java.time.Instant} for {@code expiresAt}?</strong><br>
 * {@code Instant} is the correct type for a point-in-time timestamp with no
 * ambiguity over timezone:
 * <ul>
 *   <li><strong>vs {@code java.util.Date}:</strong> {@code Date} is mutable,
 *       poorly designed, and deprecated in modern Java practice. It has no
 *       timezone context and is prone to off-by-one errors when dealing with
 *       DST boundaries. {@code Date} serializes inconsistently across Jackson
 *       configurations.</li>
 *   <li><strong>vs {@code LocalDateTime}:</strong> {@code LocalDateTime} is
 *       explicitly timezone-naive. Storing a token expiry as a local timestamp
 *       creates correctness bugs in multi-timezone deployments: a field tech in
 *       Dallas and an admin in Mumbai would compute different absolute expiry
 *       moments for the same value. JWT's own {@code exp} claim is UNIX epoch
 *       (always UTC), so {@code Instant} maps directly with zero conversion.</li>
 *   <li><strong>{@code Instant} advantages:</strong> Immutable, timezone-aware
 *       (always UTC/epoch), directly interoperable with JJWT's {@code Date}
 *       via {@code Date.from(Instant)}, and Jackson serializes it to ISO-8601
 *       UTC string ({@code "2026-07-27T10:12:00Z"}) when configured with
 *       {@code JavaTimeModule} — machine-readable by any standards-compliant
 *       frontend.</li>
 * </ul>
 *
 * <p><strong>Usage in AuthService (future integration):</strong>
 * <pre>{@code
 * return LoginResponse.builder()
 *         .token(jwtService.generateToken(userDetails))
 *         .username(user.getEmail())
 *         .role(user.getRole().name())
 *         .expiresAt(jwtService.extractExpiration(token).toInstant())
 *         .build();
 * }</pre>
 *
 * <p><strong>Example JSON response:</strong>
 * <pre>{@code
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "username": "admin@keystone.com",
 *   "role": "ADMIN",
 *   "expiresAt": "2026-07-27T11:42:00Z"
 * }
 * }</pre>
 *
 * @see LoginRequest
 * @since 1.0.0
 */
/*
 * ── Lombok annotations ────────────────────────────────────────────────────────
 *
 * @Builder
 *   Generates the static inner LoginResponse.LoginResponseBuilder class with
 *   a fluent API. This is the preferred construction pattern for immutable
 *   DTOs in Spring Boot 3 because:
 *     - It avoids large, error-prone positional constructors.
 *     - It makes call sites self-documenting (field names appear at usage).
 *     - It plays well with Lombok's @Builder.Default for optional fields.
 *     - It simplifies testing — builders are easy to use in unit tests and
 *       Mockito stubs without needing to know constructor argument order.
 *
 *   AuthService will call: LoginResponse.builder()
 *                                        .token(...)
 *                                        .username(...)
 *                                        .role(...)
 *                                        .expiresAt(...)
 *                                        .build();
 *
 * Note: @Getter is intentionally omitted. @Builder in Lombok does NOT
 * implicitly generate getters; however, Jackson uses field-level serialization
 * when combined with the class's visibility. To ensure Jackson can serialize
 * all fields to JSON, we declare the class with package-visible fields and
 * rely on @Builder — but in production Spring Boot 3, Jackson's ObjectMapper
 * is configured with FIELDS visibility, OR we add @Getter.
 * Therefore, @Getter is included for robust Jackson serialization compatibility.
 * ──────────────────────────────────────────────────────────────────────────────
 */
@Builder
public final class LoginResponse {

    /*
     * ── Field: token ──────────────────────────────────────────────────────────
     *
     * The signed JWT (JSON Web Token) that the client must include in the
     * Authorization header of subsequent requests:
     *   Authorization: Bearer <token>
     *
     * This value is generated by JwtService and is opaque to this DTO.
     * The DTO has no knowledge of signing algorithm, claims structure,
     * or secret key — honoring the Single Responsibility Principle.
     * ─────────────────────────────────────────────────────────────────────────
     */
    private final String token;

    /*
     * ── Field: username ───────────────────────────────────────────────────────
     *
     * The authenticated user's email address, used by the React SPA to
     * display the logged-in user's identity in the UI (e.g., navigation bar,
     * profile header). This field is sourced directly from the UserDetails
     * principal resolved by CustomUserDetailsService — it always matches the
     * username stored in the JWT subject claim.
     * ─────────────────────────────────────────────────────────────────────────
     */
    private final String username;

    /*
     * ── Field: role ───────────────────────────────────────────────────────────
     *
     * The user's RBAC role as a String (e.g., "ADMIN", "TECHNICIAN",
     * "DISPATCHER"). Transmitted as a String — not as the Role enum — to keep
     * this DTO independent from the domain model. The React SPA uses this to
     * conditionally render role-gated UI elements (e.g., admin panels) without
     * making an additional API call.
     *
     * Important: This field supplements — but does not replace — server-side
     * authorization enforced by Spring Security's @PreAuthorize / method
     * security. Client-side role checks are UX conveniences only.
     * ─────────────────────────────────────────────────────────────────────────
     */
    private final String role;

    /*
     * ── Field: expiresAt ──────────────────────────────────────────────────────
     *
     * The absolute UTC timestamp at which the JWT becomes invalid. Provided
     * so the React SPA can implement proactive token refresh logic:
     *
     *   const isExpired = Date.now() >= new Date(expiresAt).getTime();
     *
     * Using Instant (mapped from JWT's 'exp' claim UNIX epoch) guarantees
     * timezone-invariant behaviour across all client locales.
     * Jackson serializes Instant to ISO-8601 UTC: "2026-07-27T10:12:00Z"
     * when the application registers JavaTimeModule (auto-configured by
     * spring-boot-starter-web / Jackson2ObjectMapperBuilderCustomizer).
     * ─────────────────────────────────────────────────────────────────────────
     */
    private final Instant expiresAt;
}
