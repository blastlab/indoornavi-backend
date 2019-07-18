#!/bin/bash

: ${APP_ENVIRONMENT:=development}
: ${OPT_PARAMETERS:=""}
: ${DB_HOST:="localhost:3306"}

if [[ ${APP_ENVIRONMENT} = "development" ]]; then
    OPT_PARAMETERS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y"
fi

if [ ! -f target/coordinates-calculator.jar ];then
    mvn clean install spring-boot:repackage -Ddb.host=$DB_HOST
fi

java ${OPT_PARAMETERS} -Xms512m -Xmx2048m -jar target/coordinates-calculator.jar
