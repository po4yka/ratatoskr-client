# Testing Guide

This document describes the testing strategy and how to run tests for the Bite-Size Reader mobile client.

## Testing Strategy

The project follows a comprehensive testing strategy with multiple layers:

1. **Unit Tests** (shared/commonTest) - Test business logic in isolation
2. **Android UI Tests** (composeApp/androidTest) - Test Android Compose UI
3. **iOS UI Tests** (iosApp/iosAppUITests) - Test SwiftUI screens

## Test Coverage Goals

- **Target**: 80% code coverage for shared code
- **Focus Areas**:
  - Domain models
  - Use cases
  - Mappers
  - ViewModels
  - Repository logic

## Running Tests

### Shared Unit Tests (Kotlin Multiplatform)

Run all shared tests:
```bash
./gradlew :shared:testDebugUnitTest
```

Run tests for a specific source set:
```bash
# Android tests
./gradlew :shared:testDebugUnitTest

# iOS tests
./gradlew :shared:iosSimulatorArm64Test
```

### Android Compose UI Tests

Run Android instrumented tests:
```bash
./gradlew :composeApp:connectedAndroidTest
```

Run on a specific device:
```bash
adb devices  # List devices
./gradlew :composeApp:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.po4yka.bitesizereader.ui.screens.AuthScreenTest
```

### iOS UI Tests

Run iOS UI tests from Xcode:
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select the test target (`iosAppUITests`)
3. Press `Cmd + U` to run all tests

Run from command line:
```bash
xcodebuild test \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15'
```

## Test Structure

### Shared Tests (`shared/src/commonTest`)

```
commonTest/
├── kotlin/com/po4yka/bitesizereader/
│   ├── domain/
│   │   ├── model/           # Domain model tests
│   │   └── usecase/         # Use case tests
│   ├── data/
│   │   └── mappers/         # Mapper tests
│   ├── presentation/
│   │   └── viewmodel/       # ViewModel tests
│   └── util/                # Test utilities
│       ├── MockDataFactory.kt
│       ├── TestDispatchers.kt
│       └── CoroutineTestBase.kt
```

### Android Tests (`composeApp/src/androidTest`)

```
androidTest/
└── kotlin/com/po4yka/bitesizereader/
    └── ui/
        └── screens/         # Compose UI tests
```

### iOS Tests (`iosApp/iosAppUITests`)

```
iosAppUITests/
├── AuthViewTests.swift
├── SummaryListViewTests.swift
└── SummaryDetailViewTests.swift
```

## Test Utilities

### MockDataFactory

Factory for creating test data:

```kotlin
// Create a single summary
val summary = MockDataFactory.createSummary(
    id = 1,
    title = "Test Article",
    isRead = false
)

// Create multiple summaries
val summaries = MockDataFactory.createSummaryList(count = 10)

// Create auth tokens
val tokens = MockDataFactory.createAuthTokens()

// Create user
val user = MockDataFactory.createUser()
```

### Test Dispatchers

Utilities for testing coroutines:

```kotlin
class MyViewModelTest : CoroutineTestBase() {
    // CoroutineTestBase automatically sets up and tears down test dispatchers

    @Test
    fun myTest() = runTest {
        // Test code here
    }
}
```

### Testing Flows with Turbine

Testing Kotlin Flows:

```kotlin
@Test
fun `state flow emits correct values`() = runTest {
    viewModel.state.test {
        val initialState = awaitItem()
        // Assert initial state

        viewModel.performAction()

        val updatedState = awaitItem()
        // Assert updated state
    }
}
```

## Writing Tests

### Domain Model Tests

```kotlin
@Test
fun `Summary is created with correct properties`() {
    // Given
    val summary = MockDataFactory.createSummary(id = 1)

    // Then
    assertEquals(1, summary.id)
    assertFalse(summary.isRead)
}
```

### Use Case Tests

```kotlin
@Test
fun `invoke returns success when repository succeeds`() = runTest {
    // Given
    val mockRepository = mockk<SummaryRepository>()
    val useCase = GetSummaryByIdUseCase(mockRepository)
    coEvery { mockRepository.getSummaryById(1) } returns flowOf(mockSummary)

    // When
    val result = useCase(1).first()

    // Then
    assertEquals(mockSummary, result)
    coVerify { mockRepository.getSummaryById(1) }
}
```

### ViewModel Tests

