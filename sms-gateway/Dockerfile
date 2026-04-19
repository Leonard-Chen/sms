FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/sms-gateway-1.0.0.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
