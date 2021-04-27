FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]