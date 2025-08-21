## Milestone 4 – REST Controllers | API Endpoints

### General Tasks
- [ ] **Base REST conventions**
    
    - Use `@RestController` for JSON endpoints.
    - Prefix endpoints with `/api/v1/...` (versioning strategy).
    - Use `@RequestMapping` at class-level for grouping endpoints (e.g. `/api/v1/users`).
- [ ] **DTO usage**
    
    - Ensure controllers only expose DTOs (never entities).
    - Define request and response DTOs for each resource.
    - Validate inputs with `@Valid` and return `400 Bad Request` on errors.
- [ ] **HTTP semantics**
    
    - Map CRUD operations to correct HTTP verbs (`POST`, `GET`, `PUT`, `DELETE`).
    - Return appropriate status codes (`201 Created`, `200 OK`, `404 Not Found`, `400 Bad Request`, `204 No Content`).
    - Add `Location` header on resource creation.
- [ ] **Error handling**
    
    - Plan to implement a `@ControllerAdvice` for standardized error responses (later).
    - Ensure controllers throw meaningful exceptions to be translated.
- [ ] **TDD approach**
    
    - For each controller, write `@WebMvcTest`\-based tests with `MockMvc`.
        
    - Mock the service layer (no DB access).
        
    - Write tests before implementation:
        
        - Happy paths (correct inputs, expected success).
        - Failure cases (validation errors, missing resources, unauthorized access).
    - Validate JSON serialization/deserialization.
        
- [ ] **API Documentation**
    
    - Annotate endpoints with Swagger/OpenAPI annotations.
    - Ensure request/response schemas are documented.

---

### Controllers
- [ ] UserController – (Manage users (registration, retrieval))
    - **TDD - Unit Tests First**: Write tests for:
        - POST valid user → `201 Created` + correct JSON body.
        - POST invalid user → `400 Bad Request` + validation messages.
        - GET existing user → `200 OK` + JSON.
        - GET non-existing user → `404 Not Found`.    
    - Define base mapping: `@RequestMapping("/api/v1/users")`.
    - Endpoints:
        \* `GET /{id}` → find user by id (`UserResponseDTO`).  
        \* `POST /` → create user (accept `UserCreateDTO`, return `201 Created` + `UserResponseDTO`).  
        \* `GET /` (later optional) → list users with pagination (`?page=0&size=10`).
    - Integrate `UserService` (mocked in tests).
    - Ensure `POST` uses validation (`@Valid`).
    - Exceptions: Return proper status codes (`404` if user not found).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: `GET /api/v1/users/{id}` (authorized) → `200 OK`, JSON body matches `UserResponseDTO`.
        - Positive: `POST /api/v1/users` with valid `UserCreateDTO` → `201 Created`, `Location` header exact value, body has generated id.
        - Positive: `GET /api/v1/users?page=0&size=2` → `200 OK`, pagination metadata present (if used), array size as expected.
        - Negative: `GET /api/v1/users/{id}` for non-existing user → `404 Not Found`, error payload schema.
        - Negative: `POST /api/v1/users` with invalid email/password → `400 Bad Request`, constraint messages.
        - Negative: `POST /api/v1/users` duplicate email → `409 Conflict`, error code/message.
        - Negative: Access another user’s resource without proper role/ownership → `403 Forbidden`.
        - Negative: Wrong `Accept` or `Content-Type` (e.g., `text/plain`) → `415 Unsupported Media Type` or `406 Not Acceptable`.
        
- [ ] AuthController – ( Authentication and registration flow (login, register, refresh tokens))
    - **TDD - Unit Tests First**: Write tests for:
        \* POST /login with valid credentials → `200 OK` + token.  
        \* POST /login with invalid credentials → `401 Unauthorized`.  
        \* POST /register valid → `201 Created` + token.  
        \* POST /register invalid → `400 Bad Request`. \* Define base mapping: `@RequestMapping("/api/v1/auth")`.    
    - Endpoints:
        \* `POST /login` → accept `LoginRequest`, return `AuthenticationResponse` (JWT token).  
        \* `POST /register` → accept `RegisterRequest`, return `AuthenticationResponse` (auto-login after registration).
    - Integrate `AuthService` (mocked in tests).
    - Exceptions: Handle authentication failures → return `401 Unauthorized`.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: `POST /api/v1/auth/login` with valid credentials → `200 OK`, JSON contains JWT, token type, expiry.
        - Positive: `POST /api/v1/auth/register` with valid payload → `201 Created`, JSON contains JWT and user info, `Location` header points to `/api/v1/users/{id}`.
        - Positive: `POST /api/v1/auth/refresh` with valid refresh token → `200 OK`, new JWT returned.
        - Negative: Login with wrong password → `401 Unauthorized`, JSON error body shape verified.
        - Negative: Register with duplicate email → `409 Conflict` (or mapped status), error code/message verified.
        - Negative: Missing/invalid fields (email format, password length) → `400 Bad Request`, field-level validation errors.
        - Negative: Refresh with expired/invalid token → `401 Unauthorized` (or `403 Forbidden` depending on policy).
        
