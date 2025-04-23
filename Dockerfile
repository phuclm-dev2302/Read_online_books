# Stage 1: Build
FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17
WORKDIR /app

# Copy JAR file đã build từ stage trước
COPY --from=build /app/target/read_book_online-0.0.1-SNAPSHOT.jar app.jar

# Kiểm tra xem application.yml có tồn tại trong /etc/secrets/ không
RUN if [ -f /etc/secrets/application.yml ]; then \
    echo "Copying application.yml..."; \
    cp /etc/secrets/application.yml /app/config/application.yml; \
    else \
    echo "No application.yml found in /etc/secrets"; \
    fi

# Cấu hình Spring Boot ưu tiên đọc cả:
# - application.properties trong JAR (classpath)
# - application.yml từ file bên ngoài (override nếu cần)
ENV SPRING_CONFIG_LOCATION=classpath:/,file:/app/config/

# Mở port
EXPOSE 8080

# Entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
