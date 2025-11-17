# Unimplemented Features & Known Issues

This document tracks unimplemented features, stub implementations, and known issues in the Bite-Size Reader mobile client.

**Last Updated**: 2025-11-17
**Project Phase**: MVP Development (Early Stage)

---

## Executive Summary

The project is in **early MVP development** with the following completion status:

- ✅ **Completed**: Core architecture, data layer, domain layer, presentation layer, Android UI (Compose)
- 🚧 **In Progress**: Testing, polish, iOS UI (SwiftUI)
- ❌ **Not Started**: Platform-specific features, advanced features, CI/CD

**Overall Progress**: ~40-50% of MVP complete based on TODO.md checklist

---

## 1. Desktop Target (Development Only)

### Status: ✅ Implemented (Stubs Only)

The desktop target was added for **Compose Hot Reload** development only. All implementations are **non-production stubs**.

### Stub Implementations

**Location**: `shared/src/desktopMain/` and `composeApp/src/desktopMain/`

| Component | File | Status | Notes |
|-----------|------|--------|-------|
| Database | `DatabaseDriverFactory.kt` | ⚠️ Stub | In-memory SQLite, data lost on restart |
| Secure Storage | `SecureStorageImpl.kt` | ⚠️ Stub | In-memory Map, **not secure** |
| Share Manager | `DesktopShareManager.kt` | ⚠️ Stub | Prints to console only |
| Network Monitor | `DesktopNetworkMonitor.kt` | ⚠️ Stub | Always reports connected |
| Platform Info | `Platform.desktop.kt` | ✅ Working | Returns OS name |
| DI Module | `di/DesktopModule.kt` | ⚠️ Stub | Uses stub implementations |
| Main Entry | `main.kt` | ✅ Working | Desktop window with Koin DI |

### ⚠️ **Critical Warning**

The desktop target is **ONLY for UI development with Hot Reload**. It must **NEVER** be used for:
- Production builds
- Testing business logic
- Security testing
- Performance testing
- Data persistence testing

### Recommended Actions

1. ✅ **Keep for development** - Very useful for rapid UI iteration
2. ⚠️ **Document limitations** - Already documented in COMPOSE_HOT_RELOAD.md
3. ✅ **Add warnings** - Already added to README and documentation
4. 💡 **Future**: Consider adding proper desktop implementation if desktop app is planned (see ROADMAP.md 2026 Q1)

---

## 2. Testing Issues

### Issue: Error State Timing Test Disabled

**Location**: `shared/src/commonTest/kotlin/com/po4yka/bitesizereader/presentation/viewmodel/SummaryListViewModelTest.kt:111`

**TODO Comment**:
```kotlin
// TODO: Fix this test - error state timing issue
// @Test
// fun `loadSummaries handles failure`()
```

**Status**: ⚠️ Known Issue

**Impact**: Test coverage gap for error handling in SummaryListViewModel

**Root Cause**: Timing issue with error state emission in Flow-based testing

**Recommended Fix**:
```kotlin
@Test
fun `loadSummaries handles failure`() = runTest {
    // Given
    coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns flow {
        throw Exception("Network error")
    }

    // When
    setupViewModel()

    // Then
    viewModel.state.test {
        // Skip initial state
        skipItems(1)

        // Verify error state
        val errorState = awaitItem()
        assertFalse(errorState.isLoading)
        assertNotNull(errorState.error)
        assertEquals("Network error", errorState.error)
    }
}
```

**Priority**: Medium - Test exists but commented out

---

## 3. Missing iOS Implementation

### Status: ❌ Not Started

According to TODO.md and ROADMAP.md, iOS UI implementation is planned but not yet started.

### Missing iOS Components

**Phase 7 (Week 7-8): iOS UI (SwiftUI)** - Not Started

| Component | Status | Location |
|-----------|--------|----------|
| SwiftUI Views | ❌ Missing | `iosApp/iosApp/Views/` |
| ViewModel Wrappers | ❌ Missing | `iosApp/iosApp/ViewModels/` |
| SKIE Integration | ❌ Missing | Flow → AsyncSequence conversion |
| Navigation | ❌ Missing | Decompose integration |
| Telegram Auth | ❌ Missing | WKWebView implementation |

### iOS Platform Features (Not Implemented)

| Feature | Status | Priority |
|---------|--------|----------|
| Share Extension | ❌ Missing | High |
| WidgetKit Widget | ❌ Missing | Medium |
| Universal Links | ❌ Missing | Medium |
| Background Tasks | ❌ Missing | Medium |
| Keychain Storage | ✅ Implemented | - |
| Database Driver | ✅ Implemented | - |

