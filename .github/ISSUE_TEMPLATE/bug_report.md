---
name: Bug Report
about: Report a bug to help us improve
title: '[BUG] '
labels: bug
assignees: ''
---

## Bug Description

<!-- A clear and concise description of what the bug is -->

## Steps to Reproduce

1. Go to '...'
2. Tap on '...'
3. Scroll down to '...'
4. See error

## Expected Behavior

<!-- A clear and concise description of what you expected to happen -->

## Actual Behavior

<!-- A clear and concise description of what actually happened -->

## Screenshots/Videos

<!-- If applicable, add screenshots or screen recordings to help explain your problem -->

## Environment

**Platform**: <!-- Android / iOS / Desktop / Both -->

**Device Information**:
- Device: <!-- e.g., Pixel 7, iPhone 15 Pro, Linux desktop -->
- OS Version: <!-- e.g., Android 14, iOS 17.0, macOS 14.5 -->
- App Version: <!-- e.g., 0.1.0 -->
- Build flavor: <!-- debug / release -->

**Build environment** (only required for build-time bugs):
- Kotlin: <!-- from gradle/libs.versions.toml — currently 2.3.20 -->
- Gradle: <!-- from gradle/wrapper/gradle-wrapper.properties — currently 9.4.1 -->
- AGP: <!-- from build-logic/build.gradle.kts — currently 9.0.1 -->
- JDK: <!-- e.g., Temurin 17.0.10 -->

**For Android**:
- Android API Level: <!-- e.g., API 34 -->
- Device Manufacturer: <!-- e.g., Google, Samsung -->

**For iOS**:
- iOS Version: <!-- e.g., 17.0.1 -->
- Device Model: <!-- e.g., iPhone 15 Pro -->
- Xcode version: <!-- e.g., 15.4 -->

**For sync / auth bugs**, also include:
- Network condition: <!-- WiFi / cellular / offline / VPN / proxy -->
- Backend URL: <!-- value of api.base.url in local.properties -->
- Whether the failure reproduces against `https://api.ratatoskr.po4yka.com` vs a local backend

## Logs/Error Messages

<!-- If applicable, paste relevant logs or error messages -->

```
Paste logs here
```

## Additional Context

<!-- Add any other context about the problem here -->

## Possible Solution

<!-- Optional: suggest a fix or reason for the bug -->

## Reproducibility

- [ ] Always reproducible
- [ ] Intermittent
- [ ] Only once

## Severity

- [ ] Critical - App crashes or data loss
- [ ] High - Major feature broken
- [ ] Medium - Feature partially broken
- [ ] Low - Minor issue or cosmetic

## Checklist

- [ ] I have searched existing issues to ensure this is not a duplicate
- [ ] I have provided all the requested information
- [ ] I have tested on the latest version of the app
- [ ] I can reproduce this issue consistently
