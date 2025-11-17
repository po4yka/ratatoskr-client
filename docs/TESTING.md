# Testing Documentation

**Last Updated**: 2025-11-17
**Current Coverage**: ~60-70%
**Target Coverage**: 85%+

---

## Table of Contents

1. [Overview](#overview)
2. [Testing Strategy](#testing-strategy)
3. [Running Tests](#running-tests)
4. [Writing Tests](#writing-tests)
5. [Test Types](#test-types)
6. [Mocking](#mocking)
7. [Best Practices](#best-practices)
8. [Coverage](#coverage)
9. [CI/CD Integration](#cicd-integration)

---

## Overview

Bite-Size Reader uses a comprehensive testing strategy with multiple test types:

| Test Type | Coverage | Tools | Location |
|-----------|----------|-------|----------|
| **Unit Tests** | ~70% | JUnit, MockK, Turbine | `shared/src/commonTest/` |
| **Integration Tests** | ~40% | JUnit, MockK | `shared/src/commonTest/` |
| **UI Tests** | ~20% | Compose Testing, XCTest | `composeApp/`, `iosApp/` |
| **E2E Tests** | Planned | Maestro, XCUITest | TBD |

---

## Testing Strategy

### Test Pyramid

```
         /\
        /  \  E2E Tests (Planned)
       /____\
      /      \
     / UI     \ ~20% coverage
    / Tests    \
   /____________\
  /              \
 / Integration    \ ~40% coverage
/__________________\
/                    \
/    Unit Tests      \ ~70% coverage
/____________________\
```

### Coverage Goals

| Layer | Target | Current | Priority |
|-------|--------|---------|----------|
| Domain (Use Cases) | 90% | 85% | ✅ Good |
| Data (Repositories) | 80% | 70% | 🟡 Needs improvement |
| Presentation (ViewModels) | 80% | 60% | 🔴 Priority |
| UI (Composables/SwiftUI) | 50% | 20% | 🔴 Priority |

---

## Running Tests

### All Tests

```bash
# Run all tests (Android + Shared)
./gradlew test

# Run only shared tests
./gradlew :shared:test

# Run Android tests
./gradlew :composeApp:testDebugUnitTest

# Run with coverage
./gradlew koverHtmlReportDebug
open shared/build/reports/kover/htmlDebug/index.html
```

### Specific Test Classes

```bash
# Run specific test class
./gradlew :shared:testDebugUnitTest --tests SummaryListViewModelTest

# Run specific test method
./gradlew :shared:testDebugUnitTest --tests "SummaryListViewModelTest.loadSummaries updates state with summaries"
```

### iOS Tests

```bash
# From command line
xcodebuild test -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'

# Or from Xcode
# Product → Test (⌘U)
```

---

## Writing Tests

### Unit Test Template

```kotlin
class GetSummariesUseCaseTest {

    private lateinit var mockRepository: SummaryRepository
    private lateinit var useCase: GetSummariesUseCase

    @BeforeTest
    fun setup() {
        mockRepository = mockk()
        useCase = GetSummariesUseCase(mockRepository)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `returns summaries from repository`() = runTest {
        // Given
        val expectedSummaries = listOf(
            createTestSummary(id = 1),
            createTestSummary(id = 2)
        )
        coEvery { mockRepository.getSummaries(any(), any(), any()) } returns
            flowOf(expectedSummaries)

        // When
        val result = useCase(limit = 20, offset = 0, filters = SearchFilters())
            .first()

        // Then
        assertEquals(expectedSummaries, result)
        coVerify { mockRepository.getSummaries(20, 0, any()) }
    }
}
```

### ViewModel Test Template

```kotlin
class SummaryListViewModelTest {

    private lateinit var mockGetSummariesUseCase: GetSummariesUseCase
    private lateinit var testScope: TestScope
    private lateinit var viewModel: SummaryListViewModel

    @BeforeTest
    fun setup() {
        mockGetSummariesUseCase = mockk()
        testScope = TestScope()

        coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns
            flowOf(emptyList())
    }

    @Test
    fun `loadSummaries updates state with summaries`() = runTest {
        // Given
        val summaries = listOf(createTestSummary(id = 1))
        coEvery { mockGetSummariesUseCase(any(), any(), any()) } returns
            flowOf(summaries)

        // When
        viewModel = SummaryListViewModel(mockGetSummariesUseCase, testScope)
        testScope.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(summaries, state.summaries)
            assertFalse(state.isLoading)
        }
    }
}
```

---

## Test Types

### 1. Unit Tests

**Purpose**: Test individual components in isolation

**Tools**:
- JUnit 4
- MockK (mocking)
- Turbine (Flow testing)
- kotlinx-coroutines-test

**Examples**:
- Use Cases
- ViewModels
- Utility functions
- Error handling

### 2. Integration Tests

**Purpose**: Test multiple components working together

**Examples**:
- Repository + Database
- Use Case + Repository
- End-to-end flows

### 3. UI Tests

**Android (Compose)**:
```kotlin
@Test
fun pullToRefresh_triggersDataReload() {
    composeTestRule.setContent {
        SummaryListScreen(viewModel = mockViewModel)
    }

    composeTestRule
        .onNodeWithTag("summaryList")
        .performTouchInput { swipeDown() }

    verify { mockViewModel.refresh() }
}
```

**iOS (SwiftUI + XCTest)**:
```swift
func testSummaryListDisplaysSummaries() {
    let app = XCUIApplication()
    app.launch()

    XCTAssertTrue(app.staticTexts["Test Summary 1"].exists)
}
```

---

## Mocking

### MockK Basics

```kotlin
// Create mock
val mockRepo = mockk<SummaryRepository>()

// Stub behavior
coEvery { mockRepo.getSummaryById(1) } returns testSummary

// Verify calls
coVerify { mockRepo.getSummaryById(1) }

// Relaxed mocks
val mockRepo = mockk<SummaryRepository>(relaxed = true)
```

### Flow Testing with Turbine

```kotlin
@Test
fun `state flow emits multiple values`() = runTest {
    viewModel.state.test {
        assertEquals(SummaryListState(), awaitItem())

        viewModel.loadSummaries()

        assertTrue(awaitItem().isLoading)
        assertFalse(awaitItem().isLoading)
    }
}
```

---

## Best Practices

### DO ✅

1. **Follow AAA Pattern** (Arrange, Act, Assert)
2. **Use Descriptive Test Names**
3. **Test One Thing Per Test**
4. **Use Test Factories**
5. **Clean Up Resources**

### DON'T ❌

1. **Don't Test Implementation Details**
2. **Don't Use Real Network/Database in Unit Tests**
3. **Don't Ignore Flaky Tests**
4. **Don't Write Tests After Code**

---

## Coverage

### Viewing Coverage Reports

```bash
./gradlew koverHtmlReportDebug
open shared/build/reports/kover/htmlDebug/index.html
```

### Coverage Goals

| Module | Current | Target |
|--------|---------|--------|
| Domain | 85% | 90% |
| Data | 70% | 80% |
| Presentation | 60% | 80% |
| UI | 20% | 50% |
| **Overall** | **~65%** | **85%** |

---

## CI/CD Integration

### GitHub Actions

```yaml
- name: Run tests
  run: ./gradlew test

- name: Upload coverage
  uses: codecov/codecov-action@v4
  with:
    files: ./build/reports/kover/report.xml
```

---

## Resources

- [Kotlin Test](https://kotlinlang.org/api/latest/kotlin.test/)
- [MockK](https://mockk.io/)
- [Turbine](https://github.com/cashapp/turbine)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)

---

**Created**: 2025-11-17
**Maintained By**: Development Team
