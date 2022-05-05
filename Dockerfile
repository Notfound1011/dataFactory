FROM openjdk:8-jdk-alpine as build
MAINTAINER yuyu.shi
ADD target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","app.jar"]