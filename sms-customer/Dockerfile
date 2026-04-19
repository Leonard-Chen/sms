FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/sms-customer-1.0.0.jar app.jar
EXPOSE 9200
ENTRYPOINT ["java", "-jar", "app.jar"]
