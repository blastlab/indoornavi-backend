FROM openjdk:8-jdk-alpine
RUN apk update && apk add bash
RUN mkdir app
ENTRYPOINT ["java","-Xdebug","-Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y","-Xms512m","-Xmx2048m","-jar", "app/coordinates-calculator.jar"]
