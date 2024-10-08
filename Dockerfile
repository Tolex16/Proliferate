# Build stage
FROM maven:3.9.2-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY ./src src/
COPY ./pom.xml pom.xml

# Build the WAR file
RUN mvn clean package -DskipTests

# Tomcat stage
FROM tomcat:9.0-jdk17-adoptopenjdk

# Copy the WAR file to Tomcat's webapps directory
COPY --from=builder /app/target/proliferate.ai.war /usr/local/tomcat/webapps/

# Expose the port needed for your application
EXPOSE 9080

CMD ["catalina.sh", "run"]
