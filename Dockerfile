# ------------ STAGE 1: BUILD ------------
FROM maven:3.9.1-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ------------ STAGE 2: RUNTIME ------------
FROM eclipse-temurin:17
WORKDIR /app

# Tạo thư mục chứa config
RUN mkdir -p /app/config

# Copy file JAR từ build
COPY --from=build /app/target/read_book_online-0.0.1-SNAPSHOT.jar app.jar

# Cấu hình biến môi trường cho Spring Boot
ENV SPRING_CONFIG_LOCATION=optional:file:/app/config/

EXPOSE 8080

# Dùng ENTRYPOINT để copy secrets khi container chạy
ENTRYPOINT sh -c ' \
  if [ -f /etc/secrets/application.yml ]; then \
    echo "[INFO] Found secret application.yml, copying..."; \
    cp /etc/secrets/application.yml /app/config/application.yml; \
  else \
    echo "[WARN] No application.yml found in /etc/secrets"; \
  fi && \
  java -jar app.jar'
