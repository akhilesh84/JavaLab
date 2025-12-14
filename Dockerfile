# Command to build the docker image
# docker build -t javaapi:latest .

# Command to run the docker image. Forwardf rom container port 8080 to host port 8080 (host_port:container_port)
# docker run -it --name myapi -p 8080:8080 javaapi:latest

FROM gradle:8.14.3-jdk21 AS build
LABEL authors="akhilesh"
LABEL purpose="Build stage"

WORKDIR /codebase

COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY ./webapi ./webapi
COPY ./agent ./agent

# This is where the code is compiled and built
RUN gradle :webapi:bootJar
RUN gradle :agent:shadowJar
RUN gradle :webapi:copyAgentJar

# Download OpenTelemetry Java Agent (in build stage where we have wget/curl)
RUN wget -O /tmp/opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar

FROM ubuntu/jre:21-24.04_stable AS publish
LABEL authors="akhilesh"
LABEL purpose="runtime stage"

WORKDIR /app

# Copy the OpenTelemetry Java Agent from build stage
COPY --from=build /tmp/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

COPY --from=build /codebase/webapi/build/libs/*.jar .

EXPOSE 8080

# Run with OpenTelemetry Java Agent for automatic tracing instrumentation
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "webapi-0.0.1.jar"]
