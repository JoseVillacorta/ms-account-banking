# Etapa 1: Construcción
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew build -x test --no-daemon

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# El puerto de Cuentas es el 8082
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
