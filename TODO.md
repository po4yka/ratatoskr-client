# Implementation TODO

Detailed implementation checklist for Bite-Size Reader Mobile Client.

## Legend

- [ ] Not started
- [WIP] In progress
- [X] Completed
- [R] Needs review/testing
- [!] Blocked/needs discussion

---

## Phase 1: Project Setup & Foundation (Week 1-2)

### Gradle & Build Configuration

- [X] Update `gradle/libs.versions.toml` with all dependencies
  - [X] Add Ktor client (3.0.2+)
  - [X] Add SQLDelight (2.0.2+)
  - [X] Add Decompose (3.2.0+)
  - [X] Add Store (5.1.0+)
  - [X] Add Koin (3.5.6+)
  - [X] Add kotlinx.serialization
  - [X] Add kotlinx-datetime
  - [X] Add Kermit logging
  - [X] Add SKIE (iOS interop)
- [X] Configure `shared/build.gradle.kts`
  - [X] Add kotlinx.serialization plugin
  - [X] Add SQLDelight plugin
  - [X] Configure commonMain dependencies
  - [X] Configure androidMain dependencies (OkHttp, Android SQLDelight driver)
  - [X] Configure iosMain dependencies (Darwin engine, Native SQLDelight driver)
- [X] Configure `composeApp/build.gradle.kts`
  - [X] Add Compose dependencies
  - [X] Add Koin Android extensions
  - [X] Add Coil image loading
  - [X] Add Material 3 icons
- [X] Create `.gitignore` entries
  - [X] Add `local.properties`
  - [X] Add IDE files
  - [X] Add build outputs
- [X] Create `local.properties.example` template
- [X] iOS Xcode project configuration
  - [X] Add Podfile for CocoaPods dependencies
  - [X] Configure framework linking
  - [X] Add SKIE configuration

### Project Structure

- [X] Create `shared/src/commonMain/kotlin/com/po4yka/bitesizereader/` package structure:
  - [X] `data/local/` - SQLDelight database
  - [X] `data/remote/` - Ktor API clients
  - [X] `data/remote/dto/` - API response/request models
  - [X] `data/repository/` - Repository implementations
  - [X] `data/mappers/` - DTO ↔ Domain mappers
  - [X] `domain/model/` - Domain entities
  - [X] `domain/repository/` - Repository interfaces
  - [X] `domain/usecase/` - Use cases
  - [X] `presentation/navigation/` - Decompose navigation components
  - [X] `presentation/viewmodel/` - Shared ViewModels
  - [X] `presentation/state/` - UI state models
  - [X] `di/` - Koin modules
  - [X] `util/` - Extensions and helpers

---

## Phase 2: Data Layer (Week 2-3)

### Domain Models

- [X] Create domain models in `domain/model/`:
  - [X] `Summary.kt` - Summary domain entity
  - [X] `Request.kt` - Request domain entity
  - [X] `User.kt` - User domain entity
  - [X] `SearchQuery.kt` - Search query model
  - [X] `SyncState.kt` - Sync state model
  - [X] `AuthTokens.kt` - JWT tokens model
  - [X] `RequestStatus.kt` - Request processing status

### API DTOs (kotlinx.serialization)

- [X] Create DTOs in `data/remote/dto/`:
  - [X] `SummaryDto.kt` - API summary response
  - [X] `SummaryListResponseDto.kt` - Paginated summary list
  - [X] `RequestDto.kt` - Request response
  - [X] `RequestStatusDto.kt` - Request status polling response
  - [X] `SearchResponseDto.kt` - Search results
  - [X] `AuthRequestDto.kt` - Telegram login request
  - [X] `AuthResponseDto.kt` - JWT tokens response
  - [X] `SyncDeltaResponseDto.kt` - Delta sync response
  - [X] `ApiResponseDto.kt` - Generic API wrapper
  - [X] `ErrorResponseDto.kt` - Error response

