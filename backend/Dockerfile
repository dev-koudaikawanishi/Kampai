FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon -x test
CMD ["java", "-jar", "build/libs/Kampai-0.0.1-SNAPSHOT.jar"]
