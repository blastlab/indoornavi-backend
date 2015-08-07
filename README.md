

# IndoorNavi JEE app

## Wildfly configuration

Application requires Wildfly 9.0

### Download

After you download wildfly and unzip it in a place you want.

If you want to use wildfly admin console, first you have to do is create a managment user.
Go to $WILDFLY_HOME/bin/ and run add-user.sh(.bat)

In first question type 'a' for managment user.

After your create a user, in the same folder run standalone.sh(.bat) to start server.

If you want to manage your server via admin console, go to http://localhost:9990 and log in with credentials you've created in previous step.

To connect to CLI in the same directory run jboss-cli.sh(.bat) --connect

### Resources

#### MySQL Connector class

In $WILDFLY_HOME/modules/system/layers/com create /mysql/main (mkdir -p mysql/main), inside put your mysql-connector-java-[version]-bin.jar
and create module.xml with following content(remember to change [version] to your connector version) :

<?xml version="1.0" encoding="UTF-8"?>
        <module xmlns="urn:jboss:module:1.1" name="com.mysql">
            <resources>
                <resource-root path="mysql-connector-java-[version]-bin.jar"/>
            </resources>
            <dependencies>
                <module name="javax.api"/>
                <module name="javax.transaction.api"/>
            </dependencies>
        </module>
```bash
echo "<?xml version="1.0" encoding="UTF-8"?><module xmlns="urn:jboss:module:1.1" name="com.mysql"><resources><resource-root path="mysql-connector-java-[version]-bin.jar"/></resources><dependencies><module name="javax.api"/><module name="javax.transaction.api"/></dependencies></module>" >> module.xml
```
In CLI run following command:

```bash
/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-class-name=com.mysql.jdbc.Driver)
```

to reference the module as a driver. Command returns {"outcome" => "success"} in case of success.

#### DataSource

/subsystem=datasources/data-source=Navi:add(driver-name=mysql,user-name=navi,password=navi,connection-url=jdbc:mysql://localhost:3306/indoornavi,min-pool-size=5,max-pool-size=15,jndi-name=java:/jdbc/Navi,enabled=true,validate-on-match=true,valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker,exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter)
/subsystem=datasources/data-source=Navi/connection-properties=characterEncoding:add(value=utf-8)
/subsystem=datasources/data-source=Navi/connection-properties=connectionCollation:add(value=utf8_unicode_ci)
/subsystem=datasources/data-source=Navi/connection-properties=noAccessToProcedureBodies:add(value=true)
/subsystem=datasources/data-source=Navi/connection-properties=useUnicode:add(value=true)

#### Api User

Like in the begining, run run add-user.sh(.bat) from $WILDFLY_HOME/bin, but this time, type 'b' for Application User, add your credentials(user name and password) and in'groups' question type 'Manager' as a group.
This user will be able to acces indoornavi rest api.

#### Big files sending

To let users send and receive bigger map files, in /etc/mysql/conf.d/mariadb.cnf or /etc/mysql/my.cnf change/add max_allowed_packet value to 512M and add innodb_log_file_size with the same 512M value.

## Deploy

```bash
$WILDFLY_HOME/bin/jboss-cli.sh --connect --command="deploy --force [PATH_TO_WAR]"
```
After deploy the following paths will be available:
- `/rest` with REST API,
- `/api` with interactive API documentation (with the HTTP authorization with defined user and password).
