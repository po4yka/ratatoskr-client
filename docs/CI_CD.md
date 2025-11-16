# CI/CD Documentation

This document describes the comprehensive CI/CD setup for the Bite-Size Reader KMP (Kotlin Multiplatform) project.

## Overview

The CI/CD pipeline is built using **GitHub Actions** and follows industry best practices for Kotlin Multiplatform projects. It provides automated building, testing, code quality checks, and deployment capabilities for both Android and iOS platforms.

## Workflows

### 1. PR Validation (`pr-validation.yml`)

**Trigger:** Pull requests to `main` or `develop` branches

**Purpose:** Validate code changes before merging

**Jobs:**
- ✅ **validate-shared**: Build and test shared KMP module
- 📱 **build-android**: Build Android APK and run unit tests
- 🍎 **build-ios**: Build iOS frameworks and run tests (macOS runner)
- 🔍 **code-quality**: Run detekt and dependency checks
- 📊 **validation-summary**: Aggregate results from all jobs

**Features:**
- Concurrent job execution for faster feedback
- Conditional execution based on PR labels (`skip-android`, `skip-ios`)
- Test result artifacts uploaded for debugging
- Automatic cancellation of outdated runs

**Cost Optimization:**
```yaml
# Skip iOS build if only Android changes
if: "!contains(github.event.pull_request.labels.*.name, 'skip-ios')"
```

### 2. Main CI (`ci.yml`)

**Trigger:** Pushes to `main` or `develop` branches, or manual dispatch

**Purpose:** Comprehensive validation and artifact generation for main branches

**Jobs:**
- 🔨 **build-all**: Matrix build for Android (ubuntu-latest) and iOS (macos-14)
- 📊 **coverage**: Generate test coverage reports (JaCoCo)
- 🔒 **security-check**: Dependency vulnerability scanning
- 🚨 **notify-failure**: Alert on build failures

**Features:**
- Full test suite execution
- Release APK building (if signing keys configured)
- Coverage report generation
- Artifact retention (14 days)
- Matrix strategy for parallel platform builds

**Artifacts Generated:**
- Android Debug APK
- Android Release APK (if signing configured)
- iOS XCFramework
- Test results
- Coverage reports

### 3. Release Build (`release.yml`)

**Trigger:** Git tags matching `v*.*.*` (e.g., `v1.0.0`) or manual dispatch

**Purpose:** Build and publish production releases

**Jobs:**
- 📦 **create-release**: Create GitHub release (draft)
- 🤖 **build-android-release**: Build signed Android APK/AAB
- 🍏 **build-ios-release**: Build iOS IPA and XCFramework
- ✅ **finalize-release**: Publish release after successful builds

**Android Release Features:**
- APK signing with release keystore
- AAB (Android App Bundle) generation
- Optional Google Play publishing via Gradle plugin
- Automatic version tagging

**iOS Release Features:**
- XCFramework creation for distribution
- Code signing with certificates and provisioning profiles
- IPA export for App Store submission
- Optional Fastlane deployment

**Required Secrets:**

Android:
- `ANDROID_KEYSTORE_RELEASE_B64` - Base64-encoded release keystore
- `ANDROID_KEYSTORE_PASSWORD` - Keystore password
- `ANDROID_KEY_ALIAS` - Key alias
- `ANDROID_KEY_PASSWORD` - Key password
- `GOOGLE_PLAY_SERVICE_ACCOUNT_JSON` - Google Play service account (optional)

iOS:
- `IOS_CERTIFICATE_B64` - Base64-encoded .p12 certificate
- `IOS_CERTIFICATE_PASSWORD` - Certificate password
- `IOS_PROVISIONING_PROFILE_B64` - Base64-encoded provisioning profile
- `IOS_CODE_SIGN_IDENTITY` - Code signing identity
- `IOS_PROVISIONING_PROFILE_SPECIFIER` - Profile specifier
- `KEYCHAIN_PASSWORD` - Temporary keychain password
- `APP_STORE_CONNECT_API_KEY_B64` - App Store Connect API key (optional)

