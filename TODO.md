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

- [ ] Update `gradle/libs.versions.toml` with all dependencies
  - [ ] Add Ktor client (3.0.2+)
  - [ ] Add SQLDelight (2.0.2+)
  - [ ] Add Decompose (3.2.0+)
  - [ ] Add Store (5.1.0+)
  - [ ] Add Koin (3.5.6+)
  - [ ] Add kotlinx.serialization
  - [ ] Add kotlinx-datetime
  - [ ] Add Kermit logging
  - [ ] Add SKIE (iOS interop)
- [ ] Configure `shared/build.gradle.kts`
  - [ ] Add kotlinx.serialization plugin
  - [ ] Add SQLDelight plugin
  - [ ] Configure commonMain dependencies
  - [ ] Configure androidMain dependencies (OkHttp, Android SQLDelight driver)
  - [ ] Configure iosMain dependencies (Darwin engine, Native SQLDelight driver)
- [ ] Configure `composeApp/build.gradle.kts`
  - [ ] Add Compose dependencies
  - [ ] Add Koin Android extensions
  - [ ] Add Coil image loading
  - [ ] Add Material 3 icons
- [ ] Create `.gitignore` entries
  - [ ] Add `local.properties`
  - [ ] Add IDE files
  - [ ] Add build outputs
- [ ] Create `local.properties.example` template
- [ ] iOS Xcode project configuration
  - [ ] Add Podfile for CocoaPods dependencies
  - [ ] Configure framework linking
  - [ ] Add SKIE configuration

### Project Structure

- [ ] Create `shared/src/commonMain/kotlin/com/po4yka/bitesizereader/` package structure:
  - [ ] `data/local/` - SQLDelight database
  - [ ] `data/remote/` - Ktor API clients
  - [ ] `data/remote/dto/` - API response/request models
  - [ ] `data/repository/` - Repository implementations
  - [ ] `data/mappers/` - DTO ↔ Domain mappers
  - [ ] `domain/model/` - Domain entities
  - [ ] `domain/repository/` - Repository interfaces
  - [ ] `domain/usecase/` - Use cases
  - [ ] `presentation/navigation/` - Decompose navigation components
  - [ ] `presentation/viewmodel/` - Shared ViewModels
  - [ ] `presentation/state/` - UI state models
  - [ ] `di/` - Koin modules
  - [ ] `util/` - Extensions and helpers

---

## Phase 2: Data Layer (Week 2-3)

### Domain Models

- [ ] Create domain models in `domain/model/`:
  - [ ] `Summary.kt` - Summary domain entity
  - [ ] `Request.kt` - Request domain entity
  - [ ] `User.kt` - User domain entity
  - [ ] `SearchQuery.kt` - Search query model
  - [ ] `SyncState.kt` - Sync state model
  - [ ] `AuthTokens.kt` - JWT tokens model
  - [ ] `RequestStatus.kt` - Request processing status

### API DTOs (kotlinx.serialization)

- [ ] Create DTOs in `data/remote/dto/`:
  - [ ] `SummaryDto.kt` - API summary response
  - [ ] `SummaryListResponseDto.kt` - Paginated summary list
  - [ ] `RequestDto.kt` - Request response
  - [ ] `RequestStatusDto.kt` - Request status polling response
  - [ ] `SearchResponseDto.kt` - Search results
  - [ ] `AuthRequestDto.kt` - Telegram login request
  - [ ] `AuthResponseDto.kt` - JWT tokens response
  - [ ] `SyncDeltaResponseDto.kt` - Delta sync response
  - [ ] `ApiResponseDto.kt` - Generic API wrapper
  - [ ] `ErrorResponseDto.kt` - Error response

### SQLDelight Database Schema

- [ ] Create `shared/src/commonMain/sqldelight/com/po4yka/bitesizereader/Database.sq`:
  - [ ] `Summary` table schema
  - [ ] `Request` table schema
  - [ ] `SyncMetadata` table schema
  - [ ] Create indexes on `is_read`, `created_at`
  - [ ] Create FTS5 virtual table for search
  - [ ] Define queries: selectAll, selectById, insert, update, delete
  - [ ] Define search queries
  - [ ] Define pagination queries with limit/offset

### Ktor API Client

- [ ] Create `data/remote/ApiClient.kt`:
  - [ ] Configure Ktor HttpClient with JSON serialization
  - [ ] Add Auth plugin with JWT bearer tokens
  - [ ] Add Logging plugin (conditionally for debug builds)
  - [ ] Add DefaultRequest plugin for base URL
  - [ ] Platform-specific engines (OkHttp for Android, Darwin for iOS)
  - [ ] Implement token refresh logic
  - [ ] Implement request/response interceptors
