# Architecture

## Main Flow

1. `ContractAnalysisController` receives REST requests under `/api/contracts`.
2. `ContractAnalysisService` asks Gemini to identify contract type, legal topics, laws, and paragraphs.
3. `LawSourceService` fetches law titles and paragraph text from `zakonyprolidi.cz`.
4. `ContractAnalysisService` builds a second prompt and asks Gemini for structured risk analysis.
5. The analysis and legal issues are saved through JPA repositories.
6. `ChatAskService` answers follow-up questions using saved analysis context.

## Key Files

- `src/main/java/com/agrp/contract_analyzer/controller/ContractAnalysisController.java`
- `src/main/java/com/agrp/contract_analyzer/service/ContractAnalysisService.java`
- `src/main/java/com/agrp/contract_analyzer/service/ChatAskService.java`
- `src/main/java/com/agrp/contract_analyzer/service/GeminiService.java`
- `src/main/java/com/agrp/contract_analyzer/service/LawSourceService.java`
- `src/main/java/com/agrp/contract_analyzer/service/PromptBuilder.java`
- `src/main/java/com/agrp/contract_analyzer/model/ContractAnalysis.java`
- `src/main/java/com/agrp/contract_analyzer/model/LegalIssue.java`

## Current API Endpoints

- `POST /api/contracts/analyze`
- `GET /api/contracts`
- `GET /api/contracts/{id}`
- `DELETE /api/contracts/{id}`
- `POST /api/contracts/{id}/chat`

