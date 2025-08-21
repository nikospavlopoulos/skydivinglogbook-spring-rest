## Milestone 2 – Exceptions – Mapper – Service

### Exceptions (under Core)
- [ ] Define a **global exception handler** using `@ControllerAdvice` and `@ExceptionHandler` to map exceptions to HTTP responses (`ResponseEntity`).
- [ ] Decide consistent error response format (e.g., `{ timestamp, status, error, message, path }`).
- [ ] Ensure each existing exception (`EntityNotFoundException`, `EntityAlreadyExistsException`, etc.) maps to the correct HTTP status code (`404`, `409`, `400`, `403`, `500`).
- [ ] Refactor exception messages to be API-consumer-friendly (clear, not leaking internal details).
- [ ] Write unit tests for exception mapping.

#### Classes:
- [ ] AppServerException + tests
    - **TDD - Unit Tests First**: Write tests to assert it correctly wraps root causes (e.g., database errors)
    - Define for unexpected server-side failures (maps to 500).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Wraps DB failure into exception. Correct message propagated.
        - Negative: Null cause → still produces valid error message.        
        - Positive: Wraps technical DB error, global handler returns `500`.
        - Negative: Null cause still produces valid exception object.
        
- [ ] EntityAlreadyExistsException + tests
    - **TDD - Unit Tests First**: Write tests for inheritance structure and generic handling.
    - Use when attempting to create duplicate records (maps to 409 Conflict).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Raised when duplicate insert attempted.
        - Negative: Throw without message → default message tested.        
        - Positive: Triggered by duplicate create attempt.
        - Negative: Check global handler response consistency (`409` status).
        
- [ ] EntityGenericException + tests
	- **TDD - Unit Tests First**: Write tests for inheritance structure and generic handling.
	- flexible fallback for entity-related issues not covered by other exceptions.
	- Test Ideas - Positive/Negative Scenarios:
		- Positive: Works as fallback when unexpected entity error occurs.
		- Negative: Ensure inheritance hierarchy doesn’t break global handler.
        
- [ ] EntityInvalidArgumentException + tests
	- **TDD - Unit Tests First**: Write validation tests (e.g., jump number < 0).
	- Use for invalid request data (maps to 400 Bad Request).
	- Test Ideas - Positive/Negative Scenarios:
		- Positive: Triggered for invalid request (e.g., altitude < 0).
		- Negative: Ensure controller maps exception to 400 Bad Request.        
		- Positive: Thrown for invalid input parameters in services.
		- Negative: Ensure error message is meaningful (not empty/null).
        
- [ ] EntityNotAuthorizedException + tests
    - **TDD - Unit Tests First**: Write tests ensuring correct exception thrown when a user tries to access another user’s logbook.
    - Use for authorization/ownership errors (maps to 403).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Unauthorized user modifying another’s jump triggers exception.
        - Negative: Ensure no leakage of sensitive info in message.        
        - Positive: Unauthorized user action mapped to `403`.
        - Negative: Ensure error doesn’t leak internal entity info (e.g., jump UUID).
        
- [ ] EntityNotFoundException + tests
    - **TDD - Unit Tests First**: Write service-level tests that trigger it (e.g., jump ID not found).
    - Use for missing resources (maps to 404).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Missing jump ID triggers exception. Correct 404 returned.
        - Negative: Null entity input doesn’t silently succeed.        
        - Positive: Query missing entity triggers exception, handled as `404`.
        - Negative: Null entity reference never silently passes.

---

### GlobalExceptionHandler
- [ ] Create a `@ControllerAdvice` class (e.g., `GlobalExceptionHandler`) to centralize exception → HTTP response mapping.
- [ ] Standardize error response payloads.
- [ ] Write unit + integration tests.

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
	- Negative: Unsupported media type / method not allowed → `415/405` with consistent error format.

---

### Mapper
- [ ] Split into specialized mappers (JumpMapper, UserMapper, etc.).
- [ ] Ensure bidirectional mapping (DTO ↔ Entity).
- [ ] Introduce MapStruct.
- [ ] Write mapper unit tests.

#### Classes:
- [ ] JumpMapper, UserMapper
    - **TDD - Unit Tests First**: Write unit tests for round-trip conversions (Entity → DTO → Entity). Add test cases for null input and partial updates.
    - Define an interface for converting between entity and DTO representations.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Entity → DTO mapping preserves fields. DTO → Entity creates valid entity.
        - Negative: Null input → safe handling. Partial DTO fields → defaults/nulls managed.        
        - Positive: Round-trip mapping (`Entity → DTO → Entity`) preserves all expected fields.
        - Positive: Enum values map correctly (`Role.USER` → `"USER"`).
        - Negative: Null inputs handled gracefully.
        - Negative: Missing fields in DTO (e.g., optional description) → entity still valid.

---

### Service
- [ ] Refactor services (each `ServiceImpl`) to return DTOs instead of entities.
- [ ] Handle exceptions correctly.
- [ ] Introduce LookupService. ((only read operations) to aggregate static/reference domain data (Aircraft, Dropzones, JumpTypes, etc.)
- [ ] Write unit tests using mocked repositories. (`@ExtendWith(MockitoExtension.class)`).

#### Classes:
- [ ] AircraftServiceImpl + IAircraftService
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
        
- [ ] DropzoneServiceImpl + IDropzoneService
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
        
- [ ] JumpServiceImpl + IJumpService
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
        
- [ ] JumptypeServiceImpl + IJumptypeService
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
        
- [ ] UserServiceImpl
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
        
- [ ] LookupServiceImpl + ILookupService
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
