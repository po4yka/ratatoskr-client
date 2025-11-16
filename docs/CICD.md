# CI/CD Documentation

This document describes the Continuous Integration and Continuous Deployment (CI/CD) setup for the Bite-Size Reader Client project.

## Table of Contents

- [Overview](#overview)
- [Workflows](#workflows)
- [CI Workflow](#ci-workflow)
- [Release Workflow](#release-workflow)
- [Code Quality](#code-quality)
- [Testing](#testing)
- [Coverage Reporting](#coverage-reporting)
- [Secrets and Configuration](#secrets-and-configuration)
- [Troubleshooting](#troubleshooting)
- [Local Development](#local-development)

## Overview

The project uses GitHub Actions for CI/CD automation. The setup includes:

- **Automated testing** for shared, Android, and iOS code
- **Code quality checks** with ktlint
- **Test coverage reporting** with Kover
- **Security scanning** for dependencies
- **Automated builds** for Android (APK/AAB) and iOS (framework)
- **Release automation** with artifact generation

## Workflows

### Workflow Files

All workflows are located in `.github/workflows/`:

- `ci.yml` - Main CI workflow (runs on all PRs and pushes)
- `release.yml` - Release workflow (triggered on version tags)

### Workflow Triggers

**CI Workflow**:
- Push to `main` branch
- Pull requests to `main` branch
- Manual trigger via workflow dispatch

**Release Workflow**:
- Push of version tags (format: `v*.*.*`, e.g., `v1.0.0`)
- Manual trigger via workflow dispatch with version input

## CI Workflow

### Jobs Overview

The CI workflow consists of multiple jobs that run in parallel for efficiency:

```
├── lint (Code quality checks)
├── test-shared (Shared module tests + coverage)
├── test-android (Android tests)
├── build-android (Android APK build)
├── build-ios (iOS framework build + tests)
├── security-scan (Dependency scanning)
└── status-check (Overall status verification)
```

### 1. Lint Job

**Purpose**: Ensure code quality and consistency

**Steps**:
```bash
./gradlew ktlintCheck
```

**Runs on**: `ubuntu-latest`

**Fails if**:
- Code formatting violations found
- Style issues detected

**Fix locally**:
```bash
./gradlew ktlintFormat
```

### 2. Test Shared Job

**Purpose**: Run shared module tests and generate coverage report

**Steps**:
1. Run all shared tests
2. Generate Kover HTML coverage report
3. Comment coverage on PR (if applicable)

**Commands**:
```bash
./gradlew :shared:testDebugUnitTest
./gradlew :shared:koverHtmlReportDebug
```

**Coverage Requirements**:
- Overall: 75% minimum
- Changed files: 80% minimum

**Artifacts**:
- Test results: `shared/build/test-results/`
- Coverage report: `shared/build/reports/kover/htmlDebug/`

### 3. Test Android Job

**Purpose**: Run Android-specific tests

**Steps**:
1. Set up Android SDK
2. Run Android unit tests

**Commands**:
```bash
./gradlew :composeApp:testDebugUnitTest
```

**Runs on**: `ubuntu-latest`

**Artifacts**:
- Test results: `composeApp/build/test-results/`

### 4. Build Android Job

**Purpose**: Build Android APK to verify compilation

**Steps**:
1. Set up Android SDK
2. Build debug APK

**Commands**:
```bash
./gradlew :composeApp:assembleDebug
```

**Artifacts**:
- APK: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### 5. Build iOS Job

**Purpose**: Build iOS framework and run iOS tests

**Steps**:
1. Set up Xcode
2. Install CocoaPods dependencies
3. Build iOS framework
4. Run iOS tests

**Commands**:
```bash
cd iosApp && pod install
xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 15,OS=latest' build test
```

**Runs on**: `macos-14` (required for Xcode)

**Artifacts**:
- iOS framework: `shared/build/bin/iosSimulatorArm64/`

### 6. Security Scan Job

**Purpose**: Scan dependencies for known vulnerabilities

**Steps**:
1. Run Gradle dependency check
2. Report vulnerabilities

**Commands**:
```bash
./gradlew dependencyCheckAnalyze
```

**Fails if**:
- High or critical vulnerabilities found

### 7. Status Check Job

**Purpose**: Aggregate status of all jobs

**Depends on**: All previous jobs

This job succeeds only if all other jobs pass, providing a single status check for branch protection rules.

## Release Workflow

### Triggering a Release

#### Option 1: Git Tag (Recommended)

```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

#### Option 2: Manual Dispatch

1. Go to Actions → Release
2. Click "Run workflow"
3. Enter version (e.g., `1.0.0`)
4. Click "Run workflow"

### Release Process

1. **Version Validation**
   - Validates semantic versioning format (MAJOR.MINOR.PATCH)
   - Checks if version tag exists

2. **Build Android**
   - Builds signed release APK
   - Builds signed release AAB (for Play Store)
   - Signs with release keystore (from secrets)

3. **Build iOS**
   - Builds signed iOS IPA
   - Uses provisioning profile and certificates (from secrets)
   - Archives and exports for App Store distribution

4. **Extract Changelog**
   - Extracts version-specific changelog from CHANGELOG.md
   - Uses changelog in GitHub release description

5. **Create GitHub Release**
   - Creates release with version tag
   - Uploads artifacts (APK, AAB, IPA)
   - Includes changelog and release notes

### Release Artifacts

- `app-release.apk` - Android APK for direct installation
- `app-release.aab` - Android App Bundle for Play Store
- `app-release.ipa` - iOS IPA for App Store

Artifacts are retained for 30 days.

## Code Quality

### ktlint Configuration

**Version**: 1.0.1 (ktlint binary)

**Configuration** (`build.gradle.kts`):
```kotlin
configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("1.0.1")
    android.set(true)
    outputColorName.set("RED")
}
```

### Running Checks Locally

```bash
# Check all code
./gradlew ktlintCheck

# Auto-format
./gradlew ktlintFormat

# Check specific module
./gradlew :shared:ktlintCheck
```

### Code Style Rules

- **Max line length**: 120 characters
- **Indentation**: 4 spaces
- **Import order**: Alphabetical, with separators
- **Trailing commas**: Required in multi-line declarations
- **Blank lines**: Maximum 1 consecutive

## Testing

### Test Organization

```
shared/src/
├── commonTest/       # Shared tests
│   ├── domain/       # Domain model tests
│   ├── data/         # Repository, mapper tests
│   ├── presentation/ # ViewModel tests
│   └── util/         # Test utilities
├── androidTest/      # Android instrumented tests
└── iosTest/          # iOS-specific tests

composeApp/src/
└── androidTest/      # Compose UI tests

iosApp/
└── iosAppTests/      # iOS UI tests (XCTest)
```

### Running Tests Locally

```bash
# All tests
./gradlew test

# Shared tests
./gradlew :shared:testDebugUnitTest

# Android tests
./gradlew :composeApp:testDebugUnitTest

# With coverage
./gradlew :shared:koverHtmlReportDebug

# iOS tests (requires macOS)
cd iosApp
xcodebuild test -workspace iosApp.xcworkspace -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

### Test Requirements

- **Unit test coverage**: 75% minimum
- **Critical paths**: 90% coverage recommended
- **New features**: Tests required before merge
- **Bug fixes**: Regression tests required

## Coverage Reporting

### Kover Configuration

**Plugin**: `org.jetbrains.kotlinx.kover:0.8.3`

**Configuration** (`shared/build.gradle.kts`):
```kotlin
kover {
    reports {
        filters {
            excludes {
                classes("*.BuildConfig", "*.R", "*.R$*")
                packages("*.di", "*.util.test")
            }
        }
    }
}
```

### Coverage Reports

**Generate locally**:
```bash
./gradlew :shared:koverHtmlReportDebug
```

**View report**:
```bash
open shared/build/reports/kover/htmlDebug/index.html
```

**CI Report**: Coverage is automatically commented on PRs via the `madrapps/jacoco-report` action.

### Coverage Thresholds

- **Overall**: 75% (enforced)
- **Changed files**: 80% (enforced on PRs)
- **New files**: 90% (recommended)

## Secrets and Configuration

### Required Secrets

The following secrets must be configured in GitHub repository settings (Settings → Secrets and variables → Actions):

#### Android Signing (Release Only)

- `ANDROID_KEYSTORE_BASE64` - Base64-encoded release keystore
- `ANDROID_KEYSTORE_PASSWORD` - Keystore password
- `ANDROID_KEY_ALIAS` - Key alias
- `ANDROID_KEY_PASSWORD` - Key password

**Creating keystore**:
```bash
keytool -genkey -v -keystore release.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000

# Encode to base64
base64 -i release.keystore -o release_keystore_base64.txt
```

#### iOS Signing (Release Only)

- `IOS_CERTIFICATE_BASE64` - Base64-encoded P12 certificate
- `IOS_CERTIFICATE_PASSWORD` - P12 password
- `IOS_PROVISIONING_PROFILE_BASE64` - Base64-encoded provisioning profile
- `IOS_TEAM_ID` - Apple Developer Team ID
- `IOS_BUNDLE_ID` - App bundle identifier

**Creating certificate**:
```bash
# Export from Keychain
# File → Export Items → Save as .p12

# Encode to base64
base64 -i Certificates.p12 -o certificate_base64.txt
```

### Optional Secrets

- `GITHUB_TOKEN` - Automatically provided by GitHub Actions
- `CODECOV_TOKEN` - For Codecov integration (if used)

## Troubleshooting

### Common CI Issues

#### 1. ktlint Failures

**Error**: "Lint check failed"

**Solution**:
```bash
./gradlew ktlintFormat
git add .
git commit -m "style: fix code formatting"
git push
```

#### 2. Test Failures

**Error**: "Tests failed"

**Solution**:
```bash
# Run tests locally
./gradlew test

# Check specific test
./gradlew :shared:testDebugUnitTest --tests "*SpecificTest*"

# View test report
open shared/build/reports/tests/testDebugUnitTest/index.html
```

#### 3. Coverage Below Threshold

**Error**: "Coverage is below minimum threshold"

**Solution**:
- Add more tests for uncovered code
- Check coverage report locally
- Ensure test utilities are excluded from coverage

#### 4. Build Failures

**Error**: "Compilation failed"

**Solution**:
```bash
# Clean and rebuild
./gradlew clean build

# Check for dependency issues
./gradlew dependencies

# Update dependencies
./gradlew --refresh-dependencies
```

#### 5. iOS Build Failures (macOS Only)

**Error**: "xcodebuild failed"

**Solution**:
```bash
# Clean derived data
rm -rf ~/Library/Developer/Xcode/DerivedData

# Update CocoaPods
cd iosApp
pod deintegrate
pod install

# Rebuild
xcodebuild clean build -workspace iosApp.xcworkspace -scheme iosApp
```

#### 6. Release Workflow Issues

**Error**: "Failed to create release"

**Solution**:
- Ensure version tag follows format `v*.*.*`
- Check that secrets are configured correctly
- Verify CHANGELOG.md contains the version section
- Ensure all CI checks pass before release

### Debugging Workflows

**Enable debug logging**:
1. Go to Settings → Secrets
2. Add secret: `ACTIONS_STEP_DEBUG` = `true`
3. Re-run workflow

**View detailed logs**:
- Click on failed job
- Expand failed step
- Review error messages and stack traces

## Local Development

### Pre-commit Checks

Run these commands before committing:

```bash
# Format code
./gradlew ktlintFormat

# Run tests
./gradlew test

# Check coverage
./gradlew :shared:koverHtmlReportDebug

# Build Android
./gradlew :composeApp:assembleDebug

# Build iOS (macOS only)
cd iosApp
xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -configuration Debug build
```

### Git Hooks (Optional)

Create `.git/hooks/pre-commit`:

```bash
#!/bin/bash

echo "Running pre-commit checks..."

# Format code
./gradlew ktlintFormat

# Run tests
./gradlew test

if [ $? -ne 0 ]; then
  echo "Tests failed. Commit aborted."
  exit 1
fi

echo "Pre-commit checks passed!"
```

Make executable:
```bash
chmod +x .git/hooks/pre-commit
```

### Continuous Integration Best Practices

1. **Keep builds fast**: Current CI runs in ~10-15 minutes
2. **Run tests locally first**: Catch issues before pushing
3. **Small, focused commits**: Easier to review and debug
4. **Clear commit messages**: Follow conventional commits
5. **Update CHANGELOG.md**: For user-facing changes
6. **Monitor CI failures**: Fix immediately, don't accumulate

## Performance Optimization

### Caching Strategy

GitHub Actions caches:
- Gradle dependencies: `~/.gradle/caches`
- Gradle wrapper: `~/.gradle/wrapper`
- Android SDK: Cached by `setup-android` action
- CocoaPods: `~/.cocoapods`

### Build Time Optimization

**Gradle**:
```kotlin
// gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
kotlin.incremental=true
```

**Workflow optimization**:
- Use matrix strategy for parallel platform builds
- Cache dependencies aggressively
- Skip redundant steps (e.g., don't run iOS build for docs-only changes)

## Future Improvements

- [ ] Add screenshot testing
- [ ] Implement visual regression testing
- [ ] Add E2E tests with Maestro
- [ ] Set up Fastlane for iOS deployment
- [ ] Integrate with Google Play Store deployment
- [ ] Add performance benchmarking
- [ ] Implement automatic dependency updates (Dependabot)
- [ ] Add SLSA provenance generation

## Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [ktlint](https://pinterest.github.io/ktlint/)
- [Kover Coverage](https://github.com/Kotlin/kotlinx-kover)
- [Semantic Versioning](https://semver.org/)
- [Conventional Commits](https://www.conventionalcommits.org/)

---

For questions or issues with CI/CD, please:
- Check existing [Issues](https://github.com/po4yka/bite-size-reader-client/issues)
- Review workflow logs in the Actions tab
- Consult [CONTRIBUTING.md](../CONTRIBUTING.md)
