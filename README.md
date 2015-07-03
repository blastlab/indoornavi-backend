#### JDBC Resources

```bash

asadmin create-jdbc-connection-pool --restype javax.sql.DataSource --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlDataSource --property serverName=localhost:user=navi:password=navi:databaseName=indoornavi:useUnicode=true:characterEncoding=utf-8:connectionCollation=utf8_unicode_ci:noAccessToProcedureBodies=true NaviPool
asadmin create-jdbc-resource --connectionpoolid NaviPool jdbc/Navi
```
