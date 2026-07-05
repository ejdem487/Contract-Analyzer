# Decisions

## AI Input Handling

Decision: Do not mutate legal contract text aggressively.

Reason: the system analyzes legal documents. A real document may contain text that looks instruction-like, and altering the legal text could change the analysis.

## Chat Input Handling

Decision: be stricter with chat questions than with contract bodies.

Reason: chat questions are direct instructions to the AI assistant. Prompt-leak or role-override attempts are not valid legal analysis questions.

## Agent Memory

Decision: keep root `AGENTS.md` as the startup pointer and store detailed memory under `.codex/*.md`.

Reason: `AGENTS.md` is short and visible, while `.codex/` can hold longer project notes without cluttering the repository root.

