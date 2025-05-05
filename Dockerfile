FROM maven:3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN  mvn clean package -DskipTests

##Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --form=build /app/target/read_book_online-0.0.1-SNAPSHOW.jar read_book_online.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","read_book_online.jar"]