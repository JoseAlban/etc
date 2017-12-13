FROM openjdk:8-alpine
MAINTAINER Jose Alban

RUN apk upgrade

ADD build/libs/ai-music-exercise.jar /workdir/service.jar

WORKDIR /workdir
CMD java -jar /workdir/service.jar
