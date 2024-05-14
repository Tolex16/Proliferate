FROM maven:3.9.2-eclipse-temurin-17-alpine AS builder

# Use multi-stage builds for efficiency and security
WORKDIR /app

COPY ./src src/
COPY ./pom.xml pom.xml

# Run build in a separate stage to reduce final image size
RUN mvn clean package -DskipTests

# Use a smaller, JRE-based image for production
FROM eclipse-temurin:17-jre-alpine

# Copy only the generated JAR from the builder stage
COPY --from=builder /app/target/Proliferate-0.0.1-SNAPSHOT.jar proliferate.jar

# Expose the port needed for your application
EXPOSE 9080

# Execute the JAR within the final image
CMD ["java", "-jar", "proliferate.jar"]
