FROM openjdk:11-jre-slim

COPY . .
RUN ls
WORKDIR backend
RUN ls
RUN chmod 700 gradlew
RUN ./gradlew clean build
WORKDIR /build/libs
COPY backend-0.0.1-SNAPSHOT.jar ./
CMD nohup java -jar /backend-0.0.1-SNAPSHOT.jar &
EXPOSE 8080
