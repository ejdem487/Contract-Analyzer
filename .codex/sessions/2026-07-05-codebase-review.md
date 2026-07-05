# 2026-07-05 Codebase Review

## Summary

The repository is a small Spring Boot REST backend for AI-assisted Czech contract analysis.

## Main Findings

- REST controller exposes contract analysis CRUD-like endpoints and a chat endpoint.
- `ContractAnalysisService` orchestrates the AI analysis flow.
- `PromptBuilder` directly concatenates untrusted text into prompts.
- `LawSourceService` fetches legal context but hides all failures.
- Global exception handling maps all runtime failures to HTTP 500.

## Build Check

`./mvnw test` was attempted.

Result: failed before tests because `pom.xml` requires Java 25 and local Java is 21.