**Current State**:
- ✅ Shared business logic complete
- ✅ iOS platform implementations (database, storage) complete
- ❌ **No UI layer** - App cannot run on iOS yet

**Recommended Priority**: High - Complete iOS UI to achieve MVP parity

---

## 4. Placeholder Implementations (Android UI)

### Status: ✅ Implemented, But Uses Placeholders

The Android UI is mostly complete but contains some placeholder elements:

### Auth Screen

**Location**: `composeApp/src/androidMain/kotlin/com/po4yka/bitesizereader/ui/screens/AuthScreen.kt:40`

```kotlin
// App Logo/Icon (placeholder)
```

**Status**: ⚠️ Placeholder

**Impact**: Low - Visual only

**Recommendation**: Add actual app logo/branding before beta release

### Text Field Placeholders

**Location**: Various screens

```kotlin
// SubmitURLScreen.kt:95
placeholder = { Text("https://example.com/article") }

// SearchScreen.kt:73
placeholder = { Text("Search summaries...") }
```

**Status**: ✅ Acceptable - These are proper UI placeholders

---

## 5. Features Marked as "In Progress" or "Not Started"

### From TODO.md (Phase by Phase)

#### Phase 1: Project Setup ✅ **COMPLETE**

All tasks completed:
- ✅ Gradle configuration
- ✅ Project structure
- ✅ Dependencies added

#### Phase 2: Data Layer ✅ **COMPLETE**

All tasks completed:
- ✅ Domain models
- ✅ API DTOs
- ✅ SQLDelight schema
- ✅ Ktor client
- ✅ Mappers
- ✅ Platform implementations

#### Phase 3: Domain Layer ✅ **COMPLETE**

All tasks completed:
- ✅ Repository interfaces
- ✅ Use cases
- ✅ Repository implementations

#### Phase 4: Presentation Layer ✅ **COMPLETE**

All tasks completed:
- ✅ MVI state models
- ✅ ViewModels
- ✅ Decompose navigation

#### Phase 5: Dependency Injection ✅ **COMPLETE**

All tasks completed:
- ✅ Koin modules
- ✅ Platform-specific modules

#### Phase 6: Android UI ✅ **MOSTLY COMPLETE**

Status: ~95% complete

Completed:
- ✅ Material 3 theme
- ✅ All screens (Auth, SummaryList, SummaryDetail, SubmitURL, Search)
- ✅ Reusable components
- ✅ Decompose integration
- ✅ Navigation

Missing:
- ⚠️ App logo (placeholder)
- ❌ Share Intent implementation
- ❌ WorkManager background sync
- ❌ App Widget
- ❌ App Shortcuts

#### Phase 7: iOS UI ❌ **NOT STARTED**

Status: 0% complete

See section #3 above for details.

#### Phase 8: Authentication 🚧 **PARTIALLY COMPLETE**

Status: ~70% complete

Completed:
- ✅ JWT token management
- ✅ Auto-refresh tokens
- ✅ Secure storage (both platforms)
- ✅ Logout flow
- ✅ Login ViewModel and use cases

Missing:
- ❌ Android Custom Tab for Telegram Login Widget
- ❌ iOS WKWebView for Telegram Login Widget
- ⚠️ Callback URL handling not implemented
- ⚠️ Auth data parsing not implemented

**Current State**: Auth API integration complete, but platform-specific UI not implemented.

#### Phase 9: Testing 🚧 **IN PROGRESS**

Status: ~60% complete

Completed:
- ✅ Domain model tests
- ✅ Use case tests (partial)
- ✅ Mapper tests
- ✅ ViewModel tests (partial)
- ✅ Mock data factories

Missing or Incomplete:
- ⚠️ 1 test disabled (error state timing issue)
- ❌ Integration tests
- ❌ Android Compose UI tests (only 1 basic test exists)
- ❌ iOS XCTest UI tests
- ❌ Coverage reports
- ❌ Screenshot/snapshot tests

**Test Coverage**: Unknown (no coverage reports configured)

**Target**: 80% coverage for shared code

#### Phase 10: Polish ❌ **NOT STARTED**

Status: 0% complete

All items not started:
- ❌ Performance profiling
- ❌ Database query optimization
- ❌ Image caching implementation
- ❌ Bundle size reduction
- ❌ Memory leak detection
- ❌ Accessibility (TalkBack, VoiceOver)
- ❌ Animations
- ❌ Localization (only hardcoded English strings)
- ❌ Error message improvements

