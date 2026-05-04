# Contract Analyzer
One sentence — REST API that analyzes contract changes using AI and verifies referenced laws against the Czech legal database.

## How it works
4-step flow — law identification → verification via zakonyprolidi.cz → risk analysis → save to DB.

## Tech stack
- Java 25, Spring Boot 3.5
- Google Gemini 2.5 Flash
- PostgreSQL, Docker
- zakonyprolidi.cz API
- Swagger UI

## Getting started
1. docker-compose up -d
2. Set GEMINI_API_KEY env variable
3. ./mvnw spring-boot:run
4. Swagger UI at http://localhost:8080/swagger-ui.html
