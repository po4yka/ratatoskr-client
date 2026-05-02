# Active — ratatoskr-client

> `#status/todo` · `#status/doing` · `#status/review` tasks.


## doing

- [ ] #task Fix KMP sync-apply response DTOs to match backend contract #repo/ratatoskr-client #area/kmp #status/doing ⏫ [paperclip:POY-258]
  - Paperclip: POY-258 · assigned to: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
  
  Objective
  Update ratatoskr-client sync-apply response handling so it matches the current Ratatoskr backend contract.

  Context
  CTO contract map POY-253 found the release-blocking gap: backend `/v1/sync/apply` returns session-level `sessionId`, `results[]`, `conflicts[]`, and `hasMore` with camelCase aliases, while KMP expects an older `applied`, `server_version`, `new_server_version` style shape. Backend contract stands; KMP should adapt.

  Owner
  Senior KMP / Compose Multiplatform Engineer. Coordinate with CTO and QA Lead.

  Priority
  High.

  Parent issue or goal linkage
  Related: POY-253. Goal: Ratatoskr ecosystem mobile contract and release-readiness baseline. Project: ratatoskr-client. This is not a formal child issue because Paperclip rejected cross-project child linkage.

  Acceptance criteria
  - Update sync apply response DTOs/mappers/repository handling to consume backend `SyncApplyResponseData` and item result/conflict shape.
  - Preserve feature/sync ownership and do not leak transport DTOs into domain/UI layers.
  - Add or update focused tests for success, conflict, and `hasMore` cases.
  - Document any backend ambiguity back on POY-253 rather than changing backend shape.

  Expected artifact
  KMP client code change plus focused test evidence.

  Constraints
  Do not change backend contract or docs/openapi/mobile_api.yaml in this issue. Do not run live API calls. Follow ratatoskr-client architecture and Frost constraints.

  Risks
  Incorrect mapping can silently drop sync conflicts or corrupt offline-first pending operation state.

  Verification plan
  Run the smallest relevant feature/sync tests first; if unavailable, add targeted unit tests and report exact Gradle task.

  Definition of done
  KMP sync apply deserializes and maps the backend response shape, tests cover success/conflict/hasMore, and QA can include it in POY-255 release gate.

- [ ] #task Align KMP full-sync request with backend query contract #repo/ratatoskr-client #area/kmp #status/doing ⏫ [paperclip:POY-259]
  - Paperclip: POY-259 · assigned to: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
  
  Objective
  Remove or justify the unsupported `cursor` query parameter sent by ratatoskr-client full-sync calls.

  Context
  CTO contract map POY-253 found KMP `fullSync` sends a `cursor` query parameter, while backend full sync currently accepts `session_id` and `limit` only. The default path is to remove the unsupported client parameter unless CTO approves a backend contract change.

  Owner
  Senior KMP / Compose Multiplatform Engineer. Coordinate with CTO if any backend contract change is proposed.

  Priority
  High.

  Parent issue or goal linkage
  Related: POY-253. Goal: Ratatoskr ecosystem mobile contract and release-readiness baseline. Project: ratatoskr-client.

  Acceptance criteria
  - Confirm the current backend full-sync query contract from docs/openapi/mobile_api.yaml and router/model code.
  - Remove the unsupported `cursor` parameter from the KMP full-sync request path, or document why a backend contract change is required and block on CTO approval.
  - Add/update a focused test or API request-construction assertion if available.
  - Preserve feature/sync ownership and offline-first sync behavior.

  Expected artifact
  KMP client code change or explicit CTO-blocked contract-change note.

  Constraints
  Do not edit backend OpenAPI directly. Do not run live API calls.

  Risks
  Unsupported query parameters can hide client/server drift and complicate cache/ETag sync semantics.

  Verification plan
  Run the smallest relevant sync API/repository test; report exact Gradle task.

  Definition of done
  KMP full-sync request matches the backend contract or a deliberate backend contract-change issue exists with CTO ownership.

