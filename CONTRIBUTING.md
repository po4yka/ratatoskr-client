# Contributing to Bite-Size Reader Client

Thank you for your interest in contributing to Bite-Size Reader! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Reporting Bugs](#reporting-bugs)
- [Feature Requests](#feature-requests)

## Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct:

- Be respectful and inclusive
- Welcome newcomers and help them get started
- Focus on what is best for the community
- Show empathy towards other community members
- Accept constructive criticism gracefully

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/bite-size-reader-client.git`
3. Add upstream remote: `git remote add upstream https://github.com/po4yka/bite-size-reader-client.git`
4. Create a feature branch: `git checkout -b feature/my-feature`

## Development Setup

### Prerequisites

- **JDK**: 17 or higher
- **Android Studio**: Latest stable version (Ladybug or later)
- **Xcode**: 15.0+ (for iOS development, macOS only)
- **Kotlin**: 2.2.21+
- **CocoaPods**: Latest version (for iOS dependencies)

### Initial Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/po4yka/bite-size-reader-client.git
   cd bite-size-reader-client
   ```

2. **Sync Gradle dependencies**:
   ```bash
   ./gradlew build
   ```

3. **For iOS development**:
   ```bash
   cd iosApp
   pod install
   ```

4. **Configure API endpoint** (if needed):
   - Update `shared/src/commonMain/kotlin/com/po4yka/bitesizereader/data/remote/ApiConfig.kt`

5. **Set up Telegram Bot** (for authentication testing):
   - Follow instructions in `AUTHENTICATION.md`

### Running the App

**Android**:
```bash
./gradlew :composeApp:installDebug
```

**iOS**:
- Open `iosApp/iosApp.xcworkspace` in Xcode
- Select target device/simulator
- Press Cmd+R to build and run

## Project Structure

```
bite-size-reader-client/
├── composeApp/          # Android app module
│   ├── src/
│   │   ├── androidMain/ # Android-specific code
│   │   └── commonMain/  # Shared Compose UI code
├── iosApp/              # iOS app
│   ├── iosApp/          # SwiftUI views and ViewModels
│   └── iosApp.xcodeproj
├── shared/              # Shared KMP module
│   ├── src/
│   │   ├── commonMain/  # Shared business logic
│   │   ├── androidMain/ # Android platform code
│   │   ├── iosMain/     # iOS platform code
│   │   └── commonTest/  # Shared tests
├── gradle/              # Gradle configuration
│   └── libs.versions.toml
└── build.gradle.kts
```

### Module Responsibilities

- **composeApp**: Android UI layer (Jetpack Compose)
- **iosApp**: iOS UI layer (SwiftUI)
- **shared**: Business logic, data layer, domain models (platform-agnostic)

## Development Workflow

### 1. Before Starting

- Sync with upstream:
  ```bash
  git fetch upstream
  git checkout main
  git merge upstream/main
  ```

### 2. Create a Branch

Follow the naming convention:
- Features: `feature/feature-name`
- Bug fixes: `fix/bug-description`
- Documentation: `docs/what-you-are-documenting`
- Refactoring: `refactor/what-you-are-refactoring`

```bash
git checkout -b feature/my-awesome-feature
```

### 3. Make Your Changes

- Write clear, self-documenting code
- Follow the coding standards (see below)
- Add tests for new functionality
- Update documentation as needed

### 4. Test Your Changes

```bash
# Run all tests
./gradlew test

# Run Android tests
./gradlew :composeApp:testDebugUnitTest

# Run shared tests with coverage
./gradlew :shared:koverHtmlReportDebug

# Run ktlint checks
./gradlew ktlintCheck

# Auto-format code
./gradlew ktlintFormat
```

### 5. Commit Your Changes

Follow conventional commit format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Example**:
```bash
git commit -m "feat(summary): add offline reading mode

- Implement local caching for summaries
- Add download manager for content
- Update UI to show download status

Closes #123"
```

### 6. Push and Create PR

```bash
git push origin feature/my-awesome-feature
```

Then create a Pull Request on GitHub.

## Coding Standards

### Kotlin

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use ktlint for formatting (run `./gradlew ktlintFormat`)
- Maximum line length: 120 characters
- Use meaningful variable and function names
- Prefer immutability (`val` over `var`)
- Use sealed classes for state modeling
- Document public APIs with KDoc

**Example**:
```kotlin
/**
 * Fetches a summary by its ID from the repository.
 *
 * @param id The unique identifier of the summary
 * @return Result containing the Summary or an error
 */
suspend fun getSummaryById(id: Int): Result<Summary> {
    return repository.getSummaryById(id)
}
```

### Swift

- Follow [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/)
- Use SwiftLint if available
- Prefer value types (struct) over reference types (class) when appropriate
- Use meaningful names that read like English
- Document public APIs with DocC comments

**Example**:
```swift
/// Fetches and displays the summary for the given ID.
///
/// - Parameter id: The unique identifier of the summary to fetch
func loadSummary(id: Int) async {
    // Implementation
}
```

### Architecture

- **MVI Pattern**: Use Model-View-Intent for state management
- **Single Source of Truth**: State flows from ViewModels to UI
- **Unidirectional Data Flow**: User actions → Intent → State update → UI render
- **Repository Pattern**: Abstract data sources behind repository interfaces
- **Dependency Injection**: Use Koin for DI

### File Organization

- One class per file (exceptions for small sealed class hierarchies)
- Group related files in appropriate packages
- Use package-by-feature organization

## Testing

### Test Requirements

- **Unit tests**: All business logic and ViewModels
- **UI tests**: Critical user flows
- **Integration tests**: Repository and API interactions
- **Coverage target**: 75% minimum

### Writing Tests

**Unit Test Example**:
```kotlin
@Test
fun `login with valid credentials succeeds`() = runTest {
    // Given
    val expectedUser = MockDataFactory.createUser()
    coEvery { mockRepository.login(any(), any()) } returns Result.success(expectedUser)

    // When
    val result = loginUseCase(username, password)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(expectedUser, result.getOrNull())
}
```

**UI Test Example**:
```kotlin
@Test
fun summaryList_displaysItems() {
    composeTestRule.setContent {
        SummaryListScreen(viewModel = viewModel)
    }

    composeTestRule
        .onNodeWithText("Test Article")
        .assertExists()
        .assertIsDisplayed()
}
```

### Running Tests Locally

```bash
# All tests
./gradlew test

# Specific module
./gradlew :shared:test

# With coverage
./gradlew :shared:koverHtmlReportDebug

# View coverage report
open shared/build/reports/kover/htmlDebug/index.html
```

## Submitting Changes

### Pull Request Process

1. **Update your branch** with latest main:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Ensure all checks pass**:
   - All tests pass
   - Code coverage meets minimum (75%)
   - ktlint passes
   - No build warnings

3. **Create a Pull Request**:
   - Use the PR template
   - Provide a clear description
   - Reference related issues
   - Add screenshots/videos for UI changes
   - Request review from maintainers

4. **Address review feedback**:
   - Make requested changes
   - Push updates to your branch
   - Respond to comments

5. **Squash and merge**:
   - Maintainers will squash commits when merging
   - Ensure your PR title follows conventional commit format

### PR Checklist

Before submitting, ensure:

- [ ] Code follows project coding standards
- [ ] Tests added/updated for changes
- [ ] All tests pass locally
- [ ] ktlint checks pass
- [ ] Documentation updated (if applicable)
- [ ] CHANGELOG.md updated (for significant changes)
- [ ] Screenshots/videos added (for UI changes)
- [ ] No breaking changes (or clearly documented)

## Reporting Bugs

Use the [Bug Report template](.github/ISSUE_TEMPLATE/bug_report.md) when reporting bugs.

**Include**:
- Clear, descriptive title
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots/logs
- Environment details (OS, device, app version)

## Feature Requests

Use the [Feature Request template](.github/ISSUE_TEMPLATE/feature_request.md) for new features.

**Include**:
- Clear description of the feature
- Use case / problem it solves
- Proposed solution
- Alternative solutions considered
- Additional context

## Questions?

- Check existing [Issues](https://github.com/po4yka/bite-size-reader-client/issues)
- Review [Documentation](README.md)
- Ask in [Discussions](https://github.com/po4yka/bite-size-reader-client/discussions)

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

Thank you for contributing to Bite-Size Reader! 🎉
