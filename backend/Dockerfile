# -------------------------------------------------------------
# Stage 1: Build & Package Spring Boot Application
# -------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compile source code and produce executable JAR
COPY src ./src
RUN mvn clean package -DskipTests

# -------------------------------------------------------------
# Stage 2: Minimal Production JRE Runtime
# -------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run with a non-root user for container security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup

# Copy executable fat JAR from build stage
COPY --from=build /app/target/online-store-0.0.1-SNAPSHOT.jar app.jar

# Configurable port matching hosting environment (Render assigns PORT dynamically)
ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