- [ ] #task Audit KMP search readiness and signals/aggregations release scope #repo/ratatoskr-client #area/kmp #status/doing 🔼 [paperclip:POY-262]
  - Paperclip: POY-262 · assigned to: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
  
  Objective
  Audit ratatoskr-client readiness for search DTOs and decide whether `/v1/signals` and `/v1/aggregations` are in or out of the next mobile release.

  Context
  CTO contract map POY-253 found search exists on both backend and KMP but needs endpoint-by-endpoint DTO verification. Backend also exposes `/v1/signals` and `/v1/aggregations`; no KMP API surface was found. This should be an explicit release-scope decision, not accidental omission.

  Owner
  Senior KMP / Compose Multiplatform Engineer. Coordinate with Product Manager and CTO.

  Priority
  Medium.

  Parent issue or goal linkage
  Related: POY-253 and POY-254. Goal: Ratatoskr ecosystem mobile contract and release-readiness baseline. Project: ratatoskr-client.

  Acceptance criteria
  - Verify KMP search DTOs and repository behavior against backend `/v1/search` and `/v1/search/semantic` parameters and response envelope.
  - Decide, with PM/CTO input, whether signals and aggregations are excluded from the next mobile release or require KMP surfaces now.
  - If excluded, document the release-scope decision in Paperclip and ensure no UI path implies availability.
  - If included, create concrete implementation issues with owner, API surface, tests, and UX acceptance criteria.

  Expected artifact
  Readiness audit comment plus any concrete follow-up issues.

  Constraints
  Do not implement signals/aggregations in this issue unless explicitly split and assigned. Do not change backend API contract.

  Risks
  Unclear release scope can produce broken navigation, missing API clients, or user-visible claims for unavailable features.

  Verification plan
  Static DTO/repository inspection and, where existing, focused KMP tests for search request/response mapping.

  Definition of done
  Search readiness is classified, and signals/aggregations are explicitly in-scope or out-of-scope for the next mobile release.

- [ ] #task KMP: add SecureStorage round-trip + AEAD key persistence tests #repo/ratatoskr-client #area/kmp #status/doing ⏫ [paperclip:POY-272]
  - Paperclip: POY-272 · assigned to: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
  
  Filed from [POY-255](/POY/issues/POY-255) QA gate (row C11). Coordinate with Security Engineer ([POY-257](/POY/issues/POY-257)).

  Objective
  Prove tokens written via SecureStorage round-trip on Android (AndroidSecureStorage with Tink AEAD + DataStore secure_prefs_v3) and iOS (IosSecureStorage with KeychainSettings, service com.po4yka.ratatoskr.auth). Prove AEAD key material persists across DataStore reads. Prove clear() removes both access and refresh tokens.

  Owner: Senior KMP/Compose Engineer (Ratatoskr Client).

  Expected artifact
  - Android instrumented or Robolectric test under core/data/src/androidUnitTest verifying AndroidSecureStorage write→read→clear round-trip, plus AEAD key reuse across storage instances.
  - iOS test under core/data/src/iosTest verifying IosSecureStorage write→read→clear round-trip via KeychainSettings.
  - Tests must not assume plaintext on disk and must not log token values.
  - Run via: ./gradlew :core:data:allTests

  Constraints
  - No real Keychain access on CI iOS sim is fine (KeychainSettings supports simulator).
  - Do not exfiltrate any captured token values into logs or test assertions; assert opacity.

  Definition of done
  - Tests pass on the same CI lanes as build-all in pr-validation.yml.
  - Tests fail if AEAD key generation is bypassed or tokens are written to plaintext fallback.

- [ ] #task KMP: add Ktor bearer refresh + token rotation tests #repo/ratatoskr-client #area/kmp #status/doing ⏫ [paperclip:POY-273]
  - Paperclip: POY-273 · assigned to: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
  
  Filed from [POY-255](/POY/issues/POY-255) QA gate (row C10).

  Objective
  Cover Ktor Auth bearer plugin behavior end-to-end so a 401 triggers a refresh against POST v1/auth/refresh, the new access + refresh tokens land in SecureStorage, the original request retries with the new bearer, and the response from the second attempt reaches the caller. Prove that a 5xx from refresh does not clear stored credentials and that 400/401/403 from refresh does.

  Owner: Senior KMP/Compose Engineer (Ratatoskr Client).

  Expected artifact
  - New tests under core/data/src/commonTest/kotlin/com/po4yka/ratatoskr/data/remote/ exercising ApiClient + KtorAuthApi via MockEngine.
  - Coverage cases: happy refresh on first 401; refresh failure with 5xx (tokens preserved); refresh failure with 401 (tokens cleared via existing shouldClearTokensAfterRefreshFailure predicate); single-flight refresh under concurrent in-flight requests.
  - Run via: ./gradlew :core:data:allTests
  - Wire-up: no new prod code required; tests should consume existing ApiClient factory.

  Constraints
  - No live network. Use MockEngine.
  - Do not weaken sanitizer guarantees already covered by ApiClientLogSanitizerTest.

  Definition of done
  - Tests live in core/data commonTest, passing on Android JVM and iosSimulatorArm64.
  - Tests fail meaningfully if Ktor Auth refresh wiring is removed or token rotation is bypassed.
  - Linked back from this issue and from [POY-255](/POY/issues/POY-255) qa-gate document.

