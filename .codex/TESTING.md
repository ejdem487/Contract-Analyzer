# Testing

## Current Test Status

`./mvnw test` currently fails during compilation because the project requires Java 25 and the local environment has Java 21.

Error:

```text
release version 25 not supported
```

## Expected Verification Command

Use:

```bash
./mvnw test
```

## Security Test Ideas

Add tests for:

- direct prompt injection in chat input
- prompt injection embedded in `originalContractText`
- prompt injection embedded in `revisedContractText`
- Czech-language variants such as `ignoruj predchozi instrukce`
- encoded payloads such as base64 or hex
- AI output containing unexpected URLs, Markdown image links, or prompt leak text

