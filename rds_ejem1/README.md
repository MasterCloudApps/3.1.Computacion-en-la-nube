# Build app

```
$ mvn clean package
```

## Run app (with local MySQL)

```
$ docker run --rm -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=anuncios -p 3306:3306 -d mysql:8.0.22
$ java -jar target/rds_ejem1-0.0.1.jar \
    --spring.datasource.url=jdbc:mysql://localhost/anuncios \
    --spring.datasource.username=root \
    --spring.datasource.password=password
```

## Run app (with RDS)

```
$ java -jar target/rds_ejem1-0.0.1.jar \
    --spring.datasource.url=jdbc:mysql://<RDS_ENDPOINT>/anuncios \
    --spring.datasource.username=admin \
    --spring.datasource.password=password
```


