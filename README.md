# BRImage-REST

#### Building
To build, first compile the jar
```
./gradlew build
```
then create the Docker image
```
docker build . -t rest_test
```

#### Running
To run, simply 
```
docker run -p 8080:8080 rest_test
```
and visit `localhost:8080/greeting`.