### 4. Code Quality (`code-quality.yml`)

**Trigger:** PRs, pushes to main/develop, weekly schedule (Mondays 9 AM UTC), or manual

**Purpose:** Enforce code quality standards and detect issues

**Jobs:**
- 🔍 **kotlin-lint**: Run detekt for Kotlin code analysis
- 🔐 **dependency-scan**: Check for dependency updates and vulnerabilities
- ✔️ **gradle-validation**: Validate Gradle wrapper integrity
- 🔒 **static-analysis**: Search for TODOs, FIXMEs, and potential secrets
- 📊 **code-metrics**: Generate code statistics
- 📄 **license-check**: Verify LICENSE file exists
- 📝 **quality-summary**: Aggregate quality check results

**Security Checks:**
- Hardcoded secrets detection
- .gitignore compliance verification
- Sensitive file detection

**Code Metrics:**
- Lines of code count
- File count per module
- Test file coverage

### 5. Dependabot (`dependabot.yml`)

**Purpose:** Automated dependency updates

**Configuration:**
- Weekly updates on Mondays at 9 AM UTC
- Separate ecosystems: Gradle dependencies and GitHub Actions
- Grouped updates for related packages (Kotlin, AndroidX, Compose)
- Auto-assigned reviewers and labels
- Limited to 10 Gradle PRs and 5 GitHub Actions PRs

**Dependency Groups:**
- `kotlin` - All Kotlin and KotlinX libraries
- `androidx` - AndroidX libraries
- `compose` - Compose Multiplatform and AndroidX Compose

## Caching Strategy

### Gradle Caching

Uses `gradle/actions/setup-gradle@v4` with:
- Automatic Gradle dependency caching
- Gradle home cache cleanup (removes unused files before saving)
- Optional cache encryption via `GRADLE_CACHE_ENCRYPTION_KEY` secret

```yaml
- name: Setup Gradle
  uses: gradle/actions/setup-gradle@v4
  with:
    gradle-home-cache-cleanup: true
    cache-encryption-key: ${{ secrets.GRADLE_CACHE_ENCRYPTION_KEY }}
```

### Konan Caching (iOS)

Caches `~/.konan` directory for Kotlin/Native dependencies:

```yaml
- name: Cache Konan dependencies
  uses: actions/cache@v4
  with:
    path: ~/.konan
    key: ${{ runner.os }}-konan-${{ hashFiles('**/*.gradle.kts', 'gradle/libs.versions.toml') }}
```

**Benefits:**
- Faster iOS builds (10x cost savings on macOS runners)
- Reduced network usage
- Consistent build environments

## Runner Configuration

| Job | Runner | Cost | Reason |
|-----|--------|------|--------|
| Android builds | `ubuntu-latest` | Low | Standard JVM/Android SDK support |
| iOS builds | `macos-14` | High (10x) | Required for Xcode and iOS toolchain |
| Code quality | `ubuntu-latest` | Low | Text-based analysis only |

**Cost Optimization Tips:**
1. Use PR labels to skip unnecessary platform builds
2. Enable concurrency cancellation to stop outdated runs
3. Cache Gradle and Konan dependencies aggressively
4. Run iOS builds only when needed (not for Android-only changes)

## Setup Instructions

### 1. Enable GitHub Actions

GitHub Actions are enabled by default. Verify in repository **Settings → Actions → General**.

### 2. Configure Secrets

Navigate to **Settings → Secrets and variables → Actions** and add:

**Optional (for caching):**
- `GRADLE_CACHE_ENCRYPTION_KEY` - Any secure string for cache encryption

**For Android releases:**
```bash
# Generate keystore (if not already done)
keytool -genkey -v -keystore release.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000

# Encode to Base64
base64 -i release.keystore | pbcopy  # macOS
base64 -w 0 release.keystore | xclip  # Linux
```

