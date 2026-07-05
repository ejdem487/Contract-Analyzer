# Project State

## Current Status

- Spring Boot REST API for Czech contract analysis.
- Uses Gemini 2.5 Flash through `GeminiService`.
- Stores analyses and legal issues in PostgreSQL through Spring Data JPA.
- Fetches Czech legal context from `zakonyprolidi.cz`.
- Swagger UI is configured at `/swagger-ui.html`.

## Recently Reviewed

- Codebase structure and main flow were reviewed.
- `PromptBuilder` was reviewed for prompt injection risks.
- Main weak point found: raw user, document, and external text is concatenated into prompts.

## Known Environment Issue

- `pom.xml` currently sets Java version to 25.
- Local Java is OpenJDK 21.0.11.
- `./mvnw test` fails before tests with `release version 25 not supported`.

## Active Concerns

- Prompt injection protection is not implemented.
- AI outputs are parsed as JSON but not deeply validated after parsing.
- `RuntimeException` currently maps to HTTP 500, including not-found cases.
- `LawSourceService` catches all exceptions and returns empty strings, which hides failures.
- `zakonyprolidi.cz` API key is hardcoded as `test`.

