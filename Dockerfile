# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Install Maven to build the project
RUN apt-get update && apt-get install -y maven --no-install-recommends && rm -rf /var/lib/apt/lists/*

# Copy the project files (pom.xml and source code)
COPY pom.xml .
COPY src ./src

# Run Maven to build the project and generate the jar file
RUN mvn clean install -DskipTests

# Ensure the jar file is present before copying
RUN ls -lh target/

# Copy the built jar file
COPY target/read_book_online-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
