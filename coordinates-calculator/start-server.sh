#!/bin/bash

: ${APP_ENVIRONMENT:=development}
: ${OPT_PARAMETERS:=""}

if [[ ${APP_ENVIRONMENT} = "development" ]]; then
    OPT_PARAMETERS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y"
fi

java ${OPT_PARAMETERS} -Xms512m -Xmx2048m -jar app/coordinates-calculator.jar
