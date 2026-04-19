FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/sms-employee-1.0.0.jar app.jar
EXPOSE 9400
ENTRYPOINT ["java", "-jar", "app.jar"]
