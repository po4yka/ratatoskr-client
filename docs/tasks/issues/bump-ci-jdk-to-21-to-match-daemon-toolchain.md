---
title: Bump CI JDK to 21 to match Gradle daemon toolchain
status: backlog
area: ci
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Bump CI JDK to 21 to match Gradle daemon toolchain #repo/ratatoskr-client #area/ci #status/backlog ⏫

Filed from the 2026-05-17 deep audit (build H).

## Objective

`gradle/gradle-daemon-jvm.properties:12` declares `toolchainVersion=21`, but every CI workflow installs only JDK 17 (`.github/workflows/ci.yml:27-30`, `pr-validation.yml:23-27`, `ios.yml:16-20`, `code-quality.yml:23-27`). Gradle 9.x boots on 17 but the daemon-JVM mismatch forces Foojay toolchain auto-download on each run — slow and network-flaky. Either install JDK 21 in CI or drop the `toolchainVersion=21` requirement.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Update each `actions/setup-java@vN` step: `java-version: '21'` and `distribution: 'temurin'`.
- Verify Gradle wrapper still works.
- Update README "Build And Run" if it mentions JDK 17.

## Constraints

- Local-dev JDK requirement should remain `>=21` (CLAUDE.md prerequisites section).

## Definition of done

- All CI workflows install JDK 21.
- No more Foojay auto-download log lines in green CI runs.
- README + onboarding docs consistent.
