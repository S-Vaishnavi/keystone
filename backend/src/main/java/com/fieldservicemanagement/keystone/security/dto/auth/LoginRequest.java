package com.fieldservicemanagement.keystone.security.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Data Transfer Object representing the JSON payload sent by the client
 * during a login request to {@code POST /api/v1/auth/login}.
 *
 * <p>This DTO serves as the boundary between the HTTP transport layer
 * and the application's authentication logic. It carries only the data
 * required to authenticate a user — nothing more, nothing less — and is
 * validated by the Jakarta Bean Validation framework before the request
 * ever reaches {@code AuthService}.
 *
 * <p><strong>Design intent:</strong>
 * <ul>
 *   <li>Keeps entity classes ({@code User}) decoupled from the HTTP API surface.</li>
 *   <li>Ensures invalid payloads are rejected at the controller boundary, preventing
 *       unnecessary service and database calls.</li>
 *   <li>Exposes no internal implementation details (e.g., password hashing strategy)
 *       to API consumers or log aggregators.</li>
 * </ul>
 *
 * <p><strong>Usage:</strong>
 * <pre>{@code
 * // AuthController.java
 * @PostMapping("/login")
 * public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
 *     return ResponseEntity.ok(authService.login(request));
 * }
 * }</pre>
 *
 * <p><strong>Example JSON payload:</strong>
 * <pre>{@code
 * {
 *   "email": "admin@keystone.com",
 *   "password": "SecureP@ssw0rd!"
 * }
 * }</pre>
 *
 * @see LoginResponse
 * @since 1.0.0
 */
/*
 * ── Lombok annotations ────────────────────────────────────────────────────────
 *
 * @Getter
 *   Generates a public getEmail() and getPassword() at compile time.
 *   Spring's HttpMessageConverter (Jackson) relies on these getters during
 *   deserialization of the incoming JSON body. Without getters the Jakarta
 *   validation engine cannot read field values for constraint evaluation.
 *
 * @NoArgsConstructor
 *   Generates a public no-argument constructor required by Jackson to
 *   instantiate the DTO before populating its fields via reflection.
 *   Without this, Jackson throws a MismatchedInputException.
 *
 * @ToString(exclude = "password")
 *   Generates a human-readable toString() for structured logging.
 *   The password field is explicitly excluded to guarantee that raw
 *   credentials NEVER appear in application logs, stack traces, or
 *   serialized audit records — regardless of the logging configuration.
 *   Omitting this exclusion is a frequent, high-severity security mistake
 *   made by junior developers.
 * ──────────────────────────────────────────────────────────────────────────────
 */
@Getter
@NoArgsConstructor
@ToString(exclude = "password")
public final class LoginRequest {

    /*
     * ── Field: email ──────────────────────────────────────────────────────────
     *
     * @NotBlank
     *   Validates that the field value is not null AND not empty after trimming
     *   whitespace. This is a stricter version of @NotNull — it rejects strings
     *   like "", " ", "\t". Sourced from jakarta.validation.constraints.
     *   The 'message' attribute provides a developer-friendly, client-safe error
     *   message returned in the 400 Bad Request response body.
     *
     * @Email
     *   Validates the field value against RFC 5321-compliant email address syntax.
     *   Spring's validator delegates to the Hibernate Validator implementation
     *   under the hood. Using @Email instead of a hand-written @Pattern regex
     *   reduces maintenance burden and avoids common regex pitfalls (e.g.,
     *   catastrophic backtracking).
     * ─────────────────────────────────────────────────────────────────────────
     */
    @NotBlank(message = "Email address is required")
    @Email(message = "Email address must be a valid format")
    private String email;

    /*
     * ── Field: password ───────────────────────────────────────────────────────
     *
     * @NotBlank
     *   Ensures the password field is present and non-empty in the request body.
     *   No additional constraints (e.g., @Size, @Pattern) are placed here
     *   intentionally — those rules belong to the REGISTRATION flow, not login.
     *   Applying length or pattern constraints at login would expose information
     *   about password policy rules to an attacker (information leakage).
     *
     *   NOTE: The field type remains String. Converting to char[] at the DTO
     *   level offers marginal JVM security benefit in modern JVMs with compressed
     *   string optimizations and is not standard practice in Spring Boot DTOs.
     *   Password hashing (BCrypt) in AuthService renders the plain-text value
     *   transient.
     * ─────────────────────────────────────────────────────────────────────────
     */
    @NotBlank(message = "Password is required")
    private String password;
}