- [ ] #task KMP: add Decompose component/route tests for Auth/SummaryList/Collections/Digest/Settings #repo/ratatoskr-client #area/kmp #status/doing ⏫ [paperclip:POY-274]
  - Paperclip: POY-274 · assigned to: Senior Mobile Shells Engineer (Ratatoskr Client)
  
  Filed from [POY-255](/POY/issues/POY-255) QA gate (row C13).

  Objective
  Cover Decompose components AuthComponent, SummaryListComponent, CollectionsComponent, DigestComponent, and SettingsComponent: route push/pop transitions, retained ViewModel survival across component re-create, and back-handler behavior. The current ArchitectureBoundaryTest only enforces import rules, not runtime navigation.

  Owner: Senior Mobile Shells Engineer (Ratatoskr Client).

  Expected artifact
  - New tests under feature/<name>/src/commonTest/kotlin/com/po4yka/ratatoskr/.../presentation/navigation/<Name>ComponentTest.kt using Decompose TestLifecycle / TestStateKeeper.
  - Cases per component: instantiation under TestLifecycle, child route push and pop, retained ViewModel reuse on component recreate, terminal back closes the stack.
  - Run via: ./gradlew :feature:summary:allTests :feature:auth:allTests :feature:collections:allTests :feature:digest:allTests :feature:settings:allTests

  Definition of done
  - Tests pass on commonTest + iosSimulatorArm64Test for each module.
  - Tests fail when a child route is removed or ViewModel retention wiring is regressed.

- [ ] #task KMP: add SyncRepositoryImpl integration tests + offline pending-op drain #repo/ratatoskr-client #area/kmp #status/doing ⏫ [paperclip:POY-275]
  - Paperclip: POY-275 · assigned to: Senior Feature Module Engineer (Ratatoskr Client)
  
  Filed from [POY-255](/POY/issues/POY-255) QA gate (rows C12 and C14).

  Objective
  Cover SyncRepositoryImpl end-to-end against a fake KtorSyncApi + SQLDelight in-memory database: bootstrap session, full sync resume from cursor, delta sync apply, idempotent re-apply of already-applied items, conflict count surfaced from PendingOperation handlers, and offline pending-op drain when the network returns.

  Owner: Senior Feature Module Engineer (Ratatoskr Client). Coordinate with KMP for shared fakes.

  Expected artifact
  - New tests under feature/sync/src/commonTest/kotlin/com/po4yka/ratatoskr/data/repository/SyncRepositoryImplTest.kt.
  - Cases: full sync first run; full sync resume with cursor; delta after full; apply with conflict; idempotent re-apply; pending op drained on next session; cleanup decision matches FullSyncCleanupDecisionTest contract.
  - Use existing SyncItemApplierRegistry + PendingOperationRouting test fakes; do not introduce new prod dependencies.
  - Run via: ./gradlew :feature:sync:allTests

  Definition of done
  - Tests pass on every active source set (commonTest + iosSimulatorArm64Test).
  - Tests fail when SyncRepositoryImpl regresses on cursor resume, idempotency, or conflict surfacing.

- [ ] #task KMP CI: wire iOS XCTest into ios.yml and promote detekt to merge gate #repo/ratatoskr-client #area/kmp #status/doing ⏫ [paperclip:POY-276]
  - Paperclip: POY-276 · assigned to: Senior Build Gradle CI Engineer
  
  Filed from [POY-255](/POY/issues/POY-255) QA gate (rows C2 and C15).

  Objective
  Two CI changes that close gating gaps without new test code:

  1. iosApp/iosAppUITests (AuthViewTests, NavigationTests) currently exist but are never invoked in CI. Add an xcodebuild test step to .github/workflows/ios.yml on macos-latest:
     xcodebuild test -workspace iosApp/iosApp.xcworkspace -scheme iosApp -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 15'

  2. .github/workflows/code-quality.yml runs detekt as informational (continue-on-error). pr-validation.yml does not run detekt at all. Promote detekt to a hard merge gate by either: (a) adding detekt to pr-validation.yml validation-summary required jobs, or (b) removing continue-on-error from code-quality.yml kotlin-lint AND making it required in branch protection.

  Owner: Senior Build Gradle CI Engineer.

  Expected artifact
  - Updated workflow YAML(s).
  - Updated branch protection requirements documented in repo README or .github/branch-protection.md.

  Definition of done
  - A PR that breaks an XCTest assertion fails CI.
  - A PR with a detekt error fails CI.