- [ ] Create API service interfaces:
  - [ ] `AuthApi.kt` - Authentication endpoints
  - [ ] `SummariesApi.kt` - Summary CRUD operations
  - [ ] `RequestsApi.kt` - Request submission and status
  - [ ] `SearchApi.kt` - Search endpoints
  - [ ] `SyncApi.kt` - Sync endpoints

### Data Mappers

- [ ] Create mappers in `data/mappers/`:
  - [ ] `SummaryMapper.kt` - SummaryDto ↔ Summary
  - [ ] `RequestMapper.kt` - RequestDto ↔ Request
  - [ ] `SearchMapper.kt` - SearchResponseDto ↔ List<Summary>
  - [ ] Extension functions for batch mapping

### Store Repositories

- [ ] Implement repositories in `data/repository/`:
  - [ ] `SummaryRepositoryImpl.kt` - Store-based summary repository
    - [ ] Configure Store with Fetcher (API) and SourceOfTruth (DB)
    - [ ] Implement cache invalidation
    - [ ] Implement pagination support
  - [ ] `RequestRepositoryImpl.kt` - Request repository
  - [ ] `SearchRepositoryImpl.kt` - Search repository with local/remote merge
  - [ ] `SyncRepositoryImpl.kt` - Sync manager
    - [ ] Full sync with chunked downloads
    - [ ] Delta sync
    - [ ] Upload local changes
  - [ ] `AuthRepositoryImpl.kt` - Authentication repository
    - [ ] Login with Telegram
    - [ ] Token storage (platform-specific)
    - [ ] Token refresh
    - [ ] Logout

### Platform-Specific Implementations

- [ ] Android (`androidMain/`):
  - [ ] `DatabaseDriverFactory.kt` - Android SQLDelight driver
  - [ ] `SecureStorage.kt` - EncryptedSharedPreferences for tokens
  - [ ] `PlatformContext.kt` - Android Context wrapper
- [ ] iOS (`iosMain/`):
  - [ ] `DatabaseDriverFactory.kt` - Native SQLDelight driver
  - [ ] `SecureStorage.kt` - Keychain wrapper for tokens
  - [ ] `PlatformContext.kt` - iOS platform utilities

---

## Phase 3: Domain Layer (Week 3-4)

### Repository Interfaces

- [ ] Define interfaces in `domain/repository/`:
  - [ ] `SummaryRepository.kt`
  - [ ] `RequestRepository.kt`
  - [ ] `SearchRepository.kt`
  - [ ] `SyncRepository.kt`
  - [ ] `AuthRepository.kt`

### Use Cases

- [ ] Implement use cases in `domain/usecase/`:
  - [ ] **Summaries**:
    - [ ] `GetSummariesUseCase.kt` - Fetch summaries with filters
    - [ ] `GetSummaryByIdUseCase.kt` - Fetch single summary
    - [ ] `MarkSummaryAsReadUseCase.kt` - Update read status
    - [ ] `DeleteSummaryUseCase.kt` - Soft delete summary
  - [ ] **Requests**:
    - [ ] `SubmitURLUseCase.kt` - Submit URL and poll status
    - [ ] `GetRequestStatusUseCase.kt` - Poll request status
    - [ ] `RetryRequestUseCase.kt` - Retry failed request
  - [ ] **Search**:
    - [ ] `SearchSummariesUseCase.kt` - Search with local/remote merge
    - [ ] `GetTrendingTopicsUseCase.kt` - Get trending topic tags
  - [ ] **Sync**:
    - [ ] `SyncDataUseCase.kt` - Full and delta sync
    - [ ] `UploadChangesUseCase.kt` - Upload local changes
  - [ ] **Auth**:
    - [ ] `LoginWithTelegramUseCase.kt` - Telegram login flow
    - [ ] `RefreshTokenUseCase.kt` - Refresh access token
    - [ ] `LogoutUseCase.kt` - Clear tokens and local data
    - [ ] `GetCurrentUserUseCase.kt` - Get current user

---

## Phase 4: Presentation Layer (Week 4-5)

### MVI State Models

- [ ] Create state models in `presentation/state/`:
  - [ ] `SummaryListState.kt` - Summary list UI state
  - [ ] `SummaryDetailState.kt` - Summary detail UI state
  - [ ] `SubmitURLState.kt` - URL submission UI state
  - [ ] `SearchState.kt` - Search UI state
  - [ ] `AuthState.kt` - Authentication UI state

### ViewModels

