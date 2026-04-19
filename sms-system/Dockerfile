FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/sms-system-1.0.0.jar app.jar
EXPOSE 9100
ENTRYPOINT ["java", "-jar", "app.jar"]
