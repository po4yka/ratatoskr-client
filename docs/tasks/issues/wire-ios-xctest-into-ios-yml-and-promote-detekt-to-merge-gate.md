---
title: Wire iOS XCTest into ios.yml and promote detekt to merge gate
status: doing
area: ci
priority: high
owner: Senior Build Gradle CI Engineer
paperclip: POY-276
blocks: []
blocked_by: []
created: 2026-05-12
updated: 2026-05-17
---

- [ ] #task Wire iOS XCTest into ios.yml and promote detekt to merge gate #repo/ratatoskr-client #area/ci #status/doing ⏫ [paperclip:POY-276]

Filed from [POY-255](/POY/issues/POY-255) QA gate (rows C2 and C15).

## Objective

Two CI changes that close gating gaps without new test code:

1. iosApp/iosAppUITests (AuthViewTests, NavigationTests) currently exist but are never invoked in CI. Add an xcodebuild test step to .github/workflows/ios.yml on macos-latest:
   xcodebuild test -workspace iosApp/iosApp.xcworkspace -scheme iosApp -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 15'

2. .github/workflows/code-quality.yml runs detekt as informational (continue-on-error). pr-validation.yml does not run detekt at all. Promote detekt to a hard merge gate by either: (a) adding detekt to pr-validation.yml validation-summary required jobs, or (b) removing continue-on-error from code-quality.yml kotlin-lint AND making it required in branch protection.

## Owner

Senior Build Gradle CI Engineer.

## Expected artifact

- Updated workflow YAML(s).
- Updated branch protection requirements documented in repo README or .github/branch-protection.md.

## Definition of done

- A PR that breaks an XCTest assertion fails CI.
- A PR with a detekt error fails CI.