- [ ] Implement ViewModels in `presentation/viewmodel/`:
  - [ ] `SummaryListViewModel.kt`
    - [ ] State: summaries, loading, error, filters, pagination
    - [ ] Events: LoadSummaries, MarkAsRead, Filter, LoadMore
  - [ ] `SummaryDetailViewModel.kt`
    - [ ] State: summary, loading, error
    - [ ] Events: LoadSummary, MarkAsRead, Delete, Share
  - [ ] `SubmitURLViewModel.kt`
    - [ ] State: url, validationError, requestStatus, progress
    - [ ] Events: OnURLChange, Submit, CancelRequest
  - [ ] `SearchViewModel.kt`
    - [ ] State: query, results, loading, error
    - [ ] Events: OnQueryChange, Search, ClearResults
  - [ ] `AuthViewModel.kt`
    - [ ] State: isAuthenticated, loading, error
    - [ ] Events: LoginWithTelegram, Logout

### Decompose Navigation

- [ ] Create navigation components in `presentation/navigation/`:
  - [ ] `RootComponent.kt` - Root navigation component
    - [ ] Define Child sealed class (Splash, Auth, Main)
    - [ ] Implement navigation logic
  - [ ] `MainComponent.kt` - Main tab navigation
    - [ ] Define tabs: SummaryList, Search, Profile
  - [ ] `SummaryListComponent.kt` - Summary list component
  - [ ] `SummaryDetailComponent.kt` - Summary detail component
  - [ ] `SubmitURLComponent.kt` - Submit URL component
  - [ ] `SearchComponent.kt` - Search component
  - [ ] `AuthComponent.kt` - Authentication component

---

## Phase 5: Dependency Injection (Week 5)

### Koin Modules

- [ ] Create Koin modules in `di/`:
  - [ ] `NetworkModule.kt`
    - [ ] Provide HttpClient
    - [ ] Provide API services
  - [ ] `DatabaseModule.kt`
    - [ ] Provide SQLDelight database instance
    - [ ] Provide DAO queries
  - [ ] `RepositoryModule.kt`
    - [ ] Provide Store instances
    - [ ] Provide repository implementations
  - [ ] `UseCaseModule.kt`
    - [ ] Provide all use cases
  - [ ] `ViewModelModule.kt`
    - [ ] Provide ViewModels
  - [ ] `PlatformModule.kt`
    - [ ] Provide platform-specific dependencies
- [ ] Platform-specific modules:
  - [ ] `androidMain/di/AndroidModule.kt`
    - [ ] Provide Android Context
    - [ ] Provide EncryptedSharedPreferences
  - [ ] `iosMain/di/IosModule.kt`
    - [ ] Provide Keychain wrapper

---

## Phase 6: Android UI (Jetpack Compose) (Week 6-7)

### Theme & Design System

- [ ] Create Material 3 theme in `composeApp/src/androidMain/kotlin/ui/theme/`:
  - [ ] `Color.kt` - Color palette
  - [ ] `Type.kt` - Typography
  - [ ] `Theme.kt` - Material 3 theme composition
  - [ ] Support dynamic colors (Material You)
  - [ ] Dark mode support

### Composable Screens

- [ ] Create screens in `composeApp/src/androidMain/kotlin/ui/screens/`:
  - [ ] `SummaryListScreen.kt`
    - [ ] LazyColumn with pagination
    - [ ] Pull-to-refresh
    - [ ] Swipe-to-mark-read
    - [ ] Filter chips
    - [ ] FAB for submit URL
  - [ ] `SummaryDetailScreen.kt`
    - [ ] Scrollable content
    - [ ] Collapsing toolbar
    - [ ] Share button
    - [ ] Mark as read/unread toggle
  - [ ] `SubmitURLScreen.kt`
    - [ ] URL input field with validation
    - [ ] Submit button
    - [ ] Progress indicator with stages
    - [ ] Cancel button
  - [ ] `SearchScreen.kt`
    - [ ] Search bar
    - [ ] Results list
    - [ ] Topic tag chips
  - [ ] `AuthScreen.kt`
    - [ ] Login with Telegram button
    - [ ] Custom Tab for Telegram widget

### Reusable Components

- [ ] Create components in `composeApp/src/androidMain/kotlin/ui/components/`:
  - [ ] `SummaryCard.kt` - Summary list item
  - [ ] `TagChip.kt` - Topic tag chip
  - [ ] `ProgressIndicatorWithStages.kt` - Request progress
  - [ ] `ErrorView.kt` - Error state view
  - [ ] `EmptyStateView.kt` - Empty state view

### Navigation (Compose)

