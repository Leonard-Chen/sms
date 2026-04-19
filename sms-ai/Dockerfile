FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/sms-ai-1.0.0.jar app.jar
EXPOSE 9600
ENTRYPOINT ["java", "-jar", "app.jar"]
