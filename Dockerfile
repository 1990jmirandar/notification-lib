# --- ETAPA 1: Construir e Instalar la Librería Core ---
FROM maven:3.9.6-eclipse-temurin-21 AS lib-builder
WORKDIR /app/library

COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# --- ETAPA 2: Construir la App Demo ---
FROM maven:3.9.6-eclipse-temurin-21 AS app-builder
WORKDIR /app/demo

COPY example/demo-app/pom.xml .
COPY example/demo-app/src ./src

COPY --from=lib-builder /root/.m2 /root/.m2

RUN mvn clean package

# --- ETAPA 3: Ejecutar (Runtime Ligero) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=app-builder /app/demo/target/demo-notification-app-1.0.0.jar ./demo.jar

CMD ["java", "-jar", "demo.jar"]