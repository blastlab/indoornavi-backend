#!/bin/bash

HOST="localhost"
PORT=3306
SCHEMA="indoornavi"
USER=
PASS=

TEST=false

ROLLBACK=
ROLLBACK_TO=

DUMP=false
QUIET=false

ROOT_DIR="./"
DUMP_FILE="${ROOT_DIR}target/liquibase/migrate.sql"

GRAPH_FILE="graph.png"

function show_help {
echo "Usage: $0 -s <schema_name> [OPTIONS]

If given database does not exists it will be created.

After successful DB schema update current schema graph image is created.

Options are given with values after space. For example:
$0 --password s3cr3t

Options marked as flags does not accept any values.

The following options are possible:
-?, --help		Flag. Displays this help.
-h, --host		DB host, default '$HOST'.
    --port		DB port, default $PORT.
-s, --schema	DB schema name, default '$SCHEMA'.
-u, --user		DB connection username, default blank.
-p, --password	DB connection password, default blank.
--test			Use 'test' context with additional test data inserts.
-r, --rollback	Rollback type, one of 'count', 'date' and 'tag'. If set, Liquibase Rollback will be executed insted of Update. Requires --to to be set.
-t, --to		Number of changes to rollback, date or tag to rollback to, depending on --rollback parameter.
    --dump		Flag. If set, SQL instead of being executed will be displayed to stdout.
    --dump-quiet	Flag. Like --dump, but SQL is not displayed to stdout. Instead it can be found at ${DUMP_FILE}.
"
}

function check_dependencies {
	JAVA_VERSION=$(java -version 2>&1 | sed 's/\(java\|openjdk\) version "\(.*\)\.\(.*\)\..*"/\2\3/; 1q')
	if ! $(command -v java > /dev/null) || ! [[ $JAVA_VERSION =~ ^[0-9]+$ ]] || [ "$JAVA_VERSION" -lt 17 ]
	then
		echo "Job failed. Java 7 or higher is required."
		exit 1
	else
		mvn --version &>/dev/null
		if [ $? -ne 0 ]
		then
			echo "Job failed. Maven is required."
			exit 1
		fi
	fi
}

function check_parameters {
	if [ -z "$SCHEMA" ]; then
		echo "Database schema not set."
		exit 1
	fi
}

function perform_update {
	JDBC_URL="jdbc:mysql://$HOST:$PORT/$SCHEMA?useUnicode=true&characterEncoding=utf-8&connectionCollation=utf8_unicode_ci"
	BASE_CONFIG="-f ${ROOT_DIR}pom.xml -DskipTests"
	LIQ_CONFIG="$BASE_CONFIG -Dliquibase.url=$JDBC_URL&createDatabaseIfNotExist=true -Dliquibase.driver=com.mysql.jdbc.Driver -Dliquibase.username=$USER -Dliquibase.password=$PASS -Dliquibase.changeLogFile=src/main/resources/db.changelog.xml"

	if $TEST; then
		LIQ_CONFIG="$LIQ_CONFIG -Dliquibase.contexts=test"
	else
		# After upgrade to Liquibase 3.5 we should set default context and use it here.
		LIQ_CONFIG="$LIQ_CONFIG -Dliquibase.contexts=!test"
	fi

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
		mvn clean package "liquibase:${LIQ_FCT}SQL" $LIQ_CONFIG
		RESULT=$?
		echo
		
		if [ $RESULT -eq 0 ]
		then
			echo "Changes successfully calculated."
			if ! $QUIET
			then
				echo "Dump of SQL to be executed:"
				echo
				cat $DUMP_FILE
			else
				echo "Dump of SQL file created at $DUMP_FILE"
			fi
		else
			echo "Dump of SQL to be executed failed. See Maven logs above for more details."
			exit 1
		fi
	else
		mvn clean package liquibase:$LIQ_FCT $LIQ_CONFIG
		RESULT=$?
		echo
		
		if [ $RESULT -eq 0 ]; then
			dot -V &>/dev/null
			if [ $? -ne 0 ]; then
				echo "Schema diagram will not be generated, Grapviz is not installed."
			else
				SC_ARGS="-url=$JDBC_URL -user=$USER -password=$PASS -schemas=$SCHEMA -infolevel=detailed -portablenames -c=graph -outputformat png -outputfile=$GRAPH_FILE -tables=.*\.(?!DATABASECHANGELOG).*"
				SC_CONFIG="$BASE_CONFIG -Dexec.mainClass=schemacrawler.Main -Dexec.cleanupDaemonThreads=false"
				mvn exec:java $SC_CONFIG -Dexec.args="$SC_ARGS"
				RESULT=$?
				echo
				
				if [ $RESULT -eq 0 ]; then
					echo "Schema diagram was saved into $GRAPH_FILE"
				else
					echo "Schema diagram creation failed. See Maven logs above for more details."
				fi
			fi
			
			echo
			echo "DB update completed successfully."
		else
			echo "DB update failed. See Maven logs above for more details."
			exit 1
		fi
	fi

	echo
	echo "Total time: $SECONDS s"
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
		--test)
			TEST=true
			shift
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
			check_parameters
			perform_update
			break
			;;
	esac
done