Add secrets:
- `ANDROID_KEYSTORE_RELEASE_B64` - Paste Base64 output
- `ANDROID_KEYSTORE_PASSWORD` - Your keystore password
- `ANDROID_KEY_ALIAS` - Your key alias
- `ANDROID_KEY_PASSWORD` - Your key password

**For iOS releases:**
```bash
# Export certificate from Keychain as .p12
# Then encode
base64 -i certificate.p12 | pbcopy

# Encode provisioning profile
base64 -i profile.mobileprovision | pbcopy
```

Add secrets:
- `IOS_CERTIFICATE_B64` - Certificate Base64
- `IOS_CERTIFICATE_PASSWORD` - Certificate password
- `IOS_PROVISIONING_PROFILE_B64` - Profile Base64
- `IOS_CODE_SIGN_IDENTITY` - e.g., "Apple Distribution: Your Name (TEAM_ID)"
- `IOS_PROVISIONING_PROFILE_SPECIFIER` - Profile name
- `KEYCHAIN_PASSWORD` - Any secure string for temporary keychain

### 3. First Run

Push code or create a PR to trigger workflows:

```bash
# For PRs
git checkout -b feature/my-feature
git push origin feature/my-feature
# Create PR via GitHub UI

# For releases
git tag v1.0.0
git push origin v1.0.0
```

### 4. Monitor Workflows

View workflow runs in **Actions** tab:
- Green ✅ = Success
- Red ❌ = Failure (click for logs)
- Yellow 🟡 = In progress

## Advanced Features

### Conditional Job Execution

Skip platform builds using PR labels:

```bash
# Add label via GitHub UI or CLI
gh pr edit <pr-number> --add-label "skip-ios"
```

Available labels:
- `skip-android` - Skip Android build
- `skip-ios` - Skip iOS build

### Manual Workflow Dispatch

Trigger workflows manually via GitHub UI:
1. Go to **Actions** tab
2. Select workflow (e.g., "CI - Main Branch")
3. Click **Run workflow** button
4. Choose branch and inputs (if any)

### Artifact Download

Download build artifacts from workflow runs:
1. Go to workflow run page
2. Scroll to **Artifacts** section
3. Click artifact name to download

Artifacts available:
- `android-debug-apk` - Debug APK for testing
- `android-release-apk` - Signed release APK
- `ios-xcframework` - iOS framework distribution
- `test-results-*` - Test reports (HTML)
- `coverage-reports` - Code coverage (JaCoCo)

### Scheduled Runs

Code quality checks run automatically every Monday at 9 AM UTC to catch issues early.

## Troubleshooting

### Build Failures

**Android build fails with "SDK not found":**
- Ensure `ANDROID_HOME` is set (GitHub runners have this pre-configured)
- Check `compileSdk` and `targetSdk` versions in `build.gradle.kts`

**iOS build fails with "Xcode not found":**
- Verify `macos-14` runner is used (not `ubuntu-latest`)
- Check Xcode version selection in workflow

**Gradle task not found:**
- Verify task name matches your `build.gradle.kts`
- Run locally first: `./gradlew tasks --all`

### Caching Issues

**Cache not restoring:**
- Check cache key includes `hashFiles()` for dependency files
- Verify cache size doesn't exceed GitHub's 10 GB limit

**Builds slow despite caching:**
- Review cache hit/miss rates in logs
- Consider splitting caches by module or platform

### Signing Issues

**Android: "Keystore was tampered with":**
- Re-encode keystore: `base64 -i release.keystore` (no line breaks)
- Verify password matches

**iOS: "No matching provisioning profiles":**
- Check bundle ID matches provisioning profile
- Verify certificate is not expired
- Ensure profile is for App Store distribution (not development)

### Secrets Not Found

