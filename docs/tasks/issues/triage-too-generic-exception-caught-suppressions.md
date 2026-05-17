---
title: Triage TooGenericExceptionCaught suppressions
status: backlog
area: kmp
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Triage TooGenericExceptionCaught suppressions #repo/ratatoskr-client #area/kmp #status/backlog 🔽

Filed from the 2026-05-17 deep audit (code L1).

## Objective

The repo has 67 `@Suppress("TooGenericExceptionCaught")` annotations — each marks a place where Detekt was silenced instead of the catch tightened. Concentrations: `SummaryDetailViewModel`, `SummaryListViewModel`, `DigestViewModel` (6+ each). Narrow these to the specific exception types backed by `AppError` (`HttpRequestTimeoutException`, `IOException`, `ClientRequestException`, etc.).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- One PR per top-offender ViewModel that tightens its catches.
- Detekt suppression count tracked in the audit summary at start and end.
- Document in coding guidelines: "prefer `catch (e: AppError)` or specific HTTP exceptions over `catch (Exception)`; always rethrow `CancellationException` first."

## Constraints

- Do not change observable error-handling behavior — same user-facing error message, same retry policy.
- Land after `add-cancellation-exception-propagation-in-http-error-mapper` so the rethrow rule is enforced.

## Definition of done

- `@Suppress("TooGenericExceptionCaught")` count down by ≥75% from baseline of 67.
- No regression in error-handling tests.
