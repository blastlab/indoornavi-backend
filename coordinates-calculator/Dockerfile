FROM openjdk:8-jdk-alpine
USER root
RUN apk update && apk add bash
RUN mkdir app
COPY start-server.sh /
CMD ["/start-server.sh"]
