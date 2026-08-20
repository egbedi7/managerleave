FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/managerleave-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8093

ENTRYPOINT ["java", "-jar", "app.jar"]
