# IndoorNavi DB Updater #

# Basic usage
```
./update-database.sh --user root --password s3cr3t
```
This will make your localhost database "indoornavi" up-to-date.

## Generating database schema diagram
After updating schema script will automatically generate current schema graph image. To do this it requires **graphviz** installed. On Debian / Ubuntu it can be installed simply by calling:
```
sudo apt-get install graphviz
```

# Creating database
If specified (or default - "indoornavi") database does not exists, it will be created.

**Warning!** Users are not created when DB is created by updater. You have to create user on your own, for example:
```
CREATE USER 'indoornavi'@'localhost' IDENTIFIED BY 's3cr3t';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON indoornavi.* TO 'indoornavi'@'localhost';
```

# More info
For more info and list of options type:
```
./update-database.sh --help
```