```kotlin
@Test
fun `login success updates state`() = runTest {
    // Given
    val viewModel = createViewModel()
    coEvery { mockLoginUseCase(...) } returns Result.success(...)

    // When
    viewModel.loginWithTelegram(...)
    advanceUntilIdle()

    // Then
    viewModel.state.test {
        val state = awaitItem()
        assertTrue(state.isAuthenticated)
    }
}
```

### Android Compose UI Tests

```kotlin
@Test
fun authScreen_displaysLoginButton() {
    // Given
    val viewModel = createViewModel()

    // When
    composeTestRule.setContent {
        AuthScreen(viewModel = viewModel, onLoginSuccess = {})
    }

    // Then
    composeTestRule
        .onNodeWithText("Login with Telegram")
        .assertExists()
        .assertIsDisplayed()
}
```

### iOS UI Tests

```swift
func testLoginButtonExists() throws {
    // Given
    let app = XCUIApplication()
    app.launch()

    // When: Auth screen is displayed

    // Then: Login button should exist
    let loginButton = app.buttons["Login with Telegram"]
    XCTAssertTrue(loginButton.exists)
}
```

## Test Coverage

### Generating Coverage Reports

Run tests with coverage:
```bash
./gradlew :shared:koverHtmlReportDebug
```

View coverage report:
```bash
open shared/build/reports/kover/htmlDebug/index.html
```

### Coverage Requirements

- **Minimum**: 75% overall coverage
- **Target**: 80% overall coverage
- **Critical Paths**: 95%+ coverage
  - Authentication flow
  - Summary CRUD operations
  - Error handling

## Continuous Integration

Tests run automatically on:
- Pull request creation
- Push to main branch
- Manual workflow dispatch

See `.github/workflows/test.yml` for CI configuration.

## Best Practices

### 1. Test Naming

Use descriptive test names that explain what is being tested:

```kotlin
// Good ✅
@Test
fun `login with valid credentials returns success`()

// Bad ❌
@Test
fun testLogin()
```

### 2. Test Structure

Follow the Given-When-Then pattern:

```kotlin
@Test
fun myTest() {
    // Given - Set up test data and mocks
    val mockData = createMockData()

    // When - Execute the operation being tested
    val result = performOperation(mockData)

    // Then - Assert the expected outcome
    assertEquals(expected, result)
}
```

### 3. Test Independence

Each test should be independent and not rely on other tests:

```kotlin
// Good ✅
@Test
fun test1() {
    val data = createFreshData()
    // Test logic
}

@Test
fun test2() {
    val data = createFreshData()
    // Test logic
}

// Bad ❌
lateinit var sharedData  // Don't share mutable state between tests
```

### 4. Mock Only What You Need

Mock external dependencies, not the system under test:

```kotlin
// Good ✅
val mockRepository = mockk<Repository>()
val useCase = RealUseCase(mockRepository)

// Bad ❌
val mockUseCase = mockk<UseCase>()  // Don't mock what you're testing
```

### 5. Test Edge Cases

Test happy path, error cases, and edge cases:

```kotlin
@Test
fun `handles empty list correctly`()

@Test
fun `handles network error gracefully`()

@Test
fun `handles null values safely`()
```

## Troubleshooting

### Tests Fail Locally But Pass in CI

- Clear build cache: `./gradlew clean`
- Delete `.gradle` folder
- Restart IDE

### Flaky Tests

- Ensure tests are independent
- Use `runTest` for coroutine tests
- Avoid `delay()`, use `advanceUntilIdle()`
- Check for race conditions

### iOS Tests Not Running

- Ensure simulator is selected in Xcode
- Clean build folder: `Cmd + Shift + K`
- Reset simulator: `Device > Erase All Content and Settings`

### Android Tests Timeout

- Increase timeout in test annotation:
  ```kotlin
  @Test(timeout = 10000)
  ```
- Check for deadlocks in coroutines
- Verify mock responses are configured correctly

## Resources

- [Kotlin Test Documentation](https://kotlinlang.org/api/latest/kotlin.test/)
- [MockK Documentation](https://mockk.io/)
- [Turbine Documentation](https://github.com/cashapp/turbine)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [XCTest Documentation](https://developer.apple.com/documentation/xctest)

## Contributing

When adding new features:

1. Write tests **before** or **alongside** implementation
2. Aim for 80%+ coverage of new code
3. Run tests locally before pushing
4. Update this guide if adding new test patterns

## Test Maintenance

- Review and update tests when requirements change
- Remove obsolete tests for removed features
- Refactor tests when code is refactored
- Keep test utilities up to date

---

**Last Updated**: 2025-11-16
