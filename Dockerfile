FROM openjdk:11-jdk-slim as build
MAINTAINER yuyu.shi
ADD target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","app.jar"]