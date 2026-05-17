---
title: Add Decompose component and route tests for Auth/SummaryList/Collections/Digest/Settings
status: backlog
area: kmp
priority: high
owner: Senior Mobile Shells Engineer (Ratatoskr Client)
paperclip: POY-274
blocks: []
blocked_by: []
created: 2026-05-12
updated: 2026-05-17
---

- [ ] #task Add Decompose component and route tests for Auth/SummaryList/Collections/Digest/Settings #repo/ratatoskr-client #area/kmp #status/backlog ⏫ [paperclip:POY-274]

Filed from [POY-255](/POY/issues/POY-255) QA gate (row C13).

## Objective

Cover Decompose components AuthComponent, SummaryListComponent, CollectionsComponent, DigestComponent, and SettingsComponent: route push/pop transitions, retained ViewModel survival across component re-create, and back-handler behavior. The current ArchitectureBoundaryTest only enforces import rules, not runtime navigation.

## Owner

Senior Mobile Shells Engineer (Ratatoskr Client).

## Expected artifact

- New tests under feature/<name>/src/commonTest/kotlin/com/po4yka/ratatoskr/.../presentation/navigation/<Name>ComponentTest.kt using Decompose TestLifecycle / TestStateKeeper.
- Cases per component: instantiation under TestLifecycle, child route push and pop, retained ViewModel reuse on component recreate, terminal back closes the stack.
- Run via: ./gradlew :feature:summary:allTests :feature:auth:allTests :feature:collections:allTests :feature:digest:allTests :feature:settings:allTests

## Definition of done

- Tests pass on commonTest + iosSimulatorArm64Test for each module.
- Tests fail when a child route is removed or ViewModel retention wiring is regressed.
