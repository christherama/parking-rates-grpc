# Parking Rates API

## Setup
Clone this repository
```
$ git clone https://github.com/christherama/parking-rates-grpc
$ cd parking-rates-grpc
```

Run unit tests
```
$ ./gradlew client:test
```

Build Docker images
```
$ ./gradlew distDocker
```

Start containers
```
$ docker-compose up -d
```

Run integration tests
```
$ ./gradlew integration:test
```

Make a request
```
$ curl -X GET 'http://localhost:4567/rate?start=2015-07-01T07:00:00Z&end=2015-07-01T08:00:00Z'
```

## References
- Being that this was my first experience with gRPC, code inspiration was taken from [github.com/grpc/grpc-java](https://github.com/grpc/grpc-java).