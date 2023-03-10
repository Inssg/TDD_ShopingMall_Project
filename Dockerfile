FROM openjdk:11-jre-slim
RUN mkdir -p /app
WORKDIR /app
COPY . ./
WORKDIR /backend
RUN gradlew clean build
WORKDIR /build/libs
COPY backend-0.0.1-SNAPSHOT.jar ./
CMD nohup java -jar /backend-0.0.1-SNAPSHOT.jar &
EXPOSE 8080
