# Build artefact
FROM maven:3.6.3-openjdk-15 AS build-env
COPY . /build/
WORKDIR /build/
RUN mvn package -DskipTests

# Build runtime image
FROM adoptopenjdk/openjdk15:jre-15.0.1_9-alpine
COPY --from=build-env /build/tournament-server/target/*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","/app.jar", "--server.port=80"]