
# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy the pom.xml and download dependencies to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application, skipping tests
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create the final, smaller, and more secure runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Install curl for the HEALTHCHECK command
RUN apk add --no-cache curl

# Copy the built JAR from the 'build' stage with correct ownership
COPY --from=build --chown=appuser:appgroup /build/target/catalog-service-*.jar app.jar

# Switch to the non-root user
USER appuser

# Expose the port the application runs on (as per README.md)
EXPOSE 8085

# Health check to ensure the application is running correctly
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

# Run the application with container-aware JVM settings
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]