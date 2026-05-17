---
title: Add SecureStorage round-trip and AEAD key persistence tests
status: doing
area: auth
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
paperclip: POY-272
blocks: []
blocked_by: []
created: 2026-05-12
updated: 2026-05-17
---

- [ ] #task Add SecureStorage round-trip and AEAD key persistence tests #repo/ratatoskr-client #area/auth #status/doing ⏫ [paperclip:POY-272]

Filed from [POY-255](/POY/issues/POY-255) QA gate (row C11). Coordinate with Security Engineer ([POY-257](/POY/issues/POY-257)).

## Objective

Prove tokens written via SecureStorage round-trip on Android (AndroidSecureStorage with Tink AEAD + DataStore secure_prefs_v3) and iOS (IosSecureStorage with KeychainSettings, service com.po4yka.ratatoskr.auth). Prove AEAD key material persists across DataStore reads. Prove clear() removes both access and refresh tokens.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Android instrumented or Robolectric test under core/data/src/androidUnitTest verifying AndroidSecureStorage write→read→clear round-trip, plus AEAD key reuse across storage instances.
- iOS test under core/data/src/iosTest verifying IosSecureStorage write→read→clear round-trip via KeychainSettings.
- Tests must not assume plaintext on disk and must not log token values.
- Run via: ./gradlew :core:data:allTests

## Constraints

- No real Keychain access on CI iOS sim is fine (KeychainSettings supports simulator).
- Do not exfiltrate any captured token values into logs or test assertions; assert opacity.

## Definition of done

- Tests pass on the same CI lanes as build-all in pr-validation.yml.
- Tests fail if AEAD key generation is bypassed or tokens are written to plaintext fallback.
