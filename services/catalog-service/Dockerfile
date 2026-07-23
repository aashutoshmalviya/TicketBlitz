# Stage 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copy and install shared common-events dependency first
COPY services/common-events ./services/common-events
RUN mvn -f services/common-events/pom.xml clean install -DskipTests

# Copy pom.xml and download dependencies to leverage Docker layer caching
COPY services/catalog-service/pom.xml ./services/catalog-service/pom.xml
RUN mvn -f services/catalog-service/pom.xml dependency:go-offline

# Copy the source code and build the application, skipping tests
COPY services/catalog-service/src ./services/catalog-service/src
RUN mvn -f services/catalog-service/pom.xml package -DskipTests

# Stage 2: Create the final, smaller, and more secure runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Install curl for the HEALTHCHECK command
RUN apk add --no-cache curl

# Copy the built JAR from the 'build' stage with correct ownership
COPY --from=build --chown=appuser:appgroup /build/services/catalog-service/target/catalog-service-*.jar app.jar

# Switch to the non-root user
USER appuser

# Expose the port the application runs on
EXPOSE 8083

# Health check to ensure the application is running correctly
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

# Run the application with container-aware JVM settings
ENTRYPOINT ["java", "-jar", "app.jar"]