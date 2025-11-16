# GitHub Actions Workflows

This directory contains CI/CD workflows for the Bite-Size Reader KMP project.

## Quick Start

### For Contributors

When you create a pull request:
1. **PR Validation** workflow automatically runs
2. Builds Android and iOS (unless skipped with labels)
3. Runs tests and code quality checks
4. Results appear in PR checks

**Skip expensive iOS builds** for Android-only changes:
```bash
gh pr edit <pr-number> --add-label "skip-ios"
```

### For Maintainers

**Creating a release:**

```bash
# 1. Update version in build.gradle.kts
# 2. Commit and tag
git commit -m "chore: bump version to 1.0.0"
git tag v1.0.0
git push origin main --tags

# 3. Release workflow automatically builds and creates GitHub release
```

## Workflows Overview

| Workflow | Trigger | Purpose | Duration |
|----------|---------|---------|----------|
| **PR Validation** | Pull requests | Validate changes before merge | ~8-10 min |
| **CI - Main** | Push to main/develop | Full build and test | ~12-15 min |
| **Release Build** | Version tags (v*.*.*) | Build production releases | ~18-25 min |
| **Code Quality** | PRs, pushes, weekly | Code quality and security | ~5-7 min |

## Workflow Files

- `pr-validation.yml` - Fast feedback for pull requests
- `ci.yml` - Comprehensive CI for main branches
- `release.yml` - Production release builds
- `code-quality.yml` - Linting, security, and metrics

## Configuration Files

- `../dependabot.yml` - Automated dependency updates

## Documentation

See [docs/CI_CD.md](../docs/CI_CD.md) for comprehensive documentation including:
- Detailed workflow descriptions
- Setup instructions
- Secret configuration
- Troubleshooting guide
- Best practices

## Required Secrets

### Minimal Setup (No Secrets Required)
Workflows will run successfully without any secrets for:
- Building debug APKs
- Running tests
- Code quality checks

### Optional: Release Builds

**Android:**
- `ANDROID_KEYSTORE_RELEASE_B64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

**iOS:**
- `IOS_CERTIFICATE_B64`
- `IOS_CERTIFICATE_PASSWORD`
- `IOS_PROVISIONING_PROFILE_B64`
- `IOS_CODE_SIGN_IDENTITY`

**Caching (Recommended):**
- `GRADLE_CACHE_ENCRYPTION_KEY`

See full secret setup guide in [docs/CI_CD.md](../docs/CI_CD.md#2-configure-secrets).

## Status Badges

Add to README.md:

```markdown
[![PR Validation](https://github.com/po4yka/bite-size-reader-client/actions/workflows/pr-validation.yml/badge.svg)](https://github.com/po4yka/bite-size-reader-client/actions/workflows/pr-validation.yml)
[![CI](https://github.com/po4yka/bite-size-reader-client/actions/workflows/ci.yml/badge.svg)](https://github.com/po4yka/bite-size-reader-client/actions/workflows/ci.yml)
[![Code Quality](https://github.com/po4yka/bite-size-reader-client/actions/workflows/code-quality.yml/badge.svg)](https://github.com/po4yka/bite-size-reader-client/actions/workflows/code-quality.yml)
```

## Cost Optimization

GitHub provides **2,000 free minutes/month** for private repos (unlimited for public).

**Minutes multiplier:**
- Linux (ubuntu-latest): 1x
- macOS (macos-14): **10x**

**Optimization strategies:**
1. Use PR labels to skip iOS builds: `skip-ios`
2. Cache Gradle and Konan dependencies
3. Enable concurrency cancellation
4. Run iOS builds only on main branch (for internal projects)

**Estimated costs per workflow:**
- PR Validation (both platforms): ~15 min Linux + ~5 min macOS = **65 minutes**
- CI Main (both platforms): ~20 min Linux + ~8 min macOS = **100 minutes**
- Code Quality: ~7 min Linux = **7 minutes**

## Support

- **Documentation**: See [docs/CI_CD.md](../docs/CI_CD.md)
- **Issues**: Report CI/CD issues in the main repository
- **GitHub Actions Docs**: https://docs.github.com/en/actions