- [ ] Implement Decompose integration:
  - [ ] `MainActivity.kt` - Initialize RootComponent
  - [ ] Create Composable wrappers for Decompose components
  - [ ] Handle back navigation

### Platform Features

- [ ] Share Intent - Receive URLs from other apps
- [ ] WorkManager - Background sync
- [ ] App Widget - Home screen widget with recent summaries
- [ ] App Shortcuts - Quick actions (Submit URL, Search)

---

## Phase 7: iOS UI (SwiftUI) (Week 7-8)

### SwiftUI Views

- [ ] Create views in `iosApp/iosApp/Views/`:
  - [ ] `SummaryListView.swift`
    - [ ] List with pagination
    - [ ] Pull-to-refresh
    - [ ] Swipe actions
    - [ ] Search bar
  - [ ] `SummaryDetailView.swift`
    - [ ] ScrollView content
    - [ ] Navigation bar buttons
    - [ ] Share sheet
  - [ ] `SubmitURLView.swift`
    - [ ] TextField with validation
    - [ ] Progress view with stages
  - [ ] `SearchView.swift`
    - [ ] Search bar
    - [ ] Results list
  - [ ] `AuthView.swift`
    - [ ] Login with Telegram button
    - [ ] WKWebView for Telegram widget

### Swift ViewModel Wrappers (SKIE)

- [ ] Create Swift wrappers in `iosApp/iosApp/ViewModels/`:
  - [ ] `SummaryListViewModelWrapper.swift`
    - [ ] Convert Flow to AsyncSequence
    - [ ] ObservableObject conformance
  - [ ] `SummaryDetailViewModelWrapper.swift`
  - [ ] `SubmitURLViewModelWrapper.swift`
  - [ ] `SearchViewModelWrapper.swift`
  - [ ] `AuthViewModelWrapper.swift`

### Platform Features

- [ ] Share Extension - Submit URLs from Safari
- [ ] WidgetKit - Home screen widget
- [ ] Universal Links - Deep linking
- [ ] Background Tasks - Background sync

---

## Phase 8: Authentication (Week 8)

### Telegram Login Integration

- [ ] Android:
  - [ ] Custom Tab for Telegram Login Widget
  - [ ] Handle callback URL
  - [ ] Parse auth data
  - [ ] Store tokens securely
- [ ] iOS:
  - [ ] WKWebView for Telegram Login Widget
  - [ ] Handle callback URL
  - [ ] Parse auth data
  - [ ] Store tokens in Keychain

### Token Management

- [ ] Implement auto-refresh before token expiry
- [ ] Handle 401 unauthorized responses
- [ ] Logout and clear tokens
- [ ] Persist login state across app restarts

---

## Phase 9: Testing (Week 9)

### Shared Tests (commonTest)

- [ ] Unit tests:
  - [ ] Domain models
  - [ ] Use cases
  - [ ] Mappers
  - [ ] Repository logic (mocked)
  - [ ] ViewModels (mocked dependencies)
- [ ] Test utilities:
  - [ ] Mock data factories
  - [ ] Test coroutine dispatchers

### Android Tests

- [ ] Compose UI tests:
  - [ ] Summary list interactions
  - [ ] Summary detail navigation
  - [ ] URL submission flow
  - [ ] Search functionality
- [ ] Screenshot tests (Paparazzi)

### iOS Tests

- [ ] XCTest UI tests:
  - [ ] Navigation flows
  - [ ] UI interactions
- [ ] Snapshot tests

---

## Phase 10: Polish & Optimization (Week 10)

### Performance

- [ ] Profile app launch time
- [ ] Optimize database queries
- [ ] Implement image caching (Coil/Kingfisher)
- [ ] Reduce bundle size
- [ ] Memory leak detection

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

- [ ] Network error messages
- [ ] Offline mode indicators
- [ ] Retry mechanisms
- [ ] Validation error messages

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

### Technical Improvements

- [ ] Migrate to Compose Multiplatform (when stable for iOS)
- [ ] GraphQL API integration (if backend migrates)
- [ ] End-to-end encryption for local database
- [ ] WebSocket for real-time request status updates
- [ ] Advanced caching strategies
- [ ] A/B testing framework
- [ ] Analytics integration

---

## Known Issues / Blockers

- [!] None currently

---

## Notes

- All tasks should be completed in order within each phase
- Each completed task should have a corresponding commit
- Write tests alongside implementation, not after
- Update this document as requirements change
- Mark tasks as [R] when they need code review

---

**Last Updated**: 2025-11-16
**Current Phase**: Phase 1 - Project Setup
**Overall Progress**: 0/280 tasks (0%)