- [ ] JumpController – (CRUD for jumps)
    - **TDD - Unit Tests First**: Write tests for:
        - POST valid jump → `201 Created` + `Location` header + body.
        - POST invalid jump → `400 Bad Request`.
        - GET existing → `200 OK`.
        - GET missing → `404 Not Found`.
        - PUT existing → `200 OK` + updated object.
        - DELETE existing → `204 No Content`.    
    - Define base mapping: `@RequestMapping("/api/v1/jumps")`.
    - Endpoints:
        - `POST /` → create jump (`JumpCreateDTO` → `JumpResponseDTO`).
        - `GET /{id}` → get jump by ID.
        - `GET /` → list jumps (optional: support pagination, filtering).
        - `DELETE /{id}` → delete jump.
    - Integrate `JumpService` (mocked in tests).
    - Exceptions: Return `201` on create, `404` if not found, `204 No Content` on delete.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: `POST /api/v1/jumps` with valid `JumpCreateDTO` → `201 Created`, `Location` header `/api/v1/jumps/{id}`, body equals `JumpResponseDTO`.
        - Positive: `GET /api/v1/jumps/{id}` (owned or authorized) → `200 OK`, JSON structure and date/time format verified.
        - Positive: `GET /api/v1/jumps?userId=...&page=0&size=10` → `200 OK`, filtered + paginated results.
        - Positive: `PUT /api/v1/jumps/{id}` with valid update → `200 OK`, body reflects updates.
        - Positive: `DELETE /api/v1/jumps/{id}` (authorized) → `204 No Content`.
        - Negative: `POST` with invalid fields (altitude <= 0, missing date, invalid foreign keys) → `400 Bad Request`.
        - Negative: `GET /{id}` non-existing → `404 Not Found`.
        - Negative: `PUT /{id}` when path id ≠ body id (if body carries id) → `400 Bad Request`.
        - Negative: `DELETE /{id}` for a jump owned by another user → `403 Forbidden`.
        - Negative: Unauthenticated access to protected endpoints → `401 Unauthorized`.
        
- [ ] LookupController – (Provide reference data (jump types, aircraft, dropzones).)
    - **TDD - Unit Tests First**: Write tests for:
        - GET /jumptypes returns array with correct fields.
        - GET /aircraft returns array with correct fields.
        - GET /dropzones returns array with correct fields.
        - Ensure no entity leakage (DTO-only).    
    - Define base mapping: `@RequestMapping("/api/v1/lookups")`.
    - Endpoints:
        - `GET /jumptypes` → list of jump type DTOs.
        - `GET /aircraft` → list of aircraft DTOs.
        - `GET /dropzones` → list of dropzone DTOs.
    - Integrate `LookupService` (mocked in tests).
    - Ensure lightweight, read-only endpoints.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: `GET /api/v1/lookups/aircraft` → `200 OK`, array of `AircraftLookupDTO`, stable ordering (if specified).
        - Positive: `GET /api/v1/lookups/dropzones` → `200 OK`, array of `DropzoneLookupDTO`, fields `id`, `name` (and `location` if included).
        - Positive: `GET /api/v1/lookups/jumptypes` → `200 OK`, array of `JumptypeLookupDTO`.
        - Positive: Empty datasets → `200 OK`, empty arrays (not `null`).
        - Negative: Unsupported `Accept` header → `406 Not Acceptable`.
        - Negative: Security rules (if any) incorrectly accessing protected lookup → expected status enforced (usually public → `200`, otherwise `401/403`).
