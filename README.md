# Bigrid Provider Service

## Introduction

This project implements a reactive RESTful web service that provides various bigraph-style location models:

- grids, quadtrees, etc.

as JSON, or as XML.

Location models are typically used in reactive bigraph programs
that support cyber-physical applications or facilitate cyber-physical consistency checks.

### Features

#### Location Models

The following location models are currently supported:

- Points (randomly within a given boundary)
- Uniform n×m grids (points are evenly spaced)
- Quadtrees with boundary

These models can be parameterized (e.g., number of cells per row, maximum depth, …).

#### Ecore-compliant Models

Model output (XML) is realized using the Ecore standard from the Eclipse Modeling Framework (EMF).
Specifically, all bigraphical Ecore-based location models conform to [Bigraph Ecore Metamodel (BEM)](https://github.com/bigraph-toolkit-suite/bigraphs.bigraph-ecore-metamodel).

#### ROS-compliant Service Implementation (⚠️ WIP)

This service implementation is ROS2-compatible.
A dedicated ROS2 package manages this server and makes the web endpoints
available as ROS services to be used in native ROS environments.

## RESTful Web Endpoints

### Create Random Points

Within a default 2D rectangle (boundary) from (0,0) to (10,10):

```shell
curl "http://localhost:8080/generate/points/random?pointCount=10" \
-H "Content-Type: application/json"
```

Within a given rectangular boundary:

```shell
curl -X POST "http://localhost:8080/generate/points/random?pointCount=15" \
-H "Content-Type: application/json" \
-d '{
    "x": 0.0,
    "y": 0.0,
    "width": 15.0,
    "height": 15.0
}'
```

Arguments:
`?pointCount=<INT>`: maximum number of points to generate


### Create a Quadtree

You have to supply points of the form as shown in the example.
The output format can be specified via the format argument.

```shell
curl -X POST "http://localhost:8080/generate/quadtree?maxTreeDepth=4&format=xml" \
-H "Content-Type: application/json" \
-d '{
    "boundary": {
        "x": 0.0,
        "y": 0.0,
        "width": 10.0,
        "height": 10.0
    },
    "pointData": {
          "points": [
            {"x": 0.2, "y": 0.2},
            {"x": 1.0, "y": 2.0},
            {"x": 3.0, "y": 4.0}
          ]
    }
}'
```

Arguments:

- JSON (Default): `?format=json`
- XML (Ecore standard): `?format=xml`
- Configurable max points before subdivision: `?maxTreeDepth=<INT>`
- Configurable max depth of the quadtree: `?maxPointsPerLeaf=<INT>`

Example:

- http://localhost:8080/generate/quadtree?format=json&maxTreeDepth=4&maxPointsPerLeaf=1

## Build and Start Service

Building:
```
mvn clean package
```

Running:
```shell
java -jar ./bin/bigrid-provider-service.jar
```

### Configuration

#### Server Port

Default: 8080

```shell
# Command-Line
java -jar bigrid-provider-service.jar --server.port=9090
# System Property
java -Dserver.port=9090 -jar bigrid-provider-service.jar
# Environment Variable
export SERVER_PORT=9090
```

**Order of Priority**

Spring Boot evaluates properties in this order:
- Command-line arguments (the highest priority)
- Environment variables
- `application.properties`
- Default configuration (the lowest priority)