### SQLDelight Database Schema

- [X] Create `shared/src/commonMain/sqldelight/com/po4yka/bitesizereader/Database.sq`:
  - [X] `Summary` table schema
  - [X] `Request` table schema
  - [X] `SyncMetadata` table schema
  - [X] Create indexes on `is_read`, `created_at`
  - [X] Create FTS5 virtual table for search
  - [X] Define queries: selectAll, selectById, insert, update, delete
  - [X] Define search queries
  - [X] Define pagination queries with limit/offset

### Ktor API Client

- [X] Create `data/remote/ApiClient.kt`:
  - [X] Configure Ktor HttpClient with JSON serialization
  - [X] Add Auth plugin with JWT bearer tokens
  - [X] Add Logging plugin (conditionally for debug builds)
  - [X] Add DefaultRequest plugin for base URL
  - [X] Platform-specific engines (OkHttp for Android, Darwin for iOS)
  - [X] Implement token refresh logic
  - [X] Implement request/response interceptors
- [X] Create API service interfaces:
  - [X] `AuthApi.kt` - Authentication endpoints
  - [X] `SummariesApi.kt` - Summary CRUD operations
  - [X] `RequestsApi.kt` - Request submission and status
  - [X] `SearchApi.kt` - Search endpoints
  - [X] `SyncApi.kt` - Sync endpoints

### Data Mappers

- [X] Create mappers in `data/mappers/`:
  - [X] `SummaryMapper.kt` - SummaryDto ↔ Summary
  - [X] `RequestMapper.kt` - RequestDto ↔ Request
  - [X] `SearchMapper.kt` - SearchResponseDto ↔ List<Summary>
  - [X] Extension functions for batch mapping

### Store Repositories

- [X] Implement repositories in `data/repository/`:
  - [X] `SummaryRepositoryImpl.kt` - Store-based summary repository
    - [X] Configure Store with Fetcher (API) and SourceOfTruth (DB)
    - [X] Implement cache invalidation
    - [X] Implement pagination support
  - [X] `RequestRepositoryImpl.kt` - Request repository
  - [X] `SearchRepositoryImpl.kt` - Search repository with local/remote merge
  - [X] `SyncRepositoryImpl.kt` - Sync manager
    - [X] Full sync with chunked downloads
    - [X] Delta sync
    - [X] Upload local changes
  - [X] `AuthRepositoryImpl.kt` - Authentication repository
    - [X] Login with Telegram
    - [X] Token storage (platform-specific)
    - [X] Token refresh
    - [X] Logout

### Platform-Specific Implementations

- [X] Android (`androidMain/`):
  - [X] `DatabaseDriverFactory.kt` - Android SQLDelight driver
  - [X] `SecureStorage.kt` - EncryptedSharedPreferences for tokens
  - [X] `PlatformContext.kt` - Android Context wrapper
- [X] iOS (`iosMain/`):
  - [X] `DatabaseDriverFactory.kt` - Native SQLDelight driver
  - [X] `SecureStorage.kt` - Keychain wrapper for tokens
  - [X] `PlatformContext.kt` - iOS platform utilities

---

## Phase 3: Domain Layer (Week 3-4)

### Repository Interfaces

- [X] Define interfaces in `domain/repository/`:
  - [X] `SummaryRepository.kt`
  - [X] `RequestRepository.kt`
  - [X] `SearchRepository.kt`
  - [X] `SyncRepository.kt`
  - [X] `AuthRepository.kt`

### Use Cases

