# TODO

## High Priority

- Add a `PromptInjectionGuard`.
- Add max sizes for `originalContractText` and `revisedContractText`.
- Refactor `PromptBuilder` to use JSON-escaped data blocks.
- Validate AI output after JSON parsing.
- Add tests for prompt injection payloads.

## Medium Priority

- Return 404 for missing analysis.
- Stop swallowing all law source errors silently.
- Move the `zakonyprolidi.cz` API key into configuration.
- Consider timeout configuration for `RestTemplate`.

## Low Priority

- Clean up formatting and extra blank lines in service classes.
- Review whether `jackson-module-kotlin` is needed in a Java-only project.

