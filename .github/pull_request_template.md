## Description

<!-- Provide a clear and concise description of what this PR does -->

## Type of Change

<!-- Mark the relevant option with an "x" -->

- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Refactoring (no functional changes, code improvement)
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Test coverage improvement
- [ ] CI/CD changes

## Related Issues

<!-- Link to related issues using "Fixes #123" or "Closes #456" -->

Fixes #

## Changes Made

<!-- Provide a bullet-point list of the changes -->

-
-
-

## Platforms Tested

<!-- Mark the platforms where you tested these changes -->

- [ ] Android
- [ ] iOS
- [ ] Shared code only (no platform-specific changes)

## Testing

### Test Coverage

- [ ] Unit tests added/updated
- [ ] UI tests added/updated
- [ ] Integration tests added/updated
- [ ] All tests pass locally
- [ ] Code coverage meets minimum threshold (75%)

### Manual Testing

<!-- Describe the testing you performed -->

**Test Scenarios**:
1.
2.
3.

**Devices Tested**:
- Android: <!-- e.g., Pixel 7 (API 34), Samsung Galaxy S21 (API 33) -->
- iOS: <!-- e.g., iPhone 15 Pro (iOS 17.0), iPad Air (iOS 16.5) -->

## Screenshots/Videos

<!-- Add screenshots or videos to demonstrate the changes (especially for UI changes) -->

### Before

<!-- Screenshots/videos of the old behavior -->

### After

<!-- Screenshots/videos of the new behavior -->

## Code Quality Checklist

- [ ] Code follows the project's coding standards
- [ ] ktlint checks pass (`./gradlew ktlintCheck`)
- [ ] No compiler warnings
- [ ] Code is self-documenting or includes comments where necessary
- [ ] Public APIs are documented with KDoc/DocC comments
- [ ] No hardcoded strings (uses string resources)
- [ ] No TODO/FIXME comments left in code
- [ ] Error handling is implemented properly
- [ ] Logging is appropriate (no sensitive data logged)

## Documentation

- [ ] README.md updated (if applicable)
- [ ] CHANGELOG.md updated
- [ ] Code comments added/updated
- [ ] API documentation updated
- [ ] Migration guide provided (for breaking changes)

## Performance

<!-- Answer if applicable -->

- [ ] No performance regressions introduced
- [ ] Performance improvements measured and documented
- [ ] Memory leaks checked and prevented
- [ ] Network requests optimized
- [ ] Database queries optimized

## Accessibility

<!-- For UI changes -->

- [ ] Accessibility labels added (Android: contentDescription, iOS: accessibilityLabel)
- [ ] Proper heading hierarchy
- [ ] Screen reader tested (TalkBack/VoiceOver)
- [ ] Keyboard navigation works (if applicable)
- [ ] Color contrast meets WCAG standards
- [ ] Touch targets are at least 48dp/44pt

## Security

<!-- For changes involving data, authentication, or network -->

- [ ] No sensitive data exposed in logs
- [ ] Input validation implemented
- [ ] API calls use HTTPS
- [ ] Authentication/authorization properly implemented
- [ ] No hardcoded secrets or API keys
- [ ] SQL injection prevented (if using raw queries)
- [ ] XSS prevention implemented (if rendering web content)

## Breaking Changes

<!-- If this PR introduces breaking changes, describe them and provide migration instructions -->

**Breaking Changes**:
-

**Migration Guide**:
```kotlin
// Before
oldCode()

// After
newCode()
```

## Dependencies

<!-- List any new dependencies added -->

- [ ] No new dependencies added
- [ ] New dependencies added (list below with justification):
  - Dependency: `group:artifact:version` - Reason:

## Deployment Notes

<!-- Any special instructions for deployment -->

- [ ] No special deployment steps required
- [ ] Database migrations required
- [ ] Configuration changes required
- [ ] Special deployment notes (describe below):

## Reviewer Notes

<!-- Any additional context or areas you'd like reviewers to focus on -->

## Post-Merge Tasks

<!-- Tasks to be completed after this PR is merged -->

- [ ]
- [ ]

---

## Reviewer Checklist

<!-- For reviewers -->

- [ ] Code follows project standards and best practices
- [ ] Changes are well-tested
- [ ] Documentation is adequate
- [ ] No obvious bugs or issues
- [ ] Performance considerations addressed
- [ ] Security considerations addressed
- [ ] Accessibility requirements met (for UI changes)
