# Code Quality Tools

This project uses multiple code quality tools to maintain high code standards across the Kotlin Multiplatform codebase.

## Tools Overview

### 1. ktlint - Code Style & Formatting

**ktlint** enforces Kotlin coding conventions and formatting rules, customized for Compose Multiplatform.

#### Configuration Files
- `.editorconfig` - ktlint rules and IntelliJ IDEA settings
- `build.gradle.kts` - ktlint plugin configuration

#### Usage

```bash
# Check code style violations
./gradlew ktlintCheck

# Auto-fix violations (where possible)
./gradlew ktlintFormat

# Check specific module
./gradlew :composeApp:ktlintCheck
./gradlew :shared:ktlintCheck
```

#### Customizations for This Project

ktlint is configured with the following customizations for Compose Multiplatform:

- **Function Naming**: Disabled - Compose functions start with uppercase letters
- **Wildcard Imports**: Disabled - Common in Compose code
- **Comment Location**: Flexible - Allows inline comments
- **Max Line Length**: 120 characters
- **Generated Code**: Automatically excluded (`**/build/**`, `**/generated/**`)

### 2. detekt - Static Code Analysis

**detekt** performs static code analysis to detect code smells, complexity issues, and potential bugs.

#### Configuration Files
- `detekt.yml` - detekt rules and thresholds
- `build.gradle.kts` - detekt plugin configuration

#### Usage

```bash
# Run static analysis
./gradlew detekt

# Run with type resolution (Android-specific)
./gradlew :composeApp:detektAndroidDebug
./gradlew :composeApp:detektAndroidRelease

# Create baseline (ignore existing issues)
./gradlew detektBaseline

# View reports
open build/reports/detekt/detekt.html
```

#### Key Rules Enabled

**Complexity**
- Cyclomatic complexity threshold: 15
- Long methods: 60 lines
- Large classes: 600 lines
- Long parameter lists: 6 parameters (7 for constructors)
- Nested block depth: 4 levels

**Coroutines**
- Global coroutine usage detection
- Dispatcher injection checks
- Suspend function validation

**Potential Bugs**
- Null safety checks
- Type casting validation
- Equality checks
- Mutable collection issues

**Performance**
- Array primitive usage
- Spread operator warnings
- Unnecessary instantiation

**Style**
- Return count: max 3 per function
- Magic numbers detection (disabled in tests)
- Throw count: max 2 per function

#### Exclusions

Detekt automatically excludes:
- Generated code (`**/build/**`, `**/generated/**`)
- Test code for certain rules (magic numbers, string duplication)

### 3. Kover - Code Coverage

**Kover** is already configured for Kotlin code coverage analysis.

```bash
# Generate coverage reports
./gradlew koverHtmlReport

# View coverage
open shared/build/reports/kover/html/index.html
```

## CI/CD Integration

Both tools are integrated into the CI/CD pipeline:

### GitHub Actions Workflows

1. **Code Quality Workflow** (`.github/workflows/code-quality.yml`)
   - Runs ktlint check
   - Runs detekt analysis
   - Uploads reports as artifacts

2. **PR Validation** (`.github/workflows/pr-validation.yml`)
   - Enforces ktlint check on pull requests
   - Fails if code quality issues are found

## Pre-commit Hooks (Optional)

You can set up pre-commit hooks to run these checks locally:

```bash
# Create pre-commit hook
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/sh
echo "Running ktlint..."
./gradlew ktlintCheck --daemon

if [ $? -ne 0 ]; then
    echo "ktlint check failed. Please fix the issues or run './gradlew ktlintFormat'"
    exit 1
fi

echo "Running detekt..."
./gradlew detekt --daemon

if [ $? -ne 0 ]; then
    echo "detekt check failed. Please fix the issues."
    exit 1
fi
EOF

# Make it executable
chmod +x .git/hooks/pre-commit
```

## IDE Integration

### IntelliJ IDEA / Android Studio

#### ktlint
1. Install the **ktlint plugin** from Marketplace
2. Go to **Settings → Editor → Code Style → Kotlin**
3. Click **Load** and select `.editorconfig`

#### detekt
1. Install the **detekt plugin** from Marketplace
2. Go to **Settings → Tools → detekt**
3. Point to `detekt.yml` in project root
4. Enable "Enable detekt"

### VS Code

Add to `.vscode/settings.json`:

```json
{
  "kotlin.linting.ktlint.enabled": true,
  "kotlin.linting.detekt.enabled": true,
  "kotlin.linting.detekt.config": "${workspaceFolder}/detekt.yml"
}
```

## Customizing Rules

### Modifying ktlint Rules

Edit `.editorconfig`:

```ini
[*.{kt,kts}]
ktlint_standard_<rule-name> = disabled
```

### Modifying detekt Rules

Edit `detekt.yml` - each rule set has an `active` flag and configurable thresholds.

Example:
```yaml
complexity:
  LongMethod:
    active: true
    threshold: 60  # Change this value
```

## Reports Location

After running checks, reports are available at:

- **ktlint**: `build/reports/ktlint/`
- **detekt**: `build/reports/detekt/`
  - HTML: `detekt.html` (most readable)
  - XML: `detekt.xml` (for CI tools)
  - SARIF: `detekt.sarif` (for GitHub code scanning)
  - TXT: `detekt.txt` (for console output)

## Troubleshooting

### ktlint formatting conflicts with IDE

Run `./gradlew ktlintFormat` and commit the changes. This ensures consistency.

### detekt reports too many issues

1. Create a baseline: `./gradlew detektBaseline`
2. This creates `detekt-baseline.xml` that ignores existing issues
3. Only new code will be checked

### False positives

For specific false positives, use suppression:

```kotlin
@Suppress("RuleName")
fun myFunction() { ... }
```

Or disable the rule in `detekt.yml` or `.editorconfig`.

## Best Practices

1. **Run ktlintFormat before committing** to auto-fix style issues
2. **Address detekt warnings** - they often indicate real issues
3. **Don't suppress rules without good reason** - add comments explaining why
4. **Keep configuration in sync** - `.editorconfig` should match team standards
5. **Review reports in CI** - don't merge PRs with quality issues

## Technology-Specific Configurations

### Compose Multiplatform
- Function naming allows uppercase for `@Composable` functions
- Wildcard imports enabled for Compose packages
- Longer max line length (120) for UI code

### Kotlin Multiplatform
- Platform-specific code excluded from cross-platform checks
- Generated expect/actual declarations excluded

### SQLDelight
- Generated database code excluded from all checks

### Data Classes & DTOs
- Long parameter lists allowed for data classes
- Immutability not enforced on DTOs (mapped from API)

## Additional Resources

- [ktlint documentation](https://pinterest.github.io/ktlint/)
- [detekt documentation](https://detekt.dev/)
- [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose API guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md)
