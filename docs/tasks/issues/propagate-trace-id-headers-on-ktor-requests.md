---
title: Propagate trace-id headers on Ktor requests
status: backlog
area: observability
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Propagate trace-id headers on Ktor requests #repo/ratatoskr-client #area/observability #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Observability).

## Objective

When a user reports "the app failed at 14:32", finding the corresponding backend log is currently a needle-in-haystack. Attach a client-generated trace id (`X-Request-Id` + W3C `traceparent`) to every request so client + backend logs are linkable.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New Ktor plugin `TraceHeadersPlugin` registered in both `core/data/.../ApiClient.kt` and `core/api-generated/.../bootstrap/GeneratedApiBootstrap.kt`.
- Per-call UUID stored in MDC-equivalent for log correlation.
- W3C-compliant `traceparent` header (`00-<32hex>-<16hex>-01`).
- Settings → Debug screen surfaces the current trace id so users can quote it in support.

## Constraints

- Trace id is generated client-side (no backend dependency).
- Header values are ASCII; lower-case hex per spec.
- Do not log the trace id at INFO+ (debug only) — it's a debugging aid.

## Definition of done

- Every outgoing Ktor request carries both `X-Request-Id` and `traceparent`.
- Settings → Debug → "Recent trace" copies the last 5 trace ids to clipboard.
- Backend logs from the same session correlate via the same id.
