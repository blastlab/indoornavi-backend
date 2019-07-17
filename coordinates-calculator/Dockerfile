FROM maven:3.6.1-jdk-11-slim
USER root
RUN apt-get update && apt-get install -y curl
RUN curl -sSO https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
	&& chmod +x wait-for-it.sh \
	&& mv wait-for-it.sh /usr/local/bin/wait-for-it
WORKDIR /app
CMD ["./start-server.sh"]
