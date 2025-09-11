# Command to build the docker image
# docker build -t javaapi:latest .

# Command to run the docker image. Forwardf rom container port 8080 to host port 8080 (host_port:container_port)
# docker run -it --name myapi -p 8080:8080 javaapi:latest

FROM maven:eclipse-temurin AS build
LABEL authors="akhilesh"
LABEL purpose="Build stage"

WORKDIR /codebase

COPY pom.xml .
COPY ./webapi ./webapi
COPY ./agent ./agent
RUN mvn clean package -DskipTests

FROM ubuntu/jre:21-24.04_stable AS publish
LABEL authors="akhilesh"
LABEL purpose="runtime stage"

WORKDIR /app

COPY --from=build /codebase/webapi/target/*.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "webapi-0.0.1-SNAPSHOT.jar"]