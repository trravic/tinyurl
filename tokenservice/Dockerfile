FROM eclipse-temurin:21-jdk-alpine
LABEL authors="vaibhav"

WORKDIR /app

COPY target/tokenservice-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8000

ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]