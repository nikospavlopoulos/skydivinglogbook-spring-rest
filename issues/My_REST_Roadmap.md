## Roadmap - Plan | REST API - Spring Boot - Java | Skydiving Logbook

Domain stays the same: users register/authenticate, log jumps with details (date, type, dropzone, aircraft, altitude, freefall time, notes), and query their logs.  
Pure REST API: Clients (like a future frontend or mobile app) will hit endpoints like /api/jumps with JSON payloads.  
I'll reuse concepts from the old repo (entities like Jump/User, static data like Aircraft/Dropzone/Jumptype), but build everything from scratch in a new project.

I will emphasize test-driven development (TDD), where I write tests before the actual code. For the REST part, I will design it to be stateless, using HTTP methods properly (GET for reads, POST for creates, etc.) and JSON for data exchange.

##### Key Concepts to Cover

- **REST Design**: APIs should be resource-oriented (e.g., /users, /jumps/{id}), use standard HTTP status codes (200 OK, 404 Not Found), support query params for filtering/pagination, and be versioned (e.g., /api/v1/...). It's stateless—no server-side sessions; use JWT or basic auth for security
- **Testing Strategies**: JUnit 5 for the framework, Mockito for mocking dependencies. Unit tests: Isolate components (e.g., mock repo in service tests). Integration tests: Test full flows (e.g., controller + service + repo with an in-memory DB like H2). Aim for 80%+ coverage. Use JaCoCo to measure.
- **DTO Mapping**: Implement DTOs (Data Transfer Objects) as lightweight classes for API input/output. Use of libraries like MapStruct for auto-mapping. Never expose JPA(Java Persistence API) entities in my REST API.
- **Validation**: Use Jakarta Bean Validation (e.g., @NotNull, @Size) on DTOs to check inputs automatically.
- **Exception Handling**: Centralize with @ControllerAdvice to catch errors globally, returning JSON error responses (e.g., { "error": "Invalid input", "details": \[...\] }) with proper HTTP codes.
- **Repository/Service Layers**: Repositories to handle Database CRUD operations via Spring Data JPA. Services to add business logic (e.g., auth checks, calculations like total jumps). This follows separation of concerns (SOLID's Single Responsibility Principle).

##### Target architecture (packages)

- `rest` (REST controllers)
- `service` (interfaces + implementations)
- `repository` (Spring Data JPA)
- `model` (entities)
- `dto` (request + response shapes)
- `mapper` (manual or MapStruct later)
- `validation` (custom constraints if needed)
- `core` -> `exceptions` `filters` (domain + HTTP exception mapping + filters)
- `configuration` (Spring config, CORS, Jackson tweaks)
- `security` (security filter chain for the API)
- `authentication` (custom authentication logig)
- `test` (unit, slice, integration, utilities)

##### Draft API Resources

Versioned base path: `/api/v1`

- **Lookups** (read-only):
    
    - `GET /lookups/jumptypes`
    - `GET /lookups/aircraft`
    - `GET /lookups/dropzones`
- **Jumps**
    
    - `GET /jumps?page=&size=&sort=` (paginated, filterable later)
    - `GET /jumps/{id}`
    - `POST /jumps` (create)
    - `DELETE /jumps/{id}`
- **Users**
    
    - `POST /users` (register)
    - `GET /users/{id}` (admin or self; stretch)

* * *

### Milestone 0: Project Setup and Initial Configuration

- New Spring Boot Project(Gradle) `com.nikospavlopoulos.skydivingrest`
- Dependencies: Spring Web, Spring Data, mySQL Database, Lombok, Spring Boot Starter Validation, Spring Boot Starter Test, MockMvc, JSONPath, JaCoCo, Swagger/OpenAPI, Spring Security, JSONWebToken
- New GitHub Repository `skydiving-logbook-spring-rest`
- New mySQL Database `skydivingrest1`
- Configure Application Properties: Database Connection & Settings, Logging Level
- Initial Test: "Smoke Testing" Verifying Setup - Ensure App Boots

* * *

### Milestone 1: Domain Model (Entities) - Repositories - DTO

#### **`model` (Entities Layer)**

###### General Tasks

- Define all domain entities as JPA `@Entity` classes.
- Implement common base entity logic in `AbstractEntity` (e.g., `id`, timestamps, UUID).
- Ensure all relationships (OneToMany, ManyToOne, etc.) are mapped consistently.
- Add validation annotations (e.g., `@NotNull`, `@Size`) where appropriate.

###### Classes

1.  **AbstractEntity**
    
    - **TDD - Unit Tests First**: Write unit tests verifying lifecycle hooks populate values correctly.
    - Implement a mapped superclass with shared fields (`id`, `uuid`, `createdAt`, `updatedAt`).
    - Add lifecycle hooks (`@PrePersist`, `@PreUpdate`).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Auto-generate id and uuid correctly on persist. createdAt populated on insert. updatedAt changes on update.
        - Negative: Attempt to persist entity without required fields → validation error. Manually set createdAt/updatedAt ignored.
        - Positive: ID auto-increment works, UUID generated uniquely per entity.
        - Positive: `equals` and `hashCode` behave consistently (compare persisted vs transient entity).
        - Negative: Setting `null` values for annotated non-null fields → JPA validation fails.
        - Negative: Updating entity without modifying fields → `updatedAt` should remain unchanged.
2.  **Jump**
    
    - **TDD - Unit Tests First**: Write entity-level tests validating relationship mapping and constraints.
    - Define entity fields (date, altitude, equipment, references to user, aircraft, dropzone, jumptype).
    - Establish relationships with `User`, `Aircraft`, `Dropzone`, and `Jumptype`.
    - Add validation (e.g., altitude > 0).
    - Test Ideas - Positive/Negative Scenarios: {///add_here_test_ideas_positive_negative_scenarios}
        
        - Positive: Valid jump persists with references to User/Aircraft/Dropzone/Jumptype. Altitude > 0 accepted.
        - Negative: Missing relationships (null User or Aircraft) → constraint violation. Altitude ≤ 0 → validation error.
        
        - Positive: Can persist with valid altitude, date, references to Dropzone, Aircraft, User.
        - Positive: Lazy load user/jumptype works when accessed in service layer.
        - Negative: Altitude zero or negative → `ConstraintViolationException`.
        - Negative: Missing relation (no user assigned) → persistence error.
3.  **User**
    
    - **TDD - Unit Tests First**: Test persistence and uniqueness validation for users.
    - Define basic user attributes (firstname, lastname, email, etc.).
    - Define relationship with `Jump` (one-to-many).
    - Add uniqueness constraint for email.
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Unique email persists correctly. User–Jump one-to-many relation works (cascading fetch).
        - Negative: Duplicate email → persistence exception. Null/invalid email → validation fails.
        
        - Positive: Can persist with valid altitude, date, references to Dropzone, Aircraft, User.
        - Positive: Lazy load user/jumptype works when accessed in service layer.
        - Negative: Altitude zero or negative → `ConstraintViolationException`.
        - Negative: Missing relation (no user assigned) → persistence error.
4.  **Aircraft** (static data)
    
    - **TDD - Unit Tests First**: Test saving and retrieving aircraft entities.
    - Define attributes (model.).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid aircraft model persists. Retrieve by ID works.
        - Negative: Duplicate name/model → exception. Null name → validation failure.
5.  **Dropzone** (static data)
    
    - **TDD - Unit Tests First**: Test persistence and retrieval, plus relationships with `Jump`.
    - Define attributes (name, location).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Persist and retrieve dropzone with valid name/location. Relation with Jump works.
        - Negative: Null name or location → validation error. Attempt to delete when jumps exist → constraint violation.
6.  **Jumptype** (static data)
    
    - Define attributes (type name, description).
    - **TDD - Unit Tests First**: Test persistence and mapping.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid type name persists. Multiple jump types co-exist.
        - Negative: Duplicate type name → exception. Null/blank type name → validation error.

* * *

#### **`repository` (Data Access Layer)**

###### General Tasks

- Define Spring Data JPA repositories for each entity.
- Add custom query methods where needed (e.g., find jumps by user).

###### Classes

1.  **UserRepository**
    
    - **TDD - Unit Tests First**: Write repository integration tests using `@DataJpaTest`.
    - Extend `JpaRepository<User, Long>`.
    - Add query methods (e.g., `findByEmail`).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: findByEmail returns correct user. Save and retrieve user works.
        - Negative: findByEmail on missing user → empty. Attempt to save duplicate email → error.
        
        - Positive: `findByEmail` returns correct Optional.
        - Positive: Saving new User assigns auto-generated ID.
        - Negative: `findByEmail` with non-existent user returns empty.
        - Negative: Attempt to save duplicate email → exception.
2.  **JumpRepository**
    
    - **TDD - Unit Tests First**: Write tests to ensure custom queries return correct results.
    - Extend `JpaRepository<Jump, Long>`.
    - Add query methods (e.g., `findByUserId`, `findByDropzone`).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: findByUserId returns correct jumps. findByDropzone returns expected results.
        - Negative: Query by non-existing user/dropzone returns empty..
        
        - Positive: Query by `userId` returns only jumps for that user.
        - Positive: Query by `dropzone` returns expected results.
        - Negative: Query with invalid `userId` returns empty list.
        - Negative: Attempt to delete non-existent ID → no-op or exception depending on config.
3.  **AircraftRepository**
    
    - **TDD - Unit Tests First**: Write repository tests for aircraft lookups.
    - Extend `JpaRepository<Aircraft, Long>`.
    - Add query methods (e.g., `findByModel`).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Save and retrieve aircraft. Query by model returns correct entity.
        - Negative: Query non-existent model returns empty.
        
        - Positive: Query returns entity when model exists.
        - Negative: Save duplicate model → violates unique constraint.
4.  **DropzoneRepository**
    
    - **TDD - Unit Tests First**: Test persistence and retrieval of dropzones.
    - Extend `JpaRepository<Dropzone, Long>`.
    - Add query methods (e.g., `findByLocation`).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Save and retrieve dropzone. Query by location returns correct set.
        - Negative: Query with unknown location → empty list.
5.  **JumptypeRepository**
    
    - **TDD - Unit Tests First**: Test retrieval and persistence of jump types.
    - Extend `JpaRepository<Jumptype, Long>`.
    - Add query methods
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Save and retrieve jump type. Query returns all jump types.
        - Negative: Saving duplicate jump type fails.

* * *

#### **`dto` (Data Transfer Objects)**

###### General Tasks

- Separate read-only DTOs from insert/update DTOs.
- Define a **dedicated read-only Lookup DTO** for each entity where lightweight representation is needed (e.g., `id`, `name`).
- Use DTOs to decouple API layer from entities.
- Add validation annotations on DTOs.

###### Classes

1.  **JumpInsertDTO**
    
    - **TDD - Unit Tests First**: Write DTO validation tests (e.g., missing required fields should fail).
    - Include fields required for creating a new jump (exclude auto-generated ones like `id`, `createdAt`).
    - Add validation (`@NotNull`, `@Min`).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Valid DTO passes validation. Altitude ≥ 1000 accepted.
        - Negative: Null date or altitude → validation error. Invalid dropzone ID → mapping fails.
        
        - Positive: Validation annotations enforce constraints (e.g., `@Email`).
        - Negative: Blank/invalid inputs → trigger Bean Validation exceptions during controller binding.
2.  **JumpReadOnlyDTO**
    
    - **TDD - Unit Tests First**: Write mapping tests to ensure entity → DTO transformation works correctly.
    - Include fields exposed to clients (readonly).
    - Map from `Jump` entity → DTO using a mapper (e.g., MapStruct or manual).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Correct mapping from Jump entity. All expected fields present in output.
        - Negative: Null entity input to mapper → null-safe response.
        
        - Positive: Mapper produces correct JSON-ready output.
        - Negative: Mapping null entity → test null-safe methods.
3.  **UserInsertDTO**
    
    - **TDD - Unit Tests First**: Test DTO validation rules.
    - Include fields needed for creating a user (e.g., firstname, lastname, email, password).
    - Add validation (`@Email`, `@NotBlank`).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Valid firstname/lastname/email/password passes validation.
        - Negative: Invalid email → validation error. Password too short → fails.
        
        - Positive: Validation annotations enforce constraints (e.g., `@Email`).
        - Negative: Blank/invalid inputs → trigger Bean Validation exceptions during controller binding.
4.  **AircraftLookupDTO**
    
    - **TDD - Unit Tests First**: - Repository has aircraft → list mapped correctly. - Repository empty → returns empty list.
    
    - Define DTO with fields: `id`, `name`.
    - Mapper: convert `Aircraft` entity → `AircraftLookupDTO`.
    - Service responsibility: return **list of all aircraft** in lookup form.
    
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Repository returns entities → mapped to list of DTOs. Correct fields populated.
        - Negative: Empty repository → empty list. Null values in entity handled gracefully.
        
        - Positive: Mappers correctly flatten only `id` + `name`.
        - Negative: Repository returns empty → JSON array `[]`, not `null`.
5.  **DropzoneLookupDTO**
    
    - **TDD - Unit Tests First**: - Dropzones exist → DTOs populated with id + name (+ location). - No dropzones in DB → empty list returned.
    
    - Define DTO with fields: `id`, `name`, `location` (optional if needed for display).
    - Mapper: convert `Dropzone` entity → `DropzoneLookupDTO`.
    - Service responsibility: return **list of all dropzones** in lookup form.
    
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Repository contains dropzones → mapped list returned. Fields (id, name, location) mapped correctly.
        - Negative: Empty DB → empty list. Null location handled.
        
        - Positive: Mappers correctly flatten only `id` + `name`.
        - Negative: Repository returns empty → JSON array `[]`, not `null`.
6.  **JumpTypeLookupDTO**
    
    - **TDD - Unit Tests First**: - Jump types exist → DTOs returned correctly. - Empty DB → empty list returned.
    
    - Define DTO with fields: `id`, `name`.
    - Mapper: convert `JumpType` entity → `JumpTypeLookupDTO`.
    - Service responsibility: return **list of all jump types** (tandem, AFF, freefly, etc.).
    
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Jump types exist → mapped DTOs returned. Correct field mapping.
        - Negative: Empty DB → empty list.
        
        - Positive: Mappers correctly flatten only `id` + `name`.
        - Negative: Repository returns empty → JSON array `[]`, not `null`.

* * *

### Milestone 2: Exceptions - Mapper - Service

#### **`Exceptions`** (under `core`)

###### General Tasks

- Define a **global exception handler** using `@ControllerAdvice` and `@ExceptionHandler` to map exceptions to HTTP responses (`ResponseEntity`).
- Decide consistent error response format (e.g., `{ timestamp, status, error, message, path }`).
- Ensure each existing exception (`EntityNotFoundException`, `EntityAlreadyExistsException`, etc.) maps to the correct HTTP status code (`404`, `409`, `400`, `403`, `500`).
- Refactor exception messages to be API-consumer-friendly (clear, not leaking internal details).

###### Classes

1.  **AppServerException**
    
    - **TDD - Unit Tests First**: Write tests to assert it correctly wraps root causes (e.g., database errors)
    - Define for unexpected server-side failures (maps to 500).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Wraps DB failure into exception. Correct message propagated.
        - Negative: Null cause → still produces valid error message.
        
        - Positive: Wraps technical DB error, global handler returns `500`.
        - Negative: Null cause still produces valid exception object.
2.  **EntityAlreadyExistsException**
    
    - **TDD - Unit Tests First**: Write tests for inheritance structure and generic handling.
    - Use when attempting to create duplicate records (maps to 409 Conflict).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Raised when duplicate insert attempted.
        - Negative: Throw without message → default message tested.
        
        - Positive: Triggered by duplicate create attempt.
        - Negative: Check global handler response consistency (`409` status).
3.  **EntityGenericException**
    
    - **TDD - Unit Tests First**: Write tests for inheritance structure and generic handling.
    - flexible fallback for entity-related issues not covered by other exceptions.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Works as fallback when unexpected entity error occurs.
        - Negative: Ensure inheritance hierarchy doesn’t break global handler.
4.  **EntityInvalidArgumentException**
    
    - **TDD - Unit Tests First**: Write validation tests (e.g., jump number < 0).
    - Use for invalid request data (maps to 400 Bad Request).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Triggered for invalid request (e.g., altitude < 0).
        - Negative: Ensure controller maps exception to 400 Bad Request.
        
        - Positive: Thrown for invalid input parameters in services.
        - Negative: Ensure error message is meaningful (not empty/null).
5.  **EntityNotAuthorizedException**
    
    - **TDD - Unit Tests First**: Write tests ensuring correct exception thrown when a user tries to access another user’s logbook.
    - Use for authorization/ownership errors (maps to 403).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Unauthorized user modifying another’s jump triggers exception.
        - Negative: Ensure no leakage of sensitive info in message.
        
        - Positive: Unauthorized user action mapped to `403`.
        - Negative: Ensure error doesn’t leak internal entity info (e.g., jump UUID).
6.  **EntityNotFoundException**
    
    - **TDD - Unit Tests First**: Write service-level tests that trigger it (e.g., jump ID not found).
    - Use for missing resources (maps to 404).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Missing jump ID triggers exception. Correct 404 returned.
        - Negative: Null entity input doesn’t silently succeed.
        
        - Positive: Query missing entity triggers exception, handled as `404`.
        - Negative: Null entity reference never silently passes.

#### **`GlobalExceptionHandler`** - under `core`

- Create a `@ControllerAdvice` class (e.g., `GlobalExceptionHandler`) to centralize exception → HTTP response mapping.
    
- Define **exception-to-status mappings** for:
    
    - Validation errors → `400 Bad Request`.
    - Entity not found → `404 Not Found`.
    - Entity already exists → `409 Conflict`.
    - Invalid arguments → `400 Bad Request`.
    - Unauthorized/Forbidden → `403 Forbidden`.
    - Server errors → `500 Internal Server Error`.
- Standardize **response payloads**:
    
    - For simple errors: `ResponseMessageDTO` (code, message).
    - For validation errors: `Map<String, String>` (field → error message).
- Add **unit tests** for each exception handler.
    
- Add **integration tests** hitting endpoints that trigger these exceptions.
    
- Document all possible error responses in API documentation (Swagger/OpenAPI).
    
- Test Ideas - Positive/Negative Scenarios:
    
    - Positive: `EntityNotFoundException` → mapped to `404 Not Found`, JSON error schema includes timestamp, path, message, code.
    - Positive: `EntityAlreadyExistsException` → `409 Conflict`, body contains consistent error fields.
    - Positive: `EntityInvalidArgumentException` → `400 Bad Request`, field errors aggregated for validation failures.
    - Positive: `EntityNotAuthorizedException` → `403 Forbidden`, message does not leak sensitive data.
    - Positive: Generic uncaught exception → `500 Internal Server Error`, sanitized message.
    - Negative: Missing/invalid JSON body → `400 Bad Request`, Jackson parse error mapped to standard error payload.
    - Negative: Unsupported media type / method not allowed → `415/405` with consistent error format.\*

* * *

#### **`Mapper`**

###### General Tasks

- **Refactor Mapper interface/class**
    
    - Split into specialized mappers (e.g., `JumpMapper`, `UserMapper`) instead of one generic class if readability/maintainability requires it.
    - Ensure bidirectional mapping:
        - **InsertDTO → Entity** (for POST)
        - **Entity → ReadOnlyDTO** (for GET responses)
- **Introduce MapStruct**
    
- **Unit tests**
    
    - For each DTO-Entity mapping, write tests:
        - DTO → Entity creates the correct entity fields.
        - Entity → DTO returns the correct response model.
    - Add edge case tests (nulls, missing optional fields).

###### Classes

1.  **`JumpMapper`, `UserMapper`**
    
    - **TDD - Unit Tests First**: Write unit tests for round-trip conversions (Entity → DTO → Entity). Add test cases for null input and partial updates.
    - Define an interface for converting between entity and DTO representations.
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Entity → DTO mapping preserves fields. DTO → Entity creates valid entity.
        - Negative: Null input → safe handling. Partial DTO fields → defaults/nulls managed.
        
        - Positive: Round-trip mapping (`Entity → DTO → Entity`) preserves all expected fields.
        - Positive: Enum values map correctly (`Role.USER` → `"USER"`).
        - Negative: Null inputs handled gracefully.
        - Negative: Missing fields in DTO (e.g., optional description) → entity still valid.

* * *

#### **`Service`**

###### General Tasks

- Refactor each `ServiceImpl` to return DTOs (using the Mapper).
- Ensure proper exception handling (e.g., if `Jump` not found → throw `EntityNotFoundException`).
- Introduce **LookupService** (only read operations) to aggregate static/reference domain data (Aircraft, Dropzones, JumpTypes, etc.).
- Add **unit tests for each service method** using **mocked repositories** (`@ExtendWith(MockitoExtension.class)`).
    - Example: when `repository.findById()` returns empty, service throws `EntityNotFoundException`.

###### Classes

1.  **IAircraftService / AircraftServiceImpl**
    
    - **TDD - Unit Tests First**: Write unit tests for create/update/delete workflows.
    - Exception cases: create with duplicate name → throw `EntityAlreadyExistsException`.)
    - CRUD operations for aircraft.
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Create valid aircraft. Retrieve list works.
        - Negative: Duplicate aircraft creation throws EntityAlreadyExistsException. Update non-existing ID → not found.
        
        - *AircraftServiceImpl*
        - Positive: Create, retrieve, delete valid aircraft works.
        - Negative: Attempt duplicate name throws `EntityAlreadyExistsException`.
        - Negative: Delete with non-existing ID → `EntityNotFoundException`.
2.  **IDropzoneService / DropzoneServiceImpl**
    
    - **TDD - Unit Tests First**: Write tests covering “not found” scenarios. Add rule tests (e.g., cannot delete dropzone if linked jumps exist).
    - Exception cases: ~throws `EntityNotFoundException`
    - CRUD for dropzones.
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Create dropzone, retrieve all works.
        - Negative: Delete dropzone with jumps → EntityInvalidArgumentException. Query missing ID → not found.
        
        - *DropzoneServiceImpl*
        - Positive: Create and list dropzones works.
        - Positive: Cannot delete dropzone if linked jumps exist.
        - Negative: Attempt to update non-existent dropzone → `EntityNotFoundException`.
3.  **IJumpService / JumpServiceImpl**
    
    - **TDD - Unit Tests First**: Write tests for validation failures (EntityInvalidArgumentException).
    - CRUD for jumps. Define jump logging, retrieval, and filtering (e.g., by aircraft, dropzone, user).
    - Mapping between `JumpInsertDTO` and entity.
    - Exception cases: ~ throws `EntityInvalidArgumentException).`
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Create valid jump. Retrieve/filter by user works.
        - Negative: Altitude invalid → EntityInvalidArgumentException. Missing jump ID → not found.
        
        - *JumpServiceImpl*
        - Positive: Create jump with valid DTO works.
        - Positive: Retrieve jumps by user filters correctly.
        - Negative: Altitude ≤ 0 throws `EntityInvalidArgumentException`.
        - Negative: Jump not found → `EntityNotFoundException`.
4.  **IJumptypeService / JumptypeServiceImpl**
    
    - **TDD - Unit Tests First**: Write tests ensuring duplicate prevention and validation.
    - CRUD for jump types (e.g., tandem, freefly)
    - Manage jump types (reference static data).
    - Exception cases: ~ throws `EntityInvalidArgumentException).`
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Add valid jump type. Retrieve all types works.
        - Negative: Duplicate name throws exception. Null name → validation error.
        
        - *JumptypeServiceImpl*
        - Positive: Add new jump type works.
        - Negative: Duplicate type name throws `EntityAlreadyExistsException`.
5.  **UserServiceImpl**
    
    - **TDD - Unit Tests First**: Write tests for duplicate user creation, unauthorized updates, `EntityNotAuthorizedException`.
    - CRUD for users. User registration, update, and retrieval.
    - Add validation (unique username/email).
    - Exception cases: Authorization/ownership checks via `EntityNotAuthorizedException`.
    - Unit tests for duplicate user creation, unauthorized updates.
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Register valid user. Update own profile works
        - Negative: Duplicate email → EntityAlreadyExistsException. Unauthorized user updating another’s profile → EntityNotAuthorizedException.
        
        - *UserServiceImpl*
        - Positive: Register new user, verify password hashing.
        - Positive: Retrieve user by ID returns DTO.
        - Negative: Register duplicate → `EntityAlreadyExistsException`.
        - Negative: Unauthorized user update → `EntityNotAuthorizedException`.
6.  **ILookupService / LookupServiceImpl**
    
    - **TDD - Unit Tests First**: Write tests for:
        - Mock AircraftService, DropzoneService, JumptypeService.
    
    - Define methods: (ILookupService)
        - `List<AircraftLookupDTO> getAircrafts()`
        - `List<DropzoneLookupDTO> getDropzones()`
        - `List<JumpTypeLookupDTO> getJumpTypes()`
    - (LookupServiceImpl)
        - Inject **IAircraftService, IDropzoneService, IJumptypeService**.
        - Delegate calls to these services (which already handle mapping and validation).
        - Optionally provide **caching** for performance (if data rarely changes).
        - Aggregate results into a **LookupResponseDTO**.
    
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Return aggregated lookups (aircraft, dropzones, jump types).
        - Negative: All repositories empty → return empty sets safely.
        
        - *LookupServiceImpl*
        - Positive: Returns correct aircraft/dropzones/jump types list as LookupDTOs.
        - Negative: Empty repository returns empty arrays.

* * *

### Milestone 3: Authentication - Security

#### **`DTO Layer – Authentication`**

###### General Tasks (DTO Layer – Authentication)

- Define DTOs as **immutable records** (Java `record`) or simple POJOs with validation annotations to ensure clean input/output data.
- Apply **Jakarta Bean Validation** annotations (`@NotBlank`, `@Email`, `@Size`, etc.) to fields for automatic validation at the controller level.
- Ensure DTOs **match the REST API’s payloads** (e.g., JSON request bodies for login and register, JSON responses for authentication).
- Write **unit tests** to verify:
    - Field validation constraints.
    - Serialization/deserialization with Jackson.
    - Equality and immutability.
- Align naming with REST conventions (short, clear, domain-specific).
- Maintain **DTO-to-Entity and Entity-to-DTO mappers** (MapStruct or manual mapping in `AuthenticationService`).

###### Classes (DTO Layer – Authentication)

1.  **LoginRequest** (payload for logging into the system)
    
    - **TDD - Unit Tests First**: Write tests for: - Missing/empty values. - Proper JSON serialization/deserialization.
    - Define fields: \* `String usernameOrEmail` (allow flexibility in login, not just username). \* `String password`.
    - Add validation: `@NotBlank` on both fields. Possibly `@Email` if you enforce login only via email.
    - Ensure JSON compatibility
    - Ensure integration with `AuthenticationService` → pass into the authentication manager.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid username/password passes validation.
        - Negative: Missing username → validation error. Empty password → validation error.
2.  **RegisterRequest** (payload for creating a new user account)
    
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
3.  **AuthenticationResponse** (payload sent back to the client after successful authentication)
    
    - **TDD - Unit Tests First**: Write tests for: - Validate fields are correctly serialized. - Ensure immutability (record or final fields). - Ensuring expired/invalid tokens don’t produce a valid responseE
    - Define fields: \* `String token` (JWT). \* `String username`. \* `String role`.
    - (Optional) `Instant expirationTime` — if you want to expose expiry.
    - Ensure JSON compatibility
    - Confirm integration with `JwtService` and `AuthenticationService`.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: JWT returned after login. Username/role populated.
        - Negative: Null/expired token shouldn’t be accepted. Serialization errors handled.

* * *

###### General Tasks **`authentication`** & **`security`**

- Define **user domain model** (e.g., `User`, `Role` enum, `Authority` if needed).
- Decide on **JWT-based authentication flow** (login → JWT issued → secured endpoints).
- Establish **Spring Security configuration** with stateless session handling.
- Implement **global exception handling** for authentication/security errors (401, 403).
- Write **DTOs** for `LoginRequest`, `RegisterRequest`, `AuthenticationResponse`.
- Add proper **exception handling** (invalid credentials, unauthorized access, expired token, etc.) with global `@ControllerAdvice`.
- Ensure **password hashing** (BCrypt).
- Configure **security filters chain** (JWT filter).
- Create **test strategy**:
    - Unit tests for each service (`CustomUserDetailsService`, `JwtService`, `AuthenticationService`).
    - MockMvc tests for endpoints (`/auth/login`, `/auth/register`, `/auth/refresh`).
    - Edge case tests (invalid credentials, expired token, unauthorized access).

* * *

#### **`Authentication`**

###### Classes

1.  **CustomUserDetailsService**
    
    - **TDD - Unit Tests First**: Write tests for finding existing user by username. Handling `user not found` exception. Mapping roles/authorities correctly.
    - Implement `UserDetailsService` to load users from DB.
    - Map domain `User` entity → Spring Security `UserDetails`.
    - Handle cases where user is not found → throw `UsernameNotFoundException`.
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Existing user retrieved. Roles mapped.
        - Negative: Unknown user → UsernameNotFoundException.
        
        - Positive: Existing user loaded correctly with roles.
        - Negative: Unknown username triggers `UsernameNotFoundException`.
2.  **AuthenticationService**
    
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
3.  **JwtAuthenticationFilter**
    
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

* * *

#### **`security`**

###### Classes

1.  **JwtService**
    
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
2.  **SecurityConfiguration**
    
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
3.  **CustomAuthenticationEntryPoint**
    
    - **TDD - Unit Tests First**: Write tests for: - Test response format for unauthorized request. - Test correct status code returned.
    - Handle unauthorized access (401).
    - Return JSON error response instead of HTML login page.
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Unauthorized request → 401 JSON error.
        - Negative: Null auth context handled gracefully.
        
        - Positive: Access protected resource without login → `401 JSON error`.
        - Negative: Null auth header handled without crash.
4.  **CustomAccessDeniedHandler**
    
    - **TDD - Unit Tests First**: Write tests for: - Test response format for forbidden request. - Test correct status code returned.
    - Handle access denied errors (403).
    - Return JSON error response.
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Forbidden request → 403 JSON error.
        - Negative: Ensure consistent error format across all handlers.
        
        - Positive: User with insufficient role denied → `403`.
        - Negative: Verify response matches API error schema.

* * *

### Milestone 4: REST Controllers | API Endpoints

#### **`rest`**

###### General Tasks

- **Base REST conventions**
    
    - Use `@RestController` for JSON endpoints.
    - Prefix endpoints with `/api/v1/...` (versioning strategy).
    - Use `@RequestMapping` at class-level for grouping endpoints (e.g. `/api/v1/users`).
- **DTO usage**
    
    - Ensure controllers only expose DTOs (never entities).
    - Define request and response DTOs for each resource.
    - Validate inputs with `@Valid` and return `400 Bad Request` on errors.
- **HTTP semantics**
    
    - Map CRUD operations to correct HTTP verbs (`POST`, `GET`, `PUT`, `DELETE`).
    - Return appropriate status codes (`201 Created`, `200 OK`, `404 Not Found`, `400 Bad Request`, `204 No Content`).
    - Add `Location` header on resource creation.
- **Error handling**
    
    - Plan to implement a `@ControllerAdvice` for standardized error responses (later).
    - Ensure controllers throw meaningful exceptions to be translated.
- **TDD approach**
    
    - For each controller, write `@WebMvcTest`\-based tests with `MockMvc`.
        
    - Mock the service layer (no DB access).
        
    - Write tests before implementation:
        
        - Happy paths (correct inputs, expected success).
        - Failure cases (validation errors, missing resources, unauthorized access).
    - Validate JSON serialization/deserialization.
        
- **API Documentation**
    
    - Annotate endpoints with Swagger/OpenAPI annotations.
    - Ensure request/response schemas are documented.

* * *

###### Classes

1.  **UserController** (Manage users (registration, retrieval))
    
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
2.  **AuthController** ( Authentication and registration flow (login, register, refresh tokens))
    
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
3.  **JumpController** (CRUD for jumps)
    
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
4.  **LookupController** (Provide reference data (jump types, aircraft, dropzones).)
    
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

* * *

### Milestone 5: Swagger/OpenAPI – API Documentation

### General Tasks

* Set up Swagger/OpenAPI documentation for the REST API.
* Ensure every REST endpoint is documented with clear request/response specifications.
* Provide consistent error response documentation (aligned with `ErrorHandler`).
* Use this milestone as an opportunity to learn the fundamentals of API documentation and self-discovery with Spring Boot.

### Specific/Detailed Tasks

* Add the **Springdoc OpenAPI** dependency (`springdoc-openapi-starter-webmvc-ui`) in `build.gradle`.
* Configure the OpenAPI entry point (title, description, version, contact info).
* Annotate controllers and endpoints with Swagger annotations (`@Operation`, `@ApiResponse`, `@Parameter`).
* Ensure DTOs are well-documented (field descriptions, validation constraints).
* Document error responses globally (e.g., `400`, `404`, `409`, `500`) with standard schema.
* Verify the Swagger UI (`/swagger-ui.html`) is accessible and matches the expected API contract.
* Explore the generated OpenAPI JSON/YAML spec for external tools (e.g., Postman import).
* Write short internal notes on how Swagger integrates with validation and exception handling.

---

### Milestone 6: Frontend UI – Basic Client for the REST API

#### General Tasks

* Build a minimal frontend UI to interact with the REST API.
* Use only **HTML, CSS, and vanilla JavaScript** (no frameworks).
* Focus on learning how to consume the API from a browser client.
* Keep the design simple, with emphasis on functionality.

#### Specific/Detailed Tasks

* Set up a basic static frontend project structure (`index.html`, `style.css`, `script.js`).
* Create a simple homepage with navigation links (Users, Jumps, Lookups).
* Implement **fetch API calls** to interact with the backend:

  * Login form → call `/api/v1/auth/login`, store JWT in `localStorage`.
  * User registration form → call `/api/v1/auth/register`.
  * List Jumps → fetch `/api/v1/jumps`, render in HTML table.
* Add form validation before sending requests (basic JS checks).
* Display error messages consistently based on backend error responses.
* Use minimal CSS for layout and usability (responsive if possible).
* Test the UI against the running backend (via Docker if milestone 6 complete).
* Document how to start the UI and how it connects to the API.

---

### Milestone 7: Docker – Containerization and Deployment Setup

#### General Tasks

* Learn how to package the application into a Docker image.
* Prepare the project for container-based deployment.
* Gain an understanding of Docker concepts (images, containers, volumes, networks).
* Ensure the Skydiving Logbook API can run consistently across environments (local, staging, production).

#### Specific/Detailed Tasks

* Install Docker locally and verify installation.
* Write a `Dockerfile` to build the Spring Boot application into an image.
* Use multi-stage builds to optimize image size (build stage → runtime stage).
* Define environment variables for DB connections and secrets (use `.env` file).
* Create a `docker-compose.yml` file to run the API alongside dependencies (e.g., PostgreSQL, pgAdmin).
* Test the containerized API locally: build image, run container, verify endpoints.
* Add health checks (`/actuator/health`) for monitoring container state.
* Document Docker build/run instructions in the project README.
* Explore pushing the image to Docker Hub (optional, learning step).

---