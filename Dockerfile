# Build stage
FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app

# Copy the pom.xml
COPY pom.xml /app

# Copy the source code
COPY src /app/src

# Build the project
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true -Dmaven.repo.remote=false -Dmaven.repo.local=/app/.m2

# Runtime stage
FROM openjdk:17-alpine

# Set environment variables
ENV JAVA_OPTS=""

# Copy the jar file from the builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Expose port
EXPOSE 8181

# Run the app
CMD ["java", "-jar", "/app/app.jar"]