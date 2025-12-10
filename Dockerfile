#FROM eclipse-temurin:17
#COPY target/labs-1.0-SNAPSHOT.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "/app.jar"]
# ---- Stage 1: Build ----
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Собираем приложение (skip тесты для ускорения)
RUN mvn clean package -DskipTests

# ---- Stage 2: Run ----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копируем готовый jar из build stage
COPY --from=build /app/target/*.jar app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
