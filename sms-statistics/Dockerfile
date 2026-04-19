FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/sms-statistics-1.0.0.jar app.jar
EXPOSE 9500
ENTRYPOINT ["java", "-jar", "app.jar"]
