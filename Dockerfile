FROM openjdk:11-jre-slim
COPY ./backend/build/libs/backend-0.0.1-SNAPSHOT.jar .
CMD nohup java -jar /backend-0.0.1-SNAPSHOT.jar &
EXPOSE 8080
