# ARM Controller

A multi-module Java 17 project for dynamic timeout and retry patterns parameneters adjustment, with:
- **`arm-controller`**: a Spring Boot web application (service).
- **`arm-starter`**: a reusable starter/library module you can publish and consume from other projects.

---

## About

This repository contains a Gradle multi-project build centered around an **ARM Controller** service built with **Spring Boot**.

`arm-controller` is responsible for making algorithmic decisions on the pattern adjustments, based on Promeheus metrics and SLO targets.
`arm-starter` is a library for Spring Cloud API Gateway layer that has to be used as a dependency.

---

## Tech stack

### Core
- Java 17
- Gradle (wrapper)

### `arm-controller` module
- Spring Boot (web)
- JUnit 5 + Spring Boot Test
- Cucumber (BDD tests)
- JaCoCo (coverage)
- PIT (mutation testing)
- SonarQube plugin support

### `arm-starter` module
- Published as a Java library (`maven-publish`)
- Uses Spring dependency BOMs
- Includes common utilities (e.g., Apache Commons Lang, Resilience4j, Spring Cloud Gateway)

---

## Getting started

### Prerequisites
- **JDK 17** installed
- No need to install Gradle manually (wrapper is included)

### Build everything
`./gradlew clean build`

### Run tests
`./gradlew test`

### Run the service (ARM-Controller)
`./gradlew :arm-controller:bootRun`

## Authors and Contributors

- **Main Contributor:** Alexey Korovko  
  *Student at SPbPU ICSC*
- **Advisor and minor contributor:** Vladimir A. Parkhomenko  
  *Senior Lecturer at SPbPU ICSC*

## Warranty

The contributors provide **no warranty** for the use of this software. Use it at your own risk.

## License

This project is open for use in educational purposes and is licensed under the [MIT License](LICENSE).
