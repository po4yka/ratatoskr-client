---
title: Add TLS certificate pinning for Android and iOS HTTP clients
status: backlog
area: ops
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add TLS certificate pinning for Android and iOS HTTP clients #repo/ratatoskr-client #area/ops #status/backlog ⏫

Filed from the 2026-05-17 deep audit (security H2).

## Objective

`api.ratatoskr.po4yka.com` carries access + refresh JWTs. Both platforms use platform-default trust today: `core/data/src/androidMain/.../di/AndroidModule.kt:28` is `OkHttp.create()` and `core/data/src/iosMain/.../di/IosModule.kt:31` is `Darwin.create()`. The only reference to `CertificatePinner` in the repo is in `docs/SECURITY.md`. MASVS-NETWORK-1 requires pinning for sensitive endpoints; a single user-installed CA (corporate proxy, MITM) currently bypasses TLS protection for our tokens.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Android: `OkHttp.create { config { certificatePinner(CertificatePinner.Builder().add("api.ratatoskr.po4yka.com", "sha256/<leaf>").add("api.ratatoskr.po4yka.com", "sha256/<backup-intermediate>").build()) } }`.
- iOS: `Darwin { configureSession { delegate = … } }` with a `URLSessionDelegate` that runs SPKI hash comparison via `SecTrustEvaluateWithError` against the same two pins.
- Pins managed in a single shared `core/common` `AppConfig.Api.pins` source.
- Pin-rotation runbook in `docs/SECURITY.md` (or new section).

## Constraints

- Two pins (leaf + backup intermediate) to allow rotation without bricking installed builds.
- No pinning for non-prod environments configured by `api.base.url` override.

## Definition of done

- A MITM via locally-trusted proxy on both Android emulator and iOS simulator fails the TLS handshake.
- Pin-rotation procedure documented.
- No regression on first-run auth flows.
