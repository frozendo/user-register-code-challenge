FROM openjdk:17-alpine

MAINTAINER frozendo90

ADD ./build/libs/*.jar /user-register.jar

EXPOSE 9000

ENTRYPOINT java -jar user-register.jar