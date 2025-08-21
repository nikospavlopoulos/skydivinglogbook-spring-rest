## Milestone 1 – Domain Model (Entities) – Repositories – DTO

### model (Entities Layer)
- [ ] Define all domain entities as JPA `@Entity` classes.
- [ ] Implement common base entity logic in `AbstractEntity` (e.g., `id`, timestamps, UUID).
- [ ] Ensure relationships (OneToMany, ManyToOne, etc.) are mapped consistently.
- [ ] Add validation annotations (`@NotNull`, `@Size`, etc.).
- [ ] Write unit tests for entity validation.

#### Classes:
- [ ] AbstractEntity – lifecycle hooks, tests, validations
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

- [ ] Jump – fields, relationships, validations, tests
    - **TDD - Unit Tests First**: Write entity-level tests validating relationship mapping and constraints.
    - Define entity fields (date, altitude, equipment, references to user, aircraft, dropzone, jumptype).
    - Establish relationships with `User`, `Aircraft`, `Dropzone`, and `Jumptype`.
    - Add validation (e.g., altitude > 0).
    - Test Ideas - Positive/Negative Scenarios: 
        
        - Positive: Valid jump persists with references to User/Aircraft/Dropzone/Jumptype. Altitude > 0 accepted.
        - Negative: Missing relationships (null User or Aircraft) → constraint violation. Altitude ≤ 0 → validation error.
        
        - Positive: Can persist with valid altitude, date, references to Dropzone, Aircraft, User.
        - Positive: Lazy load user/jumptype works when accessed in service layer.
        - Negative: Altitude zero or negative → `ConstraintViolationException`.
        - Negative: Missing relation (no user assigned) → persistence error.

- [ ] User – attributes, uniqueness, relations, tests
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
        
- [ ] Aircraft – static data, persistence, tests
    - **TDD - Unit Tests First**: Test saving and retrieving aircraft entities.
    - Define attributes (model.).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid aircraft model persists. Retrieve by ID works.
        - Negative: Duplicate name/model → exception. Null name → validation failure.
        
- [ ] Dropzone – attributes, relationships, tests
    - **TDD - Unit Tests First**: Test persistence and retrieval, plus relationships with `Jump`.
    - Define attributes (name, location).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Persist and retrieve dropzone with valid name/location. Relation with Jump works.
        - Negative: Null name or location → validation error. Attempt to delete when jumps exist → constraint violation.

- [ ] Jumptype – static data, persistence, tests
    - Define attributes (type name, description).
    - **TDD - Unit Tests First**: Test persistence and mapping.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid type name persists. Multiple jump types co-exist.
        - Negative: Duplicate type name → exception. Null/blank type name → validation error.

---

### repository (Data Access Layer)
- [ ] Define Spring Data JPA repositories for each entity.
- [ ] Add custom query methods where needed.
- [ ] Write unit tests using `@DataJpaTest`.

#### Classes:
- [ ] UserRepository – queries, tests
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
        
- [ ] JumpRepository – queries, tests
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
        
- [ ] AircraftRepository – queries, tests
    - **TDD - Unit Tests First**: Write repository tests for aircraft lookups.
    - Extend `JpaRepository<Aircraft, Long>`.
    - Add query methods (e.g., `findByModel`).
    - Test Ideas - Positive/Negative Scenarios:
        
        - Positive: Save and retrieve aircraft. Query by model returns correct entity.
        - Negative: Query non-existent model returns empty.
        
        - Positive: Query returns entity when model exists.
        - Negative: Save duplicate model → violates unique constraint.
        
- [ ] DropzoneRepository – queries, tests
    - **TDD - Unit Tests First**: Test persistence and retrieval of dropzones.
    - Extend `JpaRepository<Dropzone, Long>`.
    - Add query methods (e.g., `findByLocation`).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Save and retrieve dropzone. Query by location returns correct set.
        - Negative: Query with unknown location → empty list.
        
- [ ] JumptypeRepository – queries, tests
    - **TDD - Unit Tests First**: Test retrieval and persistence of jump types.
    - Extend `JpaRepository<Jumptype, Long>`.
    - Add query methods
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Save and retrieve jump type. Query returns all jump types.
        - Negative: Saving duplicate jump type fails.

