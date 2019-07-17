#!/bin/bash

: ${APP_ENVIRONMENT:=development}
: ${OPT_PARAMETERS:=""}
: ${DB_HOST:="localhost:3306"}

if [[ ${APP_ENVIRONMENT} = "development" ]]; then
    OPT_PARAMETERS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y"
fi

if [ ! -f target/coordinates-calculator.jar ];then
    cp pom.xml pom.xml.backup
    sed -i "s|localhost:3306|${DB_HOST}|" pom.xml
    mvn clean install spring-boot:repackage
    mv pom.xml.backup pom.xml
fi

java ${OPT_PARAMETERS} -Xms512m -Xmx2048m -jar target/coordinates-calculator.jar
