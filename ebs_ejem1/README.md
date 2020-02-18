## Create Database

Run a new container
```
docker run -d --name master-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=pass -e MYSQL_DATABASE=anuncios -d  mysql:5.7
```

# Build app

```
mvn clean package
```

# Run app

```
java -jar target/ebs_ejem1-0.0.1.jar
```