- [X] Implement use cases in `domain/usecase/`:
  - [X] **Summaries**:
    - [X] `GetSummariesUseCase.kt` - Fetch summaries with filters
    - [X] `GetSummaryByIdUseCase.kt` - Fetch single summary
    - [X] `MarkSummaryAsReadUseCase.kt` - Update read status
    - [X] `DeleteSummaryUseCase.kt` - Soft delete summary
  - [X] **Requests**:
    - [X] `SubmitURLUseCase.kt` - Submit URL and poll status
    - [X] `GetRequestStatusUseCase.kt` - Poll request status
    - [X] `RetryRequestUseCase.kt` - Retry failed request
  - [X] **Search**:
    - [X] `SearchSummariesUseCase.kt` - Search with local/remote merge
    - [X] `GetTrendingTopicsUseCase.kt` - Get trending topic tags
  - [X] **Sync**:
    - [X] `SyncDataUseCase.kt` - Full and delta sync
    - [X] `UploadChangesUseCase.kt` - Upload local changes
  - [X] **Auth**:
    - [X] `LoginWithTelegramUseCase.kt` - Telegram login flow
    - [X] `RefreshTokenUseCase.kt` - Refresh access token
    - [X] `LogoutUseCase.kt` - Clear tokens and local data
    - [X] `GetCurrentUserUseCase.kt` - Get current user

---

## Phase 4: Presentation Layer (Week 4-5)

### MVI State Models

- [X] Create state models in `presentation/state/`:
  - [X] `SummaryListState.kt` - Summary list UI state
  - [X] `SummaryDetailState.kt` - Summary detail UI state
  - [X] `SubmitURLState.kt` - URL submission UI state
  - [X] `SearchState.kt` - Search UI state
  - [X] `AuthState.kt` - Authentication UI state

### ViewModels

- [X] Implement ViewModels in `presentation/viewmodel/`:
  - [X] `SummaryListViewModel.kt`
    - [X] State: summaries, loading, error, filters, pagination
    - [X] Events: LoadSummaries, MarkAsRead, Filter, LoadMore
  - [X] `SummaryDetailViewModel.kt`
    - [X] State: summary, loading, error
    - [X] Events: LoadSummary, MarkAsRead, Delete, Share
  - [X] `SubmitURLViewModel.kt`
    - [X] State: url, validationError, requestStatus, progress
    - [X] Events: OnURLChange, Submit, CancelRequest
  - [X] `SearchViewModel.kt`
    - [X] State: query, results, loading, error
    - [X] Events: OnQueryChange, Search, ClearResults
  - [X] `AuthViewModel.kt`
    - [X] State: isAuthenticated, loading, error
    - [X] Events: LoginWithTelegram, Logout

### Decompose Navigation

- [X] Create navigation components in `presentation/navigation/`:
  - [X] `RootComponent.kt` - Root navigation component
    - [X] Define Child sealed class (Splash, Auth, Main)
    - [X] Implement navigation logic
  - [X] `MainComponent.kt` - Main tab navigation
    - [X] Define tabs: SummaryList, Search, Profile
  - [X] `SummaryListComponent.kt` - Summary list component
  - [X] `SummaryDetailComponent.kt` - Summary detail component
  - [X] `SubmitURLComponent.kt` - Submit URL component
  - [X] `SearchComponent.kt` - Search component
  - [X] `AuthComponent.kt` - Authentication component

---

## Phase 5: Dependency Injection (Week 5)

### Koin Modules

- [X] Create Koin modules in `di/`:
  - [X] `NetworkModule.kt`
    - [X] Provide HttpClient
    - [X] Provide API services
  - [X] `DatabaseModule.kt`
    - [X] Provide SQLDelight database instance
    - [X] Provide DAO queries
  - [X] `RepositoryModule.kt`
    - [X] Provide Store instances
    - [X] Provide repository implementations
  - [X] `UseCaseModule.kt`
    - [X] Provide all use cases
  - [X] `ViewModelModule.kt`
    - [X] Provide ViewModels
  - [X] `PlatformModule.kt`
    - [X] Provide platform-specific dependencies
- [X] Platform-specific modules:
  - [X] `androidMain/di/AndroidModule.kt`
    - [X] Provide Android Context
    - [X] Provide EncryptedSharedPreferences
  - [X] `iosMain/di/IosModule.kt`
    - [X] Provide Keychain wrapper

