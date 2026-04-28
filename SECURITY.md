# Security Policy

## Supported versions

Only the current `main` branch and the most recent tagged release of
the mobile client receive security updates. Earlier releases are not
patched.

## Reporting a vulnerability

**Please do not report security vulnerabilities through public GitHub
issues, discussions, or pull requests.**

Instead, email **npochaev@gara.ge** with:

- A clear description of the vulnerability and its impact
- Steps to reproduce, including the affected build (debug/release),
  platform (Android/iOS), OS version, and app version
- Any proof-of-concept code, logs, or screenshots that help triage
- Whether you'd like credit in the eventual disclosure

I'll acknowledge the report within **3 business days** and work with
you on a coordinated disclosure timeline. Reports that include a
proposed fix or mitigation get prioritized.

## Scope

In scope for this policy:

- The Ratatoskr Client mobile app (Android + iOS) and its Compose
  Multiplatform shared code
- The desktop dev target (`composeApp/runDesktop`) — note that
  desktop secure storage is intentionally in-memory and is documented
  as DEVELOPMENT ONLY
- Build tooling that ships with the repo (`build-logic/`, the GitHub
  Actions workflows under `.github/workflows/`)

Out of scope:

- The FastAPI backend (`po4yka/ratatoskr`) — file vulnerabilities
  there against the [backend repo's security policy](https://github.com/po4yka/ratatoskr)
- Issues in third-party dependencies that are already addressed in
  upstream releases — please open an issue or PR to bump the
  dependency instead
- Reports about the development desktop target's lack of encryption
  (this is documented behavior, see `DesktopSecureStorage.kt`)

## Engineering details

For the implementation-side threat model, secure-storage choices
(Tink AEAD on Android, Keychain on iOS), token-refresh logic, and
the security checklist contributors should follow, see
[`docs/SECURITY.md`](docs/SECURITY.md).
