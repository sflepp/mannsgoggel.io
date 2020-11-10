# Build artefact
FROM maven:3.6.3-openjdk-15 AS build-env
COPY . /build/
WORKDIR /build/
RUN mvn package

# Build runtime image
FROM openjdk:15-jdk-alpine
COPY --from=build-env /build/tournament-server/target/*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=80"]