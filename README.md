# Java Flight Recorder Custom Events

Example code accompanying the [blog post](https://www.morling.dev/blog/tbd.) "Monitoring REST APIs with Java Flight Recorder Custom Events".
It shows how to use custom event types with Java Flight Recorder and Mission Control to gain insight into runtime performance of a JAX-RS based REST API.
It also demonstrates how to export Flight Recorder events in realtime via MicroProfile Metrics, using the Flight Recorderstreaming API added in Java 14.

## Build

Make sure to have Java 14 installed.
Run the following to build this project:

```shell
mvn clean package
docker-compose up --build
```

Open the web application at http://localhost:8080/.
You then can connect to the running application on port 1898 using Mission Control,
start Flight Recorder and observe "JAX-RS" events in the recording.
You also can observe the exported metrics via Grafana at http://localhost:3000/.

## License

This code base is available ander the Apache License, version 2.

