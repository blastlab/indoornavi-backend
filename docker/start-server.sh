#!/bin/bash

: ${APP_LOG_LEVEL:=INFO}
: ${APP_ENVIRONMENT:=development}
: ${APP_DB_USER:=root}
: ${APP_DB_PASSWORD:=password}

if [ $APP_ENVIRONMENT = "development" ]; then
	OPT_MANAGEMENT="--debug 8787 -bmanagement 0.0.0.0"
fi

exec $JBOSS_HOME/bin/standalone.sh \
	-b 0.0.0.0 $OPT_MANAGEMENT \
	-Dapp.db.host=$APP_DB_HOST \
	-Dapp.db.host.prod=$APP_DB_HOST_PROD \
	-Dapp.log.level=$APP_LOG_LEVEL \
	-Dapp.db.user=$APP_DB_USER \
	-Dapp.db.password=$APP_DB_PASSWORD
