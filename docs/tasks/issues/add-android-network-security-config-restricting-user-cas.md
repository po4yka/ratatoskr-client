---
title: Add Android network_security_config restricting user CAs
status: backlog
area: ops
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Android network_security_config restricting user CAs #repo/ratatoskr-client #area/ops #status/backlog ⏫

Filed from the 2026-05-17 deep audit (security H3).

## Objective

`androidApp/src/main/AndroidManifest.xml:8-17` declares `usesCleartextTraffic="false"` (good) but does **not** reference a `networkSecurityConfig`. No `res/xml/network_security_config.xml` exists. On Android 7+, the default trust anchor set includes user-installed CAs — one sideloaded CA on the device exposes all traffic, even before certificate pinning lands.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `androidApp/src/main/res/xml/network_security_config.xml`:
  - `<base-config cleartextTrafficPermitted="false"><trust-anchors><certificates src="system"/></trust-anchors></base-config>` (no `user`).
  - `<domain-config>` for `api.ratatoskr.po4yka.com` with a `<pin-set>` (coordinate with the pinning task).
- `<application android:networkSecurityConfig="@xml/network_security_config">` reference in the manifest.

## Constraints

- Debug builds may allow `user` trust anchors via `<debug-overrides>` to keep emulator dev workflow intact.
- Pin-set must support two pins (rotation).

## Definition of done

- Release APK refuses requests through a locally-trusted user CA.
- Verified manually on a real device with a custom CA installed.
- Pin-rotation procedure documented alongside the CertificatePinner task.
