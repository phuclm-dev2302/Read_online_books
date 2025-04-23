# Stage 2: Run
FROM eclipse-temurin:17
WORKDIR /app
RUN mkdir -p /app/config
COPY --from=build /app/target/read_book_online-0.0.1-SNAPSHOT.jar app.jar

# Kiểm tra nội dung thư mục /etc/secrets/ để debug
RUN ls -la /etc/secrets/ || echo "Directory /etc/secrets/ is empty"

# Sao chép application.yml
RUN if [ -f /etc/secrets/application.yml ]; then \
    echo "Copying application.yml..."; \
    cp /etc/secrets/application.yml /app/config/application.yml; \
    else \
    echo "No application.yml found in /etc/secrets"; \
    fi

# Sao chép application.properties
RUN if [ -f /etc/secrets/application.properties ]; then \
    echo "Copying application.properties..."; \
    cp /etc/secrets/application.properties /app/config/application.properties; \
    else \
    echo "No application.properties found in /etc/secrets"; \
    fi

# Cấu hình SPRING_CONFIG_LOCATION chỉ tìm trong /app/config/
ENV SPRING_CONFIG_LOCATION=optional:file:/app/config/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]