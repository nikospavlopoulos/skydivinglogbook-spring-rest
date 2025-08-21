## Milestone 7 – Docker – Containerization and Deployment Setup

- [ ] Install Docker locally.
- [ ] Write Dockerfile (multi-stage build).
- [ ] Use .env for DB/secrets.
- [ ] Create docker-compose.yml with DB + API.
- [ ] Test containerized API.
- [ ] Add health checks (/actuator/health).
- [ ] Document Docker build/run process.
- [ ] Explore pushing image to Docker Hub.

	#### General Tasks

	- Learn how to package the application into a Docker image.
	- Prepare the project for container-based deployment.
	- Gain an understanding of Docker concepts (images, containers, volumes, networks).
	- Ensure the Skydiving Logbook API can run consistently across environments (local, staging, production).

	#### Specific/Detailed Tasks

	- Install Docker locally and verify installation.
	- Write a `Dockerfile` to build the Spring Boot application into an image.
	- Use multi-stage builds to optimize image size (build stage → runtime stage).
	- Define environment variables for DB connections and secrets (use `.env` file).
	- Create a `docker-compose.yml` file to run the API alongside dependencies (e.g., PostgreSQL, pgAdmin).
	- Test the containerized API locally: build image, run container, verify endpoints.
	- Add health checks (`/actuator/health`) for monitoring container state.
	- Document Docker build/run instructions in the project README.
	- Explore pushing the image to Docker Hub (optional, learning step).
