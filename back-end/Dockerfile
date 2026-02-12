# Stage 1: Build the JAR
FROM amazoncorretto:17-alpine-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the JAR
RUN ./mvnw package -DskipTests

# Stage 2: Run the app
FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
