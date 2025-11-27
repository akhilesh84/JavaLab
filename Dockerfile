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

# Thi sis where the code is compiled and built
RUN gradle :webapi:bootJar
RUN gradle :agent:shadowJar
RUN gradle :webapi:copyAgentJar

FROM ubuntu/jre:21-24.04_stable AS publish
LABEL authors="akhilesh"
LABEL purpose="runtime stage"

WORKDIR /app

COPY --from=build /codebase/webapi/build/libs/*.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "webapi-0.0.1.jar"]