---

## Phase 6: Android UI (Jetpack Compose) (Week 6-7)

### Theme & Design System

- [X] Create Material 3 theme in `composeApp/src/androidMain/kotlin/ui/theme/`:
  - [X] `Color.kt` - Color palette
  - [X] `Type.kt` - Typography
  - [X] `Theme.kt` - Material 3 theme composition
  - [X] Support dynamic colors (Material You)
  - [X] Dark mode support

### Composable Screens

- [X] Create screens in `composeApp/src/androidMain/kotlin/ui/screens/`:
  - [X] `SummaryListScreen.kt`
    - [X] LazyColumn with pagination
    - [X] Pull-to-refresh
    - [X] Swipe-to-mark-read
    - [X] Filter chips
    - [X] FAB for submit URL
  - [X] `SummaryDetailScreen.kt`
    - [X] Scrollable content
    - [X] Collapsing toolbar
    - [X] Share button
    - [X] Mark as read/unread toggle
  - [X] `SubmitURLScreen.kt`
    - [X] URL input field with validation
    - [X] Submit button
    - [X] Progress indicator with stages
    - [X] Cancel button
  - [X] `SearchScreen.kt`
    - [X] Search bar
    - [X] Results list
    - [X] Topic tag chips
  - [X] `AuthScreen.kt`
    - [X] Login with Telegram button
    - [X] Custom Tab for Telegram widget

### Reusable Components

- [X] Create components in `composeApp/src/androidMain/kotlin/ui/components/`:
  - [X] `SummaryCard.kt` - Summary list item
  - [X] `TagChip.kt` - Topic tag chip
  - [X] `ProgressIndicatorWithStages.kt` - Request progress
  - [X] `ErrorView.kt` - Error state view
  - [X] `EmptyStateView.kt` - Empty state view

### Navigation (Compose)

- [X] Implement Decompose integration:
  - [X] `MainActivity.kt` - Initialize RootComponent
  - [X] Create Composable wrappers for Decompose components
  - [X] Handle back navigation

### Platform Features

- [X] Share Intent - Receive URLs from other apps
- [X] WorkManager - Background sync
- [X] App Widget - Home screen widget with recent summaries
- [X] App Shortcuts - Quick actions (Submit URL, Search)

---

## Phase 7: iOS UI (SwiftUI) (Week 7-8)

### SwiftUI Views

- [X] Create views in `iosApp/iosApp/Views/`:
  - [X] `SummaryListView.swift`
    - [X] List with pagination
    - [X] Pull-to-refresh
    - [X] Swipe actions
    - [X] Search bar
  - [X] `SummaryDetailView.swift`
    - [X] ScrollView content
    - [X] Navigation bar buttons
    - [X] Share sheet
  - [X] `SubmitURLView.swift`
    - [X] TextField with validation
    - [X] Progress view with stages
  - [X] `SearchView.swift`
    - [X] Search bar
    - [X] Results list
  - [X] `AuthView.swift`
    - [X] Login with Telegram button
    - [X] WKWebView for Telegram widget

### Swift ViewModel Wrappers (SKIE)

- [X] Create Swift wrappers in `iosApp/iosApp/ViewModels/`:
  - [X] `SummaryListViewModelWrapper.swift`
    - [X] Convert Flow to AsyncSequence
    - [X] ObservableObject conformance
  - [X] `SummaryDetailViewModelWrapper.swift`
  - [X] `SubmitURLViewModelWrapper.swift`
  - [X] `SearchViewModelWrapper.swift`
  - [X] `AuthViewModelWrapper.swift`

### Platform Features

- [X] Share Extension - Submit URLs from Safari
- [X] WidgetKit - Home screen widget
- [X] Universal Links - Deep linking
- [X] Background Tasks - Background sync

---

## Phase 8: Authentication (Week 8)

### Telegram Login Integration

