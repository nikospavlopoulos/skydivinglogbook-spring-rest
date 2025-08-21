## Milestone 3 – Authentication – Security

### dto Layer – Authentication
- [ ] Define DTOs as **immutable records** (Java `record`) or simple POJOs with validation annotations to ensure clean input/output data.
- [ ] Apply **Jakarta Bean Validation** annotations (`@NotBlank`, `@Email`, `@Size`, etc.) to fields for automatic validation at the controller level.
- [ ] Ensure DTOs **match the REST API’s payloads** (e.g., JSON request bodies for login and register, JSON responses for authentication).
- [ ] Align naming with REST conventions (short, clear, domain-specific).
- [ ] Maintain **DTO-to-Entity and Entity-to-DTO mappers** (MapStruct or manual mapping in `AuthenticationService`).
- [ ] Write unit tests for serialization and validation.

#### Classes:
- [ ] LoginRequest (payload for logging into the system)
    - **TDD - Unit Tests First**: Write tests for: - Missing/empty values. - Proper JSON serialization/deserialization.
    - Define fields: \* `String usernameOrEmail` (allow flexibility in login, not just username). \* `String password`.
    - Add validation: `@NotBlank` on both fields. Possibly `@Email` if you enforce login only via email.
    - Ensure JSON compatibility
    - Ensure integration with `AuthenticationService` → pass into the authentication manager.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid username/password passes validation.
        - Negative: Missing username → validation error. Empty password → validation error.
        
- [ ] RegisterRequest (payload for creating a new user account) 
    - **TDD - Unit Tests First**: Write tests for: - validation for short/empty/invalid values. - JSON matches expected request format. - Verifying correct mapping to entity (mocking the repository).
    - Define fields: \* `String username`. \* `String email`. \* `String password`.
        - (Optional) `String confirmPassword` — if you want backend validation.
        - (Optional) `String role` — default to `USER` if not provided.
    - Add validation: \* `@NotBlank` for all required fields. \* `@Email` for email. \* `@Size(min=8)` for password.
    - Ensure JSON compatibility
    - Map `RegisterRequest` → `User` entity in `AuthenticationService`.
    - Write test verifying correct mapping to entity (mocking the repository).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid user registration passes.
        - Negative: Invalid email or short password → fails. Duplicate user → exception.
        
- [ ] AuthenticationResponse (payload sent back to the client after successful authentication)
    - **TDD - Unit Tests First**: Write tests for: - Validate fields are correctly serialized. - Ensure immutability (record or final fields). - Ensuring expired/invalid tokens don’t produce a valid responseE
    - Define fields: \* `String token` (JWT). \* `String username`. \* `String role`.
    - (Optional) `Instant expirationTime` — if you want to expose expiry.
    - Ensure JSON compatibility
    - Confirm integration with `JwtService` and `AuthenticationService`.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: JWT returned after login. Username/role populated.
        - Negative: Null/expired token shouldn’t be accepted. Serialization errors handled.
        
---

### Authentication & Security
- [ ] Define **user domain model** (e.g., `User`, `Role` enum, `Authority` if needed).
- [ ] Decide on **JWT-based authentication flow** (login → JWT issued → secured endpoints).
- [ ] Establish **Spring Security configuration** with stateless session handling.
- [ ] Implement **global exception handling** for authentication/security errors (401, 403).
- [ ] Add proper **exception handling** (invalid credentials, unauthorized access, expired token, etc.) with global `@ControllerAdvice`.
- [ ] Hash passwords with BCrypt.
- [ ] Configure **security filters chain** (JWT filter).
- [ ] Write unit + integration tests.
    - Unit tests for each service (`CustomUserDetailsService`, `JwtService`, `AuthenticationService`).
    - MockMvc tests for endpoints (`/auth/login`, `/auth/register`, `/auth/refresh`).
    - Edge case tests (invalid credentials, expired token, unauthorized access).

#### Authentication:
- [ ] CustomUserDetailsService
    - **TDD - Unit Tests First**: Write tests for finding existing user by username. Handling `user not found` exception. Mapping roles/authorities correctly.
    - Implement `UserDetailsService` to load users from DB.
    - Map domain `User` entity → Spring Security `UserDetails`.
    - Handle cases where user is not found → throw `UsernameNotFoundException`.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Existing user retrieved. Roles mapped.
        - Negative: Unknown user → UsernameNotFoundException.        
        - Positive: Existing user loaded correctly with roles.
        - Negative: Unknown username triggers `UsernameNotFoundException`.
        
