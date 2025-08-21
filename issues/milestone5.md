## Milestone 5 – Swagger/OpenAPI – API Documentation

- [ ] Set up Swagger/OpenAPI documentation for the REST API.
- [ ] Ensure every REST endpoint is documented with clear request/response specifications.
- [ ] Provide consistent error response documentation (aligned with `ErrorHandler`).
- [ ] Document DTOs (fields, constraints).

	### General Tasks

	- Set up Swagger/OpenAPI documentation for the REST API.
	- Ensure every REST endpoint is documented with clear request/response specifications.
	- Provide consistent error response documentation (aligned with `ErrorHandler`).
	- Use this milestone as an opportunity to learn the fundamentals of API documentation and self-discovery with Spring Boot.

	### Specific/Detailed Tasks
	
	- Add the **Springdoc OpenAPI*- dependency (`springdoc-openapi-starter-webmvc-ui`) in `build.gradle`.
	- Configure the OpenAPI entry point (title, description, version, contact info).
	- Annotate controllers and endpoints with Swagger annotations (`@Operation`, `@ApiResponse`, `@Parameter`).
	- Ensure DTOs are well-documented (field descriptions, validation constraints).
	- Document error responses globally (e.g., `400`, `404`, `409`, `500`) with standard schema.
	- Verify the Swagger UI (`/swagger-ui.html`) is accessible and matches the expected API contract.
	- Explore the generated OpenAPI JSON/YAML spec for external tools (e.g., Postman import).
	- Write short internal notes on how Swagger integrates with validation and exception handling.

