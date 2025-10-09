FROM amazoncorretto:21
LABEL maintainer="nikospavlopoulos.com"
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Commands notes
# docker build -t skydivinglogbook .
# docker run -p8080:8080 --name=sdlog skydivinglogbook:latest