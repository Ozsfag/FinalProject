# Build stage
FROM maven:3.8.4-openjdk-17 as builder
ARG JAR_FILE=docker/services/app/SearchEngine-1.0-SNAPSHOT.jar
ADD SearchEngine-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"]