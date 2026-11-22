FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --create-home --uid 10001 appuser
COPY --from=build --chown=appuser:appuser /workspace/target/account-service-0.0.1-SNAPSHOT.jar app.jar
USER appuser
EXPOSE 8080
# This keeps the JVM inside the Kubernetes memory limit.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
