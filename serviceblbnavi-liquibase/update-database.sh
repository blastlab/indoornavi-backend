#!/bin/bash

NAME="IndoorNavi DB Updater"

HOST="localhost"
PORT=3306
SCHEMA="indoornavi"
USER=
PASS=

ROLLBACK=
ROLLBACK_TO=

DUMP=false
QUIET=false

ROOT_DIR="./"
DUMP_FILE="${ROOT_DIR}target/liquibase/migrate.sql"

GRAPH_FILE="graph.png"

function show_help {
echo "$NAME

Usage: $0 [OPTIONS]

If given database does not exists it will be created. This script does not create any users.

After successful DB schema update current schema graph image is created.

Options are given with values after space. For example:
$0 --password s3cr3t

Options marked as flags does not accept any values.

The following options are possible:
-?, --help		Flag. Displays this help.
-h, --host		DB host, default '$HOST'.
    --port		DB port, default $PORT.
-s, --schema		DB schema name, default '$SCHEMA'.
-u, --user		DB connection username, default blank.
-p, --password		DB connection password, default blank.
-r, --rollback		Rollback type, one of 'count', 'date' and 'tag'. If set, Liquibase Rollback will be executed insted of Update. Requires --to to be set.
-t, --to		Number of changes to rollback, date or tag to rollback to, depending on --rollback parameter.
    --dump		Flag. If set, SQL instead of being executed will be displayed to stdout.
    --dump-quiet	Flag. Like --dump, but SQL is not displayed to stdout. Instead it can be found at ${DUMP_FILE}.
"
}

function check_dependencies {
	java -version &>/dev/null
	if [ $? -ne 0 ] || [ $(java -version 2>&1 | sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q') -lt 17 ]
	then
		echo "$NAME requires Java 7 or higher. See http://www.webupd8.org/2014/03/how-to-install-oracle-java-8-in-debian.html"
		exit 1
	else
		mvn --version &>/dev/null
		if [ $? -ne 0 ]
		then
			echo "$NAME requires Maven installed. Install it using:
sudo apt-get install maven"
			exit 1
		fi
	fi
}

function perform_update {
	JDBC_URL="jdbc:mysql://$HOST:$PORT/$SCHEMA?useUnicode=true&characterEncoding=utf-8&connectionCollation=utf8_unicode_ci"
	BASE_CONFIG="-f ${ROOT_DIR}pom.xml"
	LIQ_CONFIG="$BASE_CONFIG -Dliquibase.url=$JDBC_URL&createDatabaseIfNotExist=true -Dliquibase.driver=com.mysql.jdbc.Driver -Dliquibase.username=$USER -Dliquibase.password=$PASS -Dliquibase.changeLogFile=src/main/resources/db.changelog.xml"

	mvn clean package $BASE_CONFIG

	if [ -z $ROLLBACK ]
	then
		LIQ_FCT="update"
	else
		LIQ_FCT="rollback"
		case $ROLLBACK in
			count | date | tag)
				if [ -z $ROLLBACK_TO ]
				then
					echo "Missing --to parameter."
					exit 1
				fi
				LIQ_CONFIG="$LIQ_CONFIG -Dliquibase.rollback${ROLLBACK^}=$ROLLBACK_TO"
				;;
			*)
				echo "Invalid value in --rollback parameter. Allowed are: count, date, tag."
				exit 1
		esac
	fi
	
	if $DUMP
	then
		mvn "liquibase:${LIQ_FCT}SQL" $LIQ_CONFIG
		if [ $? -eq 0 ]
		then
			echo -e "\nChanges successfully calculated."
			if ! $QUIET
			then
				echo -e "Dump of SQL to be executed:\n"
				cat $DUMP_FILE
			else
				echo -e "Dump of SQL file created at $DUMP_FILE"
			fi
		else
			echo -e "\nDump of SQL to be executed failed. See Maven logs above for more details."
		fi
	else
		mvn "liquibase:$LIQ_FCT" $LIQ_CONFIG
		if [ $? -eq 0 ]
		then
			dot -V &>/dev/null
        	        if [ $? -ne 0 ]
	                then
				echo -e "\n$NAME requires Grapviz installed to generate schema diagram. Install it using:
sudo apt-get install graphviz"
			else
		                SC_ARGS="-url=$JDBC_URL -user=$USER -password=$PASS -schemas=$SCHEMA -infolevel=detailed -portablenames -c=graph -outputformat png -outputfile=$GRAPH_FILE -tables=.*\.(?!DATABASECHANGELOG).*"
        		        SC_CONFIG="$BASE_CONFIG -Dexec.mainClass=schemacrawler.Main -Dexec.cleanupDaemonThreads=false"
                		mvn exec:java $SC_CONFIG -Dexec.args="$SC_ARGS"
				echo -e "\nDB graph was saved into $GRAPH_FILE"
			fi

			echo -e "\nDB update completed successfully."
		else
			echo -e "\nDB update failed. See Maven logs above for more details."
		fi
	fi

	echo -e "\nTotal time: $SECONDS s"
}

check_dependencies

while :
do
	case $1 in
		-h | --host)
			HOST=$2
			shift 2
			;;
		--port)
			PORT=$2
			shift 2
			;;
		-s | --schema)
			SCHEMA=$2
			shift 2
			;;
		-u | --user)
			USER=$2
			shift 2
			;;
		-p | --password)
			PASS=$2
			shift 2
			;;
		-r | --rollback)
			ROLLBACK=$2
			shift 2
			;;
		-t | --to)
			ROLLBACK_TO=$2
			shift 2
			;;
		--dump)
			DUMP=true
			shift
			;;
		--dump-quiet)
			DUMP=true
			QUIET=true
			shift
			;;
		-? | --help)
			show_help
			exit 0
			;;
		-*)
			echo "Error: unknown option $1" >&2
			exit 1
			;;
		*) # no more options
			perform_update
			break
			;;
	esac
done
