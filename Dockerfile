FROM openjdk:11-jre-slim
WORKDIR /backend
RUN ./gradlew build
WORKDIR /build/libs
COPY backend-0.0.1-SNAPSHOT.jar ./
CMD nohup java -jar /backend-0.0.1-SNAPSHOT.jar &
EXPOSE 8080