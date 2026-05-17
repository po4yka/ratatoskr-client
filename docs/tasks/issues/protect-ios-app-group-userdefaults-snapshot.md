---
title: Protect iOS app-group UserDefaults snapshot
status: backlog
area: ops
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Protect iOS app-group UserDefaults snapshot #repo/ratatoskr-client #area/ops #status/backlog 🔼

Filed from the 2026-05-17 deep audit (security M5).

## Objective

`iosApp/SharedSupport/AppGroupContract.swift:39-56` writes shared URLs and `RecentSummariesSnapshot` (summary IDs, titles, excerpts, domains, reading times) to `UserDefaults(suiteName: "group.com.po4yka.ratatoskr")` in plaintext. On jailbroken devices the app-group container is filesystem-accessible to any process with the right keychain access group; backups (iCloud / iTunes) include the data unless explicitly excluded. MASVS-STORAGE-2.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Set file protection class `NSFileProtectionCompleteUntilFirstUserAuthentication` on the app-group container's snapshot file.
- Mark `URLResourceKey.isExcludedFromBackupKey = true` on the snapshot path.
- Consider symmetric encryption of the snapshot blob with a key stored in keychain (shared access group) — defer to a follow-up if implementation cost is high.

## Constraints

- Widget + share extension must still read the snapshot post-first-unlock.

## Definition of done

- Snapshot file unreadable before first unlock.
- Snapshot not present in iTunes backup of the device.
- Widget + share extension still function after first unlock.
