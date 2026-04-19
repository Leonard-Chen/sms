FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/sms-order-1.0.0.jar app.jar
EXPOSE 9300
ENTRYPOINT ["java", "-jar", "app.jar"]