#### Phase 11: CI/CD & Release ❌ **NOT STARTED**

Status: 0% complete

All items not started:
- ❌ GitHub Actions workflow
- ❌ Lint automation
- ❌ Test automation
- ❌ Build automation
- ❌ CHANGELOG.md
- ❌ App store assets
- ❌ Privacy policy
- ❌ Terms of service

---

## 6. Platform-Specific Features

### Android Platform Features

| Feature | Status | Priority | Notes |
|---------|--------|----------|-------|
| Share Intent | ❌ Not Implemented | High | Receive URLs from other apps |
| WorkManager Sync | ❌ Not Implemented | High | Background data sync |
| App Widget | ❌ Not Implemented | Medium | Home screen widget |
| App Shortcuts | ❌ Not Implemented | Low | Quick actions |
| Tablet Layouts | ❌ Not Implemented | Low | Adaptive layouts for large screens |
| Material You | ✅ Implemented | - | Dynamic colors working |
| Dark Mode | ✅ Implemented | - | Theme switching working |

### iOS Platform Features

| Feature | Status | Priority | Notes |
|---------|--------|----------|-------|
| Share Extension | ❌ Not Implemented | High | Requires SwiftUI implementation |
| WidgetKit | ❌ Not Implemented | Medium | Requires SwiftUI implementation |
| Universal Links | ❌ Not Implemented | Medium | Deep linking |
| Siri Shortcuts | ❌ Not Implemented | Low | Voice commands |
| Background Tasks | ❌ Not Implemented | High | Background sync |
| iPad Layouts | ❌ Not Implemented | Low | Multi-column layout |

---

## 7. Advanced Features (Future Roadmap)

### From ROADMAP.md - Not Started

These features are planned for **post-MVP** (Q4 2025 and beyond):

#### Offline Reading Mode (Q4 2025, Week 31-33)
- ❌ Download full article content
- ❌ Offline-first with sync queue
- ❌ Storage management
- ❌ Download progress indicators

#### Reading Analytics (Q4 2025, Week 34-36)
- ❌ Reading statistics
- ❌ Reading streaks
- ❌ Topic interest analysis
- ❌ Reading goals
- ❌ Charts and visualizations

#### Social Features (Q4 2025, Week 37-38)
- ❌ Share preview (rich link)
- ❌ Export summaries (PDF, Markdown)
- ❌ Custom collections/folders
- ❌ Import OPML

#### 2026 Features
- ❌ Desktop apps (Compose Multiplatform)
- ❌ Web app
- ❌ Browser extension
- ❌ AI recommendations
- ❌ Voice narration
- ❌ Collaboration features

**Status**: All future features - documented but not prioritized for current MVP

---

## 8. Documentation Gaps

### Missing Documentation

| Document | Status | Priority |
|----------|--------|----------|
| DEVELOPMENT.md | ❌ Missing | Medium |
| CONTRIBUTING.md | ❌ Missing | Low |
| CHANGELOG.md | ❌ Missing | Medium |
| API.md (client API docs) | ❌ Missing | Low |
| TESTING.md | ❌ Missing | Medium |
| ARCHITECTURE.md | ❌ Missing | High |

### Existing Documentation (Complete)

- ✅ README.md - Comprehensive
- ✅ TODO.md - Detailed task list
- ✅ ROADMAP.md - Long-term vision
- ✅ CI_CD.md - CI/CD pipeline documentation
- ✅ LOGGING.md - kotlin-logging usage
- ✅ COMPOSE_HOT_RELOAD.md - Hot reload setup

---

## 9. Known Technical Debt

### From Code Comments and Grep Search

1. **Test Timing Issue** (SummaryListViewModelTest.kt)
   - Disabled error state test
   - Priority: Medium

2. **Placeholder Logo** (AuthScreen.kt)
   - UI uses placeholder for app logo
   - Priority: Low (visual only)

3. **Desktop Stubs** (all desktop implementations)
   - Non-production stub implementations
   - Priority: N/A (by design for development)

### From TODO.md "Known Issues / Blockers"

**Current Status**: None listed

