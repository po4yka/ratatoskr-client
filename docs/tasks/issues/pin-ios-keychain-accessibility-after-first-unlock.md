---
title: Pin iOS keychain accessibility to AfterFirstUnlockThisDeviceOnly
status: backlog
area: ops
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Pin iOS keychain accessibility to AfterFirstUnlockThisDeviceOnly #repo/ratatoskr-client #area/ops #status/backlog ⏫

Filed from the 2026-05-17 deep audit (security H4).

## Objective

`core/data/src/iosMain/.../IosSecureStorage.kt:10` constructs `KeychainSettings(service = "com.po4yka.ratatoskr.auth")` without an explicit `kSecAttrAccessible` value. The default chosen by `russhwolf/multiplatform-settings` is `kSecAttrAccessibleWhenUnlocked` (acceptable) but the default `kSecAttrSynchronizable` allows the item to sync via iCloud Keychain to other devices the user owns — violating MASVS-STORAGE-1.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Explicit `Accessibility.AfterFirstUnlockThisDeviceOnly` passed to `KeychainSettings` (verify `multiplatform-settings` exposes this; if not, drop to raw `SecItemAdd` with `kSecAttrAccessible = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly` and `kSecAttrSynchronizable = false`).
- Decide explicitly whether share extension + widget share keychain access; if yes, add a `keychain-access-groups` entitlement in `iosApp/iosApp/Ratatoskr.entitlements` and `ShareExtension/ShareExtension.entitlements`.
- Migration note for existing installs (re-store tokens on next launch with the new accessibility class).

## Constraints

- Lockdown on next install only — graceful re-auth if reads fail post-migration.
- Maintain compatibility with multiplatform-settings if possible to avoid hand-rolled Keychain code.

## Definition of done

- Tokens no longer sync to iCloud Keychain.
- Verified by installing on two devices under the same Apple ID — second device cannot read the first device's tokens.
