# CloudEvents REST Client

Spring Boot 4 Anwendung, die CloudEvents im **Binary Content Mode** via `RestClient` an `http://localhost:3000/api` sendet.

## Voraussetzungen

- Java 21+
- Maven 3.9+ (oder den enthaltenen Maven Wrapper `./mvnw` verwenden)

## Projektstruktur

```
src/main/java/com/example/
├── Application.java                 Spring Boot Hauptklasse
├── client/
│   └── CloudEventClient.java       REST-Client zum Senden von CloudEvents
├── config/
│   └── RestClientConfig.java       RestClient-Bean mit Error-Handling
├── exception/
│   └── ApiClientException.java     Exception fuer HTTP-Fehler (nicht 200/201/202)
└── model/
    └── Payload.java                 Payload-Record (id, name)

src/test/java/com/example/client/
├── CloudEventClientTest.java              Unit-Tests (@RestClientTest)
└── CloudEventClientIntegrationTest.java   Integration-Tests (@SpringBootTest + WireMock)
```

## CloudEvent-Konfiguration

Das gesendete CloudEvent verwendet Binary Encoding -- die Attribute werden als HTTP-Header transportiert:

| Header           | Wert                        |
|------------------|-----------------------------|
| `ce-specversion` | `1.0`                       |
| `ce-id`          | `4000`                      |
| `ce-type`        | `com.example.sending`       |
| `ce-source`      | `/cloud-event-client`       |
| `ce-time`        | aktuelle Zeit (ISO 8601)    |
| `Content-Type`   | `application/json`          |

**Payload (HTTP Body):**

```json
{ "id": 123, "name": "hello" }
```

## Error-Handling

Der `RestClient` behandelt alle HTTP-Statuscodes ausser **200**, **201** und **202** als Fehler und wirft eine `ApiClientException` mit Statuscode und Response-Body.

## Build und Tests

```bash
# Kompilieren
./mvnw compile

# Alle Tests ausfuehren
./mvnw test

# Nur Unit-Tests
./mvnw test -Dtest=CloudEventClientTest

# Nur Integration-Tests
./mvnw test -Dtest=CloudEventClientIntegrationTest
```

## Konfiguration

Die Ziel-URL ist ueber `application.properties` konfigurierbar:

```properties
api.base-url=http://localhost:3000/api
```

## Abhaengigkeiten

| Abhaengigkeit             | Version | Zweck                                      |
|---------------------------|---------|---------------------------------------------|
| Spring Boot Starter Web   | 4.0.3   | RestClient, Spring MVC                      |
| CloudEvents Spring        | 4.0.1   | `CloudEventHttpUtils` fuer Binary Encoding  |
| Spring Boot Starter Test  | 4.0.3   | JUnit 5, MockRestServiceServer              |
| WireMock Spring Boot      | 4.1.0   | HTTP-Mock fuer Integration-Tests            |
