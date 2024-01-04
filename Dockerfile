# Base image 
FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/drrr-api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
# 애플리케이션 실행 명령어
CMD ["java", "-jar", "app.jar"]
