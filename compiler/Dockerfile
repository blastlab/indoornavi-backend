FROM maven:3.3-jdk-8

ENV LANG en_US.utf8
USER root
# Directory for output package to deploy
RUN mkdir /deployments

# Separate directory to prevent creating build files in (potential) shared volume
RUN mkdir /target

RUN mkdir /app
WORKDIR /app


CMD ["mvn", "clean", "package"]

