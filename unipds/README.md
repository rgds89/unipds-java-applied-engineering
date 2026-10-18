# unipds

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/unipds-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): Build RESTful web services and APIs using Jakarta REST (formerly JAX-RS)
- OpenAPI Generator - REST Client Generator ([guide](https://docs.quarkiverse.io/quarkus-openapi-generator/dev/index.html)): Generation of Rest Clients based on OpenAPI specification files
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Generate OpenAPI schemas and serve Swagger UI for REST API documentation

## Provided Code

### OpenAPI Generator Client Codestart

Start to code with the OpenAPI Generator Client extension.

[Related guide section...](https://docs.quarkiverse.io/quarkus-openapi-generator/dev/client.html)

## Quarkus OpenAPI Generator Client

The Codestart adds automatically the `io.quarkus:quarkus-rest-client-jackson` dependency to a generated project, this dependency is required for generating the [REST Client](https://quarkus.io/guides/rest-client) classes. 

For more information about how to use the Quarkus OpenAPI Generator Client extension, please refer to [Quarkus OpenAPI Generator Client documentation](https://docs.quarkiverse.io/quarkus-openapi-generator/dev/client.html).
### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