**Actual Issues Found**:
- Test timing issue (see #2)
- iOS UI not started (blocking MVP parity)
- Platform features not implemented (blocking platform-specific features)

---

## 10. Recommendations by Priority

### 🔴 **High Priority** (Blocking MVP)

1. **Complete iOS UI Implementation** (Phase 7)
   - Required for MVP parity across platforms
   - Estimated: 2-3 weeks
   - Blockers: None

2. **Implement Telegram Auth UI** (Phase 8)
   - Android Custom Tab
   - iOS WKWebView
   - Callback handling
   - Estimated: 1 week
   - Blockers: None

3. **Fix Test Coverage Gaps** (Phase 9)
   - Fix error state timing test
   - Add integration tests
   - Add UI tests for both platforms
   - Estimated: 1 week
   - Blockers: iOS UI needed for iOS tests

4. **Implement Critical Platform Features**
   - Android: Share Intent, WorkManager
   - iOS: Share Extension, Background Tasks
   - Estimated: 1 week
   - Blockers: iOS UI needed

### 🟡 **Medium Priority** (Pre-Beta)

5. **Polish & Accessibility** (Phase 10)
   - Performance optimization
   - Accessibility support
   - Animations
   - Error handling improvements
   - Estimated: 2 weeks

6. **Documentation**
   - ARCHITECTURE.md
   - TESTING.md
   - DEVELOPMENT.md
   - Estimated: 3-4 days

7. **CI/CD Setup** (Phase 11)
   - GitHub Actions
   - Automated testing
   - Build automation
   - Estimated: 1 week

### 🟢 **Low Priority** (Post-Beta)

8. **Localization**
   - Extract strings
   - Add translations (en, ru)
   - Estimated: 1 week

9. **App Store Preparation**
   - CHANGELOG.md
   - App store assets
   - Privacy policy, ToS
   - Estimated: 1 week

10. **Advanced Features**
    - App widgets
    - Tablet layouts
    - App shortcuts
    - Estimated: 2-3 weeks

---

## 11. MVP Completion Checklist

Based on TODO.md and current status, here's what's needed to complete MVP:

### Must Have (Blocking Release)

- [ ] Complete iOS UI implementation (Phase 7)
- [ ] Implement Telegram auth UI (both platforms)
- [ ] Fix critical tests (error state test)
- [ ] Implement Share Intent (Android)
- [ ] Implement Share Extension (iOS)
- [ ] Implement background sync (both platforms)
- [ ] Basic accessibility support
- [ ] Error handling polish
- [ ] Performance optimization
- [ ] CI/CD pipeline
- [ ] Privacy policy and ToS

**Estimated Time to MVP**: 6-8 weeks

### Should Have (Pre-Public Beta)

- [ ] Comprehensive test coverage (>75%)
- [ ] Documentation complete
- [ ] Localization (en, ru)
- [ ] App widgets
- [ ] Animations and polish
- [ ] Beta testing infrastructure

**Estimated Time to Public Beta**: +2-3 weeks

### Could Have (Post-Beta)

- [ ] Tablet/iPad layouts
- [ ] Advanced animations
- [ ] Analytics integration
- [ ] A/B testing framework

---

## 12. Summary

### What's Working Well ✅

1. **Architecture** - Solid KMP foundation with clean architecture
2. **Data Layer** - Complete API integration, database, repositories
3. **Domain Layer** - All use cases implemented
4. **Presentation Layer** - ViewModels and navigation working
5. **Android UI** - Compose screens functional and polished
6. **Development Tools** - Hot reload, logging, documentation

### Major Gaps ❌

1. **iOS UI** - Completely missing (0% complete)
2. **Telegram Auth UI** - Platform-specific UI not implemented
3. **Platform Features** - Share, widgets, background sync missing
4. **Testing** - Coverage gaps, disabled tests, no UI tests
5. **CI/CD** - No automation
6. **Polish** - No accessibility, performance work, localization

### Critical Path to MVP

1. Complete iOS UI (2-3 weeks) ← **HIGHEST PRIORITY**
2. Implement auth UI (1 week)
3. Add platform features (1 week)
4. Fix tests and coverage (1 week)
5. Polish and performance (2 weeks)
6. CI/CD and release prep (1 week)

**Total**: ~6-8 weeks to MVP

---

## Change Log

| Date | Changes | Author |
|------|---------|--------|
| 2025-11-17 | Initial audit - comprehensive review of unimplemented features | Claude |

---

## Next Steps

1. **Review this document** with the team
2. **Prioritize** features based on MVP requirements
3. **Update TODO.md** with current status
4. **Create GitHub issues** for high-priority items
5. **Assign ownership** for iOS UI implementation
6. **Set milestone dates** for MVP completion

---

**Maintained by**: Development Team
**Review Frequency**: Weekly during active development
