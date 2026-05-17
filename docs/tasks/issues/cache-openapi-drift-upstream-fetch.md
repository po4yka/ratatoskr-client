---
title: Cache OpenAPI drift upstream fetch with offline fallback
status: backlog
area: ci
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Cache OpenAPI drift upstream fetch with offline fallback #repo/ratatoskr-client #area/ci #status/backlog ⏫

Filed from the 2026-05-17 deep audit (build H).

## Objective

`core/api-generated/build.gradle.kts:119-163` `CheckOpenApiDriftTask` hits `https://raw.githubusercontent.com/...` on every CI run with `retries=0` (`gradle/wrapper/gradle-wrapper.properties:6`). A transient GitHub Raw outage breaks the merge gate. The task is also not declared as having the URL as a Gradle `inputs.property`, so the build cache cannot invalidate properly when the lock changes.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Cache the fetched upstream YAML keyed by `tools/openapi.lock` SHA under `.gradle/openapi-cache/<sha>/mobile_api.yaml`.
- Add `inputs.property("upstreamSha", lockFile.readText().sha())` and `inputs.file(lockFile)` to the task so cache invalidates correctly.
- On fetch failure, fall back to cached copy with a clear warning (still byte-compare).
- Optional: add `--offline` aware behavior — skip the network and use cache.

## Constraints

- Drift gate must remain strict (byte-compare).
- Cache must respect the SHA so a lockfile update genuinely re-fetches.

## Definition of done

- A simulated GitHub Raw 503 does not fail the build when a cached copy matching the lock SHA exists.
- New lock SHA forces a re-fetch.
- Build cache hits on the task when lock unchanged.
