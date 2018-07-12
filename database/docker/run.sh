#!/usr/bin/env bash

# Set default values
: ${PORT:=3306}
: ${COMMAND:="update"}

JDBC_URL="jdbc:mysql://$HOST:$PORT/$SCHEMA?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf-8&connectionCollation=utf8_unicode_ci&serverTimezone=UTC"

function liq {
	cd /liquibase
	./liquibase \
		--url="$JDBC_URL" \
		--username="$USER" \
		--password="$PASSWORD" \
		--contexts="$CONTEXTS" \
		"$@"
}

liq ${COMMAND}

# Serve website with doc, only after regular update action (on others, like rollback, finish and close container)
if [ "$COMMAND" == "update" ]; then
	liq dbDoc /www/liquibase

	GRAPH_FILE="/www/graph.png"
	SC_ARGS="-url=jdbc:mysql://$HOST:$PORT/$SCHEMA?nullNamePatternMatchesAll=true&serverTimezone=UTC -user=$USER -password=$PASSWORD -command=schema -loglevel=CONFIG -infolevel=detailed -portablenames -c=graph -outputformat png -outputfile=$GRAPH_FILE -tables=.*\.(?!DATABASECHANGELOG).*"
	cd /schemacrawler
	./schemacrawler.sh ${SC_ARGS}

	cd /
	exec lighttpd -D -f lighttpd.conf
fi