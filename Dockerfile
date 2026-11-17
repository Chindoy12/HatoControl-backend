# Construcción
FROM maven:3.9-eclipse-temurin-25 AS construccion
WORKDIR /app
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp package -DskipTests

# Ejecución
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=construccion /app/target/hatocontrol-backend-*.jar hatocontrol.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "hatocontrol.jar"]
