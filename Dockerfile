# Stage 1: Build Stage

FROM gradle:8.14.3-jdk21-corretto AS builder
WORKDIR /
COPY . .
RUN gradle clean bootJar

#Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /
ENV SPRING_PROFILES_ACTIVE=demo
COPY --from=builder /build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Commands notes
# docker build -t skydivinglogbook .
# docker run --rm -p8080:8080 --name=sdlog skydivinglogbook:latest