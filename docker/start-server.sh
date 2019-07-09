#!/bin/bash

: ${APP_LOG_LEVEL:=INFO}
: ${APP_ENVIRONMENT:=development}
: ${APP_DB_USER:=root}
: ${APP_DB_PASSWORD:=password}
: ${CALCULATOR_URL:='ws://localhost:99'}

f [ -f /opt/jboss/wildfly/standalone/deployments/env.js ]; then
	cd /opt/jboss/wildfly/standalone/deployments/
	sed -i "s|ws:\/\/localhost:99|${CALCULATOR_URL}|" env.js
	jar -uvf indoornavi_backend-*.war env.js
	cd /	
fi

if [ $APP_ENVIRONMENT = "development" ]; then
	OPT_MANAGEMENT="--debug 8787 -bmanagement 0.0.0.0"

	# additional debug JVM options for async-profiler
	echo "JAVA_OPTS=\"\$JAVA_OPTS -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints\"" >> ${JBOSS_HOME}/bin/standalone.conf
fi

exec $JBOSS_HOME/bin/standalone.sh \
	-b 0.0.0.0 $OPT_MANAGEMENT \
	-Dapp.db.host=$APP_DB_HOST \
	-Dapp.db.host.prod=$APP_DB_HOST_PROD \
	-Dapp.log.level=$APP_LOG_LEVEL \
	-Dapp.db.user=$APP_DB_USER \
	-Dapp.db.password=$APP_DB_PASSWORD
