FROM maven:3.5.2-jdk-15-alpine AS build-env
COPY . /build/
WORKDIR /build/
RUN mvn package


FROM openjdk:15-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build-env /app/tournament-server/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]