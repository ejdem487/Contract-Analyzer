# Security Notes

## AI Security Rules

- Contract text is untrusted input.
- Chat questions are untrusted input.
- External law source text is untrusted input.
- Gemini output is untrusted until validated by deterministic Java code.
- Do not rely only on prompt wording for security.
- Keep tool and API calls in application code, not under model control.

## Prompt Injection Direction

Preferred approach:

1. Validate input length and required fields.
2. Detect suspicious prompt injection patterns.
3. Treat suspicious contract text differently from suspicious chat instructions.
4. Use structured JSON data blocks in prompts.
5. Tell the model that document text is data, not instructions.
6. Validate structured output before saving to the database.
7. Add adversarial tests.

## Contract Text Handling

Do not aggressively mutate legal contract text.

Reason: a real contract can legitimately contain words such as "instruction", "ignore", "override", or "system". For contract bodies, suspicious content should usually be flagged, isolated, and treated as data.

## Chat Input Handling

Be stricter with chat questions.

Reason: chat questions are direct user instructions, not source documents. Prompt-leak, role-override, or system-prompt extraction requests can be blocked.

