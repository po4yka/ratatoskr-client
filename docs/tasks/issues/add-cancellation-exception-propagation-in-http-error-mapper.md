---
title: Add CancellationException propagation in HttpErrorMapper
status: backlog
area: kmp
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add CancellationException propagation in HttpErrorMapper #repo/ratatoskr-client #area/kmp #status/backlog 🔼

Filed from the 2026-05-17 deep audit (code M8).

## Objective

`core/data/.../HttpErrorMapper.kt:109-120` catches `Exception` after specific cases and re-wraps as `AppError.UnknownError`. On JVM/Native, `kotlinx.coroutines.CancellationException : IllegalStateException : Exception` — so coroutine cancellations get silently converted to `UnknownError` and never propagate, breaking structured concurrency.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Add `catch (e: CancellationException) { throw e }` as the **first** catch clause in `handleApiError`.
- Audit other broad `catch (Exception)` / `catch (Throwable)` sites for the same bug (likely candidates: `SyncRepositoryImpl`, `SummaryDetailViewModel`).

## Constraints

- No behavior change for non-cancellation exceptions.

## Definition of done

- New test: invoke an HTTP call that throws `CancellationException`; assert it propagates instead of becoming `AppError.UnknownError`.
- Audit comment in CLAUDE.md / coding guidelines: "always rethrow CancellationException first."