---

### dto (Data Transfer Objects)
- [ ] Separate read-only DTOs from insert/update DTOs.
- [ ] Add validation annotations.
- [ ] Use DTOs to decouple API from entities.
- [ ] Write unit tests for DTO validation.

#### Classes:
- [ ] JumpInsertDTO – validation tests
    - **TDD - Unit Tests First**: Write DTO validation tests (e.g., missing required fields should fail).
    - Include fields required for creating a new jump (exclude auto-generated ones like `id`, `createdAt`).
    - Add validation (`@NotNull`, `@Min`).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid DTO passes validation. Altitude ≥ 1000 accepted.
        - Negative: Null date or altitude → validation error. Invalid dropzone ID → mapping fails.        
        - Positive: Validation annotations enforce constraints (e.g., `@Email`).
        - Negative: Blank/invalid inputs → trigger Bean Validation exceptions during controller binding.
        
- [ ] JumpReadOnlyDTO – mapping tests
    - **TDD - Unit Tests First**: Write mapping tests to ensure entity → DTO transformation works correctly.
    - Include fields exposed to clients (readonly).
    - Map from `Jump` entity → DTO using a mapper (e.g., MapStruct or manual).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Correct mapping from Jump entity. All expected fields present in output.
        - Negative: Null entity input to mapper → null-safe response.        
        - Positive: Mapper produces correct JSON-ready output.
        - Negative: Mapping null entity → test null-safe methods.
        
- [ ] UserInsertDTO – validation tests
    - **TDD - Unit Tests First**: Test DTO validation rules.
    - Include fields needed for creating a user (e.g., firstname, lastname, email, password).
    - Add validation (`@Email`, `@NotBlank`).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Valid firstname/lastname/email/password passes validation.
        - Negative: Invalid email → validation error. Password too short → fails.        
        - Positive: Validation annotations enforce constraints (e.g., `@Email`).
        - Negative: Blank/invalid inputs → trigger Bean Validation exceptions during controller binding.
        
- [ ] AircraftLookupDTO – mapping, tests
    - **TDD - Unit Tests First**: - Repository has aircraft → list mapped correctly. - Repository empty → returns empty list.
    - Define DTO with fields: `id`, `name`.
    - Mapper: convert `Aircraft` entity → `AircraftLookupDTO`.
    - Service responsibility: return **list of all aircraft** in lookup form.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Repository returns entities → mapped to list of DTOs. Correct fields populated.
        - Negative: Empty repository → empty list. Null values in entity handled gracefully.        
        - Positive: Mappers correctly flatten only `id` + `name`.
        - Negative: Repository returns empty → JSON array `[]`, not `null`.
        
- [ ] DropzoneLookupDTO – mapping, tests
    - **TDD - Unit Tests First**: - Dropzones exist → DTOs populated with id + name (+ location). - No dropzones in DB → empty list returned.
    - Define DTO with fields: `id`, `name`, `location` (optional if needed for display).
    - Mapper: convert `Dropzone` entity → `DropzoneLookupDTO`.
    - Service responsibility: return **list of all dropzones** in lookup form.
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Repository contains dropzones → mapped list returned. Fields (id, name, location) mapped correctly.
        - Negative: Empty DB → empty list. Null location handled.        
        - Positive: Mappers correctly flatten only `id` + `name`.
        - Negative: Repository returns empty → JSON array `[]`, not `null`.
        
- [ ] JumptypeLookupDTO – mapping, tests
    - **TDD - Unit Tests First**: - Jump types exist → DTOs returned correctly. - Empty DB → empty list returned.
    - Define DTO with fields: `id`, `name`.
    - Mapper: convert `JumpType` entity → `JumpTypeLookupDTO`.
    - Service responsibility: return **list of all jump types** (tandem, AFF, freefly, etc.).
    - Test Ideas - Positive/Negative Scenarios:
        - Positive: Jump types exist → mapped DTOs returned. Correct field mapping.
        - Negative: Empty DB → empty list.        
        - Positive: Mappers correctly flatten only `id` + `name`.
        - Negative: Repository returns empty → JSON array `[]`, not `null`.
