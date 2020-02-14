# Build app

```
$ mvn clean package
```

## Run app

Run a new container
```
$ java -jar target/rds_ejem1-0.0.1.jar \
    --spring.datasource.url=jdbc:mysql://<RDS_ENDPOINT>/anuncios \
    --spring.datasource.username=admin \
    --spring.datasource.password=password
```