- [X] Android:
  - [X] Custom Tab for Telegram Login Widget
  - [X] Handle callback URL
  - [X] Parse auth data
  - [X] Store tokens securely
- [X] iOS:
  - [X] WKWebView for Telegram Login Widget
  - [X] Handle callback URL
  - [X] Parse auth data
  - [X] Store tokens in Keychain

### Token Management

- [X] Implement auto-refresh before token expiry
- [X] Handle 401 unauthorized responses
- [X] Logout and clear tokens
- [X] Persist login state across app restarts

---

## Phase 9: Testing (Week 9)

### Shared Tests (commonTest)

- [X] Unit tests:
  - [X] Domain models
  - [X] Use cases
  - [X] Mappers
  - [X] Repository logic (mocked)
  - [X] ViewModels (mocked dependencies)
- [X] Test utilities:
  - [X] Mock data factories
  - [X] Test coroutine dispatchers

### Android Tests

- [X] Compose UI tests:
  - [X] Summary list interactions
  - [X] Summary detail navigation
  - [X] URL submission flow
  - [X] Search functionality
- [X] Screenshot tests (Paparazzi)

### iOS Tests

- [X] XCTest UI tests:
  - [X] Navigation flows
  - [X] UI interactions
- [X] Snapshot tests

---

## Phase 10: Polish & Optimization (Week 10)

### Performance

- [X] Profile app launch time
- [X] Optimize database queries
- [X] Implement image caching (Coil/Kingfisher)
- [X] Reduce bundle size
- [X] Memory leak detection

### Accessibility

- [ ] Android:
  - [ ] TalkBack support
  - [ ] Content descriptions
  - [ ] Proper heading hierarchy
- [ ] iOS:
  - [ ] VoiceOver support
  - [ ] Accessibility labels
  - [ ] Dynamic Type support

### Animations

- [ ] Android:
  - [ ] Shared element transitions
  - [ ] List item animations
- [ ] iOS:
  - [ ] View transitions
  - [ ] Animation curves

### Localization

- [ ] Extract strings to resources
- [ ] Add Russian translations (matching backend)
- [ ] Add English translations

### Error Handling

- [X] Network error messages
- [X] Offline mode indicators
- [X] Retry mechanisms
- [X] Validation error messages

---

## Phase 11: CI/CD & Release (Week 11)

### CI/CD Pipeline

- [ ] GitHub Actions workflow:
  - [ ] Lint Kotlin code
  - [ ] Run shared tests
  - [ ] Build Android APK
  - [ ] Build iOS framework
  - [ ] Run UI tests
  - [ ] Generate coverage reports

### Release Preparation

- [ ] Create CHANGELOG.md
- [ ] Prepare app store assets:
  - [ ] Screenshots (Android)
  - [ ] Screenshots (iOS)
  - [ ] App icon
  - [ ] Feature graphic
  - [ ] Descriptions
- [ ] Beta testing:
  - [ ] Google Play Internal Testing
  - [ ] TestFlight
- [ ] Privacy policy
- [ ] Terms of service

---

## Future Enhancements (Post-MVP)

### Advanced Features

- [ ] Offline reading mode with downloaded content
- [ ] Reading statistics and analytics
- [ ] Custom topic collections/folders
- [ ] Export summaries (PDF, Markdown)
- [ ] Reading goals and streaks
- [ ] Social sharing with summary preview
- [ ] Voice narration of summaries (TTS)
- [ ] Browser extension for quick saves

### Platform-Specific

- [ ] Android:
  - [ ] Tablet/foldable layouts
  - [ ] Wear OS companion app
  - [ ] Android Auto integration
- [ ] iOS:
  - [ ] iPad multi-column layout
  - [ ] Apple Watch companion app
  - [ ] Siri Shortcuts
  - [ ] Live Activities for request processing

---

**Last Updated**: 2025-11-16
**Current Phase**: Phase 1 - Project Setup
**Overall Progress**: 0/280 tasks (0%)