- [ ] AuthenticationService
    - **TDD - Unit Tests First**: Write tests for:
        - `register`: Hashing works. Duplicate username throws exception.
        - `authenticate`: Valid login returns token. Invalid login rejects access.
        - `refreshToken`: Valid refresh returns new token. Expired/invalid refresh token throws exception.
    - Implement `register` logic (save user with hashed password, assign default role).
    - Implement `authenticate` logic (validate user, generate JWT).
    - Implement `refreshToken` logic if you want refresh support.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Register new user → hashed password stored. Authenticate valid credentials returns JWT.
        - Negative: Duplicate registration → EntityAlreadyExistsException. Invalid credentials → 401 Unauthorized.        
        - Positive: Valid login returns JWT.
        - Positive: Register persists user with encoded password.
        - Negative: Wrong credentials → `401 Unauthorized`.
        - Negative: Duplicate email → `EntityAlreadyExistsException`.
        
- [ ] JwtAuthenticationFilter
    - **TDD - Unit Tests First**: Write tests for: - filter extracts token from `Authorization` header. - filter rejects missing/invalid token. -filter sets authentication context properly.
    - Extend `OncePerRequestFilter`.
    - Extract JWT from request headers.
    - Validate token → set authentication in SecurityContext.
    - Skip filter for public endpoints (like `/auth/login`).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid token → authentication context set. Public endpoints bypass filter.
        - Negative: Missing/invalid token → request rejected.        
        - Positive: Valid JWT attaches Authentication to SecurityContext.
        - Positive: Missing token on public endpoint passes through.
        - Negative: Invalid/expired JWT → request rejected with `401`.

---

### Security
- [ ] JwtService – token generation/validation, tests
    - **TDD - Unit Tests First**: Write tests for:
        - `generateToken` returns valid JWT.
        - `validateToken` passes for valid token.
        - `validateToken` fails for expired or tampered token.
        - Extracting roles and username from JWT.
    - Generate JWT for authenticated users.
    - Parse and validate JWT tokens.
    - Extract claims (username, roles).
    - Handle expiration and invalid signatures.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Generate valid token. Validate correct token succeeds.
        - Negative: Expired/tampered token → validation fails.        
        - Positive: Generate JWT with correct subject and claims.
        - Positive: Parse valid token → correct username.
        - Negative: Expired/tampered token → validation fails.
        
- [ ] SecurityConfiguration – stateless setup, tests
    - **TDD - Unit Tests First**: Write tests - Integration tests with MockMvc:
        - Public endpoint accessible without token.
        - Protected endpoint requires token.
        - Token authentication works for authorized user.
        - Unauthorized user is rejected.
    - Define `SecurityFilterChain`.
    - Configure:
        - Stateless session management.
        - Public endpoints (`/auth/**`).
        - Protected endpoints (e.g., `/jumps/**`).
        - Attach `JwtAuthenticationFilter`.
    - Register password encoder bean.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Public endpoints accessible without token. Authenticated requests to protected endpoints succeed.
        - Negative: Missing token → 401. Unauthorized role → 403.        
        - Positive: Public endpoints accessible without token.
        - Positive: Authenticated requests to protected endpoints succeed.
        - Negative: Unauthorized role → `403`.
        - Negative: No token → `401`.
        
- [ ] CustomAuthenticationEntryPoint – 401 handler
    - **TDD - Unit Tests First**: Write tests for: - Test response format for unauthorized request. - Test correct status code returned.
    - Handle unauthorized access (401).
    - Return JSON error response instead of HTML login page.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Unauthorized request → 401 JSON error.
        - Negative: Null auth context handled gracefully.        
        - Positive: Access protected resource without login → `401 JSON error`.
        - Negative: Null auth header handled without crash.
        
- [ ] CustomAccessDeniedHandler – 403 handler
    - **TDD - Unit Tests First**: Write tests for: - Test response format for forbidden request. - Test correct status code returned.
    - Handle access denied errors (403).
    - Return JSON error response.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Forbidden request → 403 JSON error.
        - Negative: Ensure consistent error format across all handlers.        
        - Positive: User with insufficient role denied → `403`.
        - Negative: Verify response matches API error schema.

