# Parking Rates API
This repository constitutes a code challenge submission to SpotHero.

## Setup
Clone this repository
```
$ git clone https://github.com/christherama/parking-rates-grpc
```

Build the Docker images
```
$ cd parking-rates-grpc
$ ./gradlew distDocker
```

Start the containers
```
$ docker-compose up -d
```

Make a request
```
$ curl -X GET 'http://localhost:4567/rate?start=2015-07-01T07:00:00Z&end=2015-07-01T08:00:00Z'
```

## References
- Being that this was my first experience with gRPC, code inspiration was taken from [github.com/grpc/grpc-java](https://github.com/grpc/grpc-java).