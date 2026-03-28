# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar -x test
RUN find build/libs -name "*SNAPSHOT.jar" -not -name "*plain.jar" -exec cp {} build/libs/app.jar \;

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN useradd -m appuser
USER appuser
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT ["java", "-Xmx384m", "-Xms384m", "-jar", "app.jar"]
