# Build stage
FROM maven:3.8.4-openjdk-17 as builder
ARG JAR_FILE=target/SearchEngine-1.0-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]