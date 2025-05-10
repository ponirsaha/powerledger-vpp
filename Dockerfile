FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Install Maven to generate wrapper
RUN apt-get update && apt-get install -y maven

# Copy pom.xml to prepare wrapper
COPY pom.xml .

# Generate Maven wrapper inside container (no need to copy from host)
RUN mvn -N io.takari:maven:wrapper

# Copy source code AFTER wrapper is created
COPY src/ src/
COPY mvnw .
# No need to COPY .mvn/ because it's already created in the image

# Make wrapper executable
RUN chmod +x mvnw

# Go offline and build
RUN ./mvnw dependency:go-offline -B
RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
