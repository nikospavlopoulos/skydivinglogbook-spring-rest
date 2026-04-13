# Stage 1: Build Stage
FROM gradle:8.14.3-jdk21-corretto AS builder
WORKDIR /app
COPY . .
# Skip tests during Docker build as they are covered by CI/CD pipeline
RUN ./gradlew clean bootJar -x test

# Stage 2: Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: Create and use a non-privileged system user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/build/libs/app.jar app.jar

# ENTRYPOINT allows the container to receive environment variables at runtime
ENTRYPOINT ["java", "-jar", "app.jar"]

# Usage Notes:
# Build:  docker build -t skydivinglogbook .
# Run in Demo mode:  docker run -e SPRING_PROFILES_ACTIVE=demo --rm -p 8080:8080 --env-file .env --name=sdlog skydivinglogbook:latest