#!/bin/bash

: ${APP_LOG_LEVEL:=INFO}

: ${APP_ENVIRONMENT:=development}

if [ $APP_ENVIRONMENT = "development" ]; then
	OPT_MANAGEMENT="-bmanagement 0.0.0.0"
fi

exec $JBOSS_HOME/bin/standalone.sh \
	--debug 8787 -b 0.0.0.0 $OPT_MANAGEMENT \
	-Dapp.db.host=$APP_DB_HOST \
	-Dapp.db.host.prod=$APP_DB_HOST_PROD \
	-Dapp.log.level=$APP_LOG_LEVEL
