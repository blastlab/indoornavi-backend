FROM openjdk:8-jre

ENV LIQUIBASE_VERSION 3.5.3
ENV MYSQL_CONNECTOR_VERSION 5.1.45
ENV SCHEMACRAWLER_VERSION 14.19.01

WORKDIR /liquibase

# Download wait-for-it
RUN wget https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
	&& chmod +x wait-for-it.sh \
	&& mv wait-for-it.sh /usr/local/bin/wait-for-it

# Download Liquibase
RUN wget https://github.com/liquibase/liquibase/releases/download/liquibase-parent-${LIQUIBASE_VERSION}/liquibase-${LIQUIBASE_VERSION}-bin.tar.gz \
	&& tar -zxf liquibase-${LIQUIBASE_VERSION}-bin.tar.gz \
	&& rm -rf liquibase-${LIQUIBASE_VERSION}-bin.tar.gz sdk/

# Download connector
RUN wget http://central.maven.org/maven2/mysql/mysql-connector-java/${MYSQL_CONNECTOR_VERSION}/mysql-connector-java-${MYSQL_CONNECTOR_VERSION}.jar -P lib

# Install Lighttpd and SchemaCrawler
RUN apt-get update && apt-get install -y lighttpd graphviz unzip \
	&& cd / \
	&& wget https://github.com/sualeh/SchemaCrawler/releases/download/v$SCHEMACRAWLER_VERSION/schemacrawler-${SCHEMACRAWLER_VERSION}-distribution.zip \
	&& unzip schemacrawler-${SCHEMACRAWLER_VERSION}-distribution.zip \
	&& mv /schemacrawler-${SCHEMACRAWLER_VERSION}-distribution/_schemacrawler /schemacrawler \
	&& rm -rf schemacrawler-${SCHEMACRAWLER_VERSION}-distribution.zip schemacrawler-${SCHEMACRAWLER_VERSION}-distribution

COPY docker/ /

CMD ["/run.sh"]
