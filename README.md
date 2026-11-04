# Payment Processing Platform

A personal Java and Spring Boot project exploring account-to-account payments and ledger design, with attention to transaction consistency, concurrency, idempotency, and monetary precision. It documents proposed designs separately from behavior currently implemented in the application.

## Design

- [Transfer design](docs/design/transfer-design.md) — proposed account model, ERD, transfer invariants, and unresolved design decisions.
- [Architecture decision records](docs/adr/) — rationale and trade-offs for significant technical decisions.

## Technology

Java 21 · Spring Boot 4.1.1 · Maven · PostgreSQL 17 · Flyway · Spring Data JPA · OpenAPI/Swagger · Docker Compose

## License

Licensed under the [MIT License](LICENSE).
