# Troubleshooting

Common build, runtime, and integration issues encountered while
developing the Ratatoskr client.

For build-system meta-issues (Gradle cache corruption, IDE sync,
CocoaPods deintegration), see also
[`docs/DEVELOPMENT.md#troubleshooting`](DEVELOPMENT.md#troubleshooting).

For auth-flow specific failures (deep links not firing, blank
WebViews), see [`docs/AUTHENTICATION.md#troubleshooting`](AUTHENTICATION.md#troubleshooting).

## Build issues

### iOS build fails with "Framework not found ComposeApp"

The Kotlin/Native framework hasn't been linked yet for the architecture
Xcode is targeting. Force a fresh framework sync, then reinstall pods:

```bash
./gradlew :composeApp:syncFramework \
  -Pkotlin.native.cocoapods.platform=iphonesimulator \
  -Pkotlin.native.cocoapods.archs=arm64 \
  -Pkotlin.native.cocoapods.configuration=Debug
cd iosApp && pod install
```

If that still fails, regenerate the Xcode project from `project.yml`
via `xcodegen generate` (requires `brew install xcodegen`).

### Android build fails with SQLDelight errors

The generated database interfaces are stale or missing. Re-run the
codegen task:

```bash
./gradlew :core:data:generateCommonMainDatabaseInterface --rerun-tasks
```

### `Compose Resources package not found` after rename

The Compose Resources `Res` accessor namespace is derived from
`rootProject.name` lowercased (currently `ratatoskr.core.ui.generated.resources.*`).
If imports look stale, clear the build cache and let Gradle regenerate:

```bash
./gradlew :core:ui:clean :core:ui:generateComposeResClass
```

## Runtime / API issues

### API connection refused

- Confirm the backend is running: `cd ../ratatoskr && docker-compose up`.
- Check `api.base.url` in `local.properties`.
- On the **Android emulator**, `localhost` resolves inside the
  emulator VM. Use `http://10.0.2.2:8000` instead to reach a backend
  running on the host machine.
- iOS simulator can use `http://localhost:8000` directly, but ATS
  blocks plain HTTP in release builds — keep dev URLs in
  `api.base.url` and use the HTTPS production override
  (`api.release.base.url`) for release builds.

### Telegram auth fails

- Verify `telegram.bot.username` and `telegram.bot.id` in
  `local.properties` match a bot registered with BotFather, and that
  `/setdomain` includes the API base URL host.
- Check backend logs for HMAC validation errors — most rejections are
  hash mismatches caused by mismatched bot tokens.
- Telegram requires the auth payload to be processed within a
  ~24-hour window. Stale auth payloads will be rejected; tap "Login
  with Telegram" again to mint a fresh one.

For deeper Android/iOS auth-flow failures (deep link callbacks,
WebView rendering issues), see
[`docs/AUTHENTICATION.md#troubleshooting`](AUTHENTICATION.md#troubleshooting).

## Logging

Verbose logging is opt-in via `local.properties`:

```properties
api.logging.enabled=true
```

This enables Ktor's `Logging` plugin at `INFO` level — request URLs
and response status codes are logged, but bodies are still redacted
unless you raise the level explicitly.

For Android logcat:

```bash
adb logcat | grep ratatoskr
```

For iOS, use Console.app or Xcode's Console pane and filter by the
`com.po4yka.ratatoskr` subsystem (configured in `iOSApp.swift`).

For desktop hot-reload runs, log output goes to stdout. The `kotlin-logging`
facade backed by logback emits structured lines.
