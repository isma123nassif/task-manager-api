# ---- Build stage ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy the Maven wrapper and pom first to cache dependencies.
COPY .mvn/ .mvn
COPY mvnw ./
COPY pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Copy the source code and build the executable jar.
COPY src ./src
RUN ./mvnw -B -q package -DskipTests

# Extract the Spring Boot layered layout.
RUN java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination target/extracted

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY --from=build --chown=spring:spring /app/target/extracted/dependencies/ ./
COPY --from=build --chown=spring:spring /app/target/extracted/spring-boot-loader/ ./
COPY --from=build --chown=spring:spring /app/target/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=spring:spring /app/target/extracted/application/ ./

USER spring
EXPOSE 8080

ENV JAVA_OPTS='-XX:MaxRAMPercentage=75.0'

# exec replaces the shell so java becomes PID 1 and receives SIGTERM directly for graceful shutdown.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
