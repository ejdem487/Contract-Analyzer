# 2026-07-05 Prompt Injection Review

## Scope

Reviewed `PromptBuilder` and the AI flow for prompt injection risk.

## Key Risk

Raw contract text, chat text, legal context, and LLM-derived values are inserted directly into prompts.

## Recommended Direction

- Add deterministic input screening.
- Use JSON-escaped data blocks in prompts.
- Clearly mark all contract and external law text as untrusted data.
- Validate AI output after parsing.
- Add adversarial tests for prompt injection attempts.

## Important Distinction

Contract text should usually be flagged rather than mutated or blocked, because legal documents may contain instruction-like language.

Chat input can be blocked more aggressively because it is a direct instruction channel.

