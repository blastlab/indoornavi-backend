FROM jboss/wildfly:10.1.0.Final

ENV MARIADB_CONNECTOR_VERSION 1.5.4

# User root user instead of jboss, as it can execute other actions then starting server
# like pinging other containers
USER root

RUN yum install -y curl

RUN curl -sSO https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh \
	&& chmod +x wait-for-it.sh \
	&& mv wait-for-it.sh /usr/local/bin/wait-for-it

# Add admin user for management panel
RUN $JBOSS_HOME/bin/add-user.sh admin admin --silent

RUN $JBOSS_HOME/bin/add-user.sh -a api api --group Manager --silent

COPY docker/wait-for-container-end /usr/local/bin/
COPY docker/standalone.xml $JBOSS_HOME/standalone/configuration/
COPY docker/mariadb-module.xml $JBOSS_HOME/modules/system/layers/base/org/mariadb/jdbc/main/module.xml
RUN cd $JBOSS_HOME/modules/system/layers/base/org/mariadb/jdbc/main/ \
	&& curl -o mariadb-java-client.jar http://central.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/$MARIADB_CONNECTOR_VERSION/mariadb-java-client-$MARIADB_CONNECTOR_VERSION.jar

COPY docker/start-server.sh /

CMD ["/start-server.sh"]
