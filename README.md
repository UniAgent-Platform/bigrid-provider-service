# Bigrid Provider Service

Latest Version: `v1.2.0`

- See how to [Build and Start the Service](#Build-and-Start-the-Service)
- See [RESTful Web Endpoints](#RESTful-Web-Endpoints) on how to use the service

## Introduction

This project implements a reactive RESTful web service that provides various bigraph-style location models:

- grids, quadtrees, lines, etc.

as XML (Ecore-compliant bigraph models), or as JSON (custom).

Location models are typically used in reactive bigraph programs
that support cyber-physical applications or facilitate cyber-physical consistency checks.

### Features

#### Location Models

The following location models are currently supported:

- Points (randomly within a given boundary)
- Uniform n×m grids (points are evenly spaced)
- Quadtrees with boundary
- Lines (Interpolated with stepsize)

These models can be parameterized (e.g., number of cells per row, maximum depth, …).

#### Ecore-compliant Models

Model output (XML) is realized using the Ecore standard from the Eclipse Modeling Framework (EMF).
Specifically, all bigraphical Ecore-based location models conform to [Bigraph Ecore Metamodel (BEM)](https://github.com/bigraph-toolkit-suite/bigraphs.bigraph-ecore-metamodel).

## RESTful Web Endpoints

### Retrieve the Bi-Spatial Metamodel

You can retrieve the Ecore-based metamodel used for all bi-spatial location models. 
This metamodel defines the structure and types of the instance models (e.g., grids, interpolated trajectories) that your application will load or validate.

```shell
curl http://localhost:8080/generate/metamodel?format=xml
```

This metamodel is required to:
- Validate location-based bigraph instance models.
- Enable model-driven tools (e.g., EMF, Eclipse) to work with generated content.
- Maintain consistency across different cyber-physical space representations.

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

### Interpolate Points

```shell
curl -X POST http://localhost:8080/generate/interpolated?format=xml \
  -H "Content-Type: application/json" \
  -d '{
    "points": [
        {"x": 0, "y": 0},
        {"x": 1, "y": 1},
        {"x": 2, "y": 2}
    ],
    "stepSizeX": 0.25,
    "stepSizeY": 0.25
  }'
```

Arguments:

- JSON (Default): `?format=json`

### Generate a Bi-Spatial Convex Shape

This endpoint creates a bi-spatial bigraph model based on a custom list of 2D points that define a convex polygon. 
The interior of the shape is rasterized using a specified step size, and a spatial location node is generated for each rasterized point.

This generates a convex bi-spatial structure from a list of polygon vertices:
```shell
curl -X POST http://localhost:8080/generate/convex \
  -H "Content-Type: application/json" \
  -d '{
        "stepSize": 0.25,
        "points": [
          { "x": 0.0, "y": 0.0 },
          { "x": -1.24, "y": 0.58 },
          { "x": 2.86, "y": 2.93 },
          { "x": 3.08, "y": 0.0 }
        ]
      }'
```

Arguments:

- Step Size (required): included in body → `"stepSize": 0.25`
- Format (default: XML): `?format=xml` or `?format=json`

Use Cases:

- Defining irregular regions of interest in a spatial simulation.
- Modeling bounded areas in drone or robot navigation maps.
- Loading custom-shaped digital twin spaces into your application.

### Generate a Uniform Bi-Spatial Grid

These endpoints allow you to generate a **uniform grid-based bi-spatial bigraph model**, which serves as a spatial structure of discrete locations. The grid can be used as a base layout for simulation, visualization, or as part of a digital twin model.

Example: Generate a **default 3x3 grid** with step size 1.0 in both directions, where the origin is at (x,y) = (0,0):

```shell
curl http://localhost:8080/generate/bigrid
curl "http://localhost:8080/generate/bigrid?rows=4&cols=2&format=xml"
```

To adjust the default values:

```shell
curl -X POST http://localhost:8080/generate/bigrid \
  -H "Content-Type: application/json" \
  -d '{"x":0,"y":0,"stepSizeX":1.5,"stepSizeY":2.0}'
```

Arguments:

- Rows (default: `3`): `?rows=3`
- Columns (default: `3`): `?cols=3`
- Format (default: JSON): `?format=json` or `?format=xml`



## Build and Start the Service

**1. Building:**
```
mvn clean package
```

**2. Running:**
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

## Contributing

Contributions are welcomed from the open source community! 
To get started:

- See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to propose changes or submit pull requests.

- Please review our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) to foster an inclusive and respectful environment.


## License

This project is licensed under the Apache License 2.0.

You are free to use, modify, and distribute this software in accordance with the license terms.