---
title: Fix shared TestDispatcher singleton and AppConfig test globals
status: backlog
area: testing
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Fix shared TestDispatcher singleton and AppConfig test globals #repo/ratatoskr-client #area/testing #status/backlog 🔼

Filed from the 2026-05-17 deep audit (test quality).

## Objective

Two latent flakiness sources before the test count grows:

1. `core/common/src/commonTest/kotlin/com/po4yka/ratatoskr/util/TestDispatchers.kt:20` exposes `StandardTestDispatcher` as an `object`-level singleton. `StandardTestDispatcher` carries internal mutable state (pending-task queue). Tests sharing one instance via `CoroutineTestBase` can leak coroutines from one test into another.
2. `core/common/src/commonTest/kotlin/com/po4yka/ratatoskr/util/config/AppConfigTest.kt:9` mutates `var` fields on the `AppConfig` global object directly. The `try/finally` restores values, but global state is not thread-safe — running tests in parallel will produce flaky results.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Refactor `TestDispatchers` to construct a fresh `StandardTestDispatcher()` per test invocation (e.g., via a function rather than a singleton property).
- Either (a) make `AppConfig` injectable (pass a config map / interface to consumers) and update `AppConfigTest` to use a test-local instance, or (b) document the no-parallel restriction and enforce it via Gradle test config.

## Constraints

- `CoroutineTestBase` API must not regress; existing tests must keep passing.

## Definition of done

- A new test that exercises both dispatchers concurrently does not leak state.
- `./gradlew :core:common:allTests --parallel` is safe.
