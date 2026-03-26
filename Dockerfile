FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/hotel-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]