**Error: "Secret `XYZ` not found":**
- Verify secret name matches exactly (case-sensitive)
- Check secret is set at repository level (not organization)
- Re-create secret if corrupted

## Best Practices

### 1. Commit Messages

Follow Conventional Commits for automatic changelog generation:

```
feat: add offline sync capability
fix: resolve crash on empty summary list
docs: update CI/CD documentation
ci: optimize iOS build caching
```

### 2. PR Labels

Use labels to optimize CI runs:

- `skip-android` - Backend-only or iOS-only changes
- `skip-ios` - Android-only changes
- `dependencies` - Dependency updates (auto-added by Dependabot)

### 3. Branch Protection

Enable branch protection on `main`:
1. **Settings → Branches → Add rule**
2. Branch name pattern: `main`
3. Required checks:
   - ✅ Validate Shared Module
   - ✅ Build Android App
   - ✅ Build iOS Framework
   - ✅ Code Quality Checks
4. Require pull request reviews (recommended: 1)

### 4. Security

- Never commit secrets to `.env`, `local.properties`, or source files
- Use GitHub Secrets for sensitive data
- Enable secret scanning in repository settings
- Review Dependabot alerts weekly
- Keep dependencies up-to-date

### 5. Release Process

Recommended release workflow:

1. Update version in `build.gradle.kts`:
   ```kotlin
   versionName = "1.2.0"
   versionCode = 12
   ```

2. Update `CHANGELOG.md` with release notes

3. Commit changes:
   ```bash
   git commit -m "chore: bump version to 1.2.0"
   ```

4. Create and push tag:
   ```bash
   git tag v1.2.0
   git push origin main --tags
   ```

5. Release workflow automatically:
   - Builds signed APK and AAB
   - Builds iOS IPA
   - Creates GitHub release (draft)
   - Uploads artifacts

6. Manually:
   - Review draft release
   - Edit release notes
   - Publish release
   - Deploy to stores (if auto-deploy not configured)

## CI/CD Metrics

Monitor these metrics to optimize your pipeline:

- **PR Validation Time**: Target < 10 minutes
- **Main CI Time**: Target < 15 minutes
- **Release Build Time**: Target < 20 minutes
- **Cache Hit Rate**: Target > 80%
- **iOS/Android Cost Ratio**: ~10:1 (macOS vs Ubuntu runners)

## Future Enhancements

Potential improvements for the CI/CD pipeline:

- [ ] Add UI testing with Maestro or Appium
- [ ] Integrate SonarQube for advanced code quality
- [ ] Add performance testing (startup time, memory usage)
- [ ] Implement automatic screenshot generation
- [ ] Add A/B testing deployment tracks
- [ ] Integrate crash reporting (Sentry, Firebase Crashlytics)
- [ ] Add automated release notes generation
- [ ] Implement blue-green deployments

## Resources

### Official Documentation
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Gradle Build Cache](https://docs.gradle.org/current/userguide/build_cache.html)

### Community Resources
- [AKJAW KMP GitHub Actions Examples](https://github.com/AKJAW/kotlin-multiplatform-github-actions)
- [KMPShip CI/CD Guide](https://www.kmpship.app/blog/ci-cd-kotlin-multiplatform-2025)
- [Marco Gomiero's KMP CI Series](https://www.marcogomiero.com/posts/2024/kmp-ci-android/)

### Tools
- [Gradle Build Scan](https://scans.gradle.com/) - Build performance analysis
- [Fastlane](https://fastlane.tools/) - iOS/Android deployment automation
- [Dependabot](https://github.com/dependabot) - Automated dependency updates

## Support

For issues with CI/CD:
1. Check workflow logs in GitHub Actions tab
2. Review this documentation
3. Search [GitHub Actions community forum](https://github.community/c/code-to-cloud/52)
4. Open issue in this repository with workflow logs

---

**Last Updated:** 2025-01-16
**Maintained By:** @po4yka
