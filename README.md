#### JDBC Resources

```bash

asadmin create-jdbc-connection-pool --restype javax.sql.DataSource --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlDataSource --property serverName=localhost:user=navi:password=navi:databaseName=indoornavi:useUnicode=true:characterEncoding=utf-8:connectionCollation=utf8_unicode_ci:noAccessToProcedureBodies=true NaviPool
asadmin create-jdbc-resource --connectionpoolid NaviPool jdbc/Navi
```

#### Big files sending

To let users send and receive bigger map files, in /etc/mysql/conf.d/mariadb.cnf or /etc/mysql/my.cnf change/add max_allowed_packet value to 512M and add innodb_log_file_size with the same 512M value.
