FROM maven:3.3-jdk-8

ENV LANG en_US.utf8

# Directory for output package to deploy
RUN mkdir /deployments

# Separate directory to prevent creating build files in (potential) shared volume
RUN mkdir /target

# Create directory for application sources
RUN mkdir /app
WORKDIR /app
COPY . /app

CMD ["mvn", "clean", "install", "-DbuildDirectory=/target", "-Dwar.output.directory=/deployments", "-DskipTests=true"]
