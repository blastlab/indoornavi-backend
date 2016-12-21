FROM maven:3-jdk-8

ENV LANG C.UTF-8

RUN curl -sSO https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
	&& chmod +x wait-for-it.sh \
	&& mv wait-for-it.sh /usr/local/bin/wait-for-it

RUN mkdir /liquibase
WORKDIR /liquibase
COPY . /liquibase

CMD ["/liquibase/update-database.sh", "-u", "root"]
