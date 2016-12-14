#!/bin/bash

: ${APP_ENVIRONMENT:=production}

if [ $APP_ENVIRONMENT = "development" ]; then
	OPT_MANAGEMENT="-bmanagement 0.0.0.0"
	APP_LOG_LEVEL="DEBUG"
else
	APP_LOG_LEVEL="INFO"
fi

exec $JBOSS_HOME/bin/standalone.sh \
	--debug 8787 -b 0.0.0.0 $OPT_MANAGEMENT \
	-Dapp.db.host=$APP_DB_HOST \
	-Dapp.db.host.prod=$APP_DB_HOST_PROD \
	-Dapp.log.level=$APP_LOG_LEVEL
