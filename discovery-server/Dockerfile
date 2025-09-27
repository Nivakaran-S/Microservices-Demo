# ====== Build stage ======
FROM gradle:8.9-jdk17 AS build
WORKDIR /app

# Install CA certificates for HTTPS access
RUN apt-get update && apt-get install -y ca-certificates wget unzip && rm -rf /var/lib/apt/lists/*

# Copy Gradle configs first for caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies
RUN gradle dependencies --refresh-dependencies

# Copy source code
COPY src ./src

# Build fat JAR (skip tests if desired)
RUN gradle clean build -x test --no-daemon \
    -Dorg.gradle.jvmargs="-Xmx2g -Djava.awt.headless=true"

# ====== Runtime stage ======
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Install CA certificates in runtime (for HTTPS healthchecks)
RUN apk add --no-cache ca-certificates wget

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Copy Spring Boot fat JAR from build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# JVM and server settings
ENV JAVA_OPTS="-XX:+UseZGC -XX:MaxRAMPercentage=75.0"
ENV SERVER_PORT=8080

# Expose port
EXPOSE 8080

# Healthcheck for Spring Boot actuator
HEALTHCHECK --interval=20s --timeout=3s --retries=5 --start-period=20s \
  CMD wget -qO- http://localhost:${SERVER_PORT}/actuator/health | grep -q '"status":"UP"' || exit 1

# Entry point
ENTRYPOINT ["/bin/sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
