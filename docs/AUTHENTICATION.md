# Authentication Setup Guide

This document explains how to set up and configure Telegram Login Widget authentication for the Ratatoskr application.

## Overview

The application uses the Telegram Login Widget for user authentication, providing a secure OAuth-based login flow. A single Compose screen — [`TelegramAuthScreen`](../feature/auth/src/commonMain/kotlin/com/po4yka/ratatoskr/feature/auth/ui/auth/TelegramAuthScreen.kt) in `feature/auth` — drives the UI on every platform. It hosts a platform-specific `WebView` actual that loads the bot's login widget and intercepts the `ratatoskr://telegram-auth` callback URL.

- **Android** (`WebView.android.kt`): wraps `android.webkit.WebView` with a `WebViewClient` that intercepts `shouldOverrideUrlLoading` and forwards `ratatoskr://` URLs to the Compose layer.
- **iOS** (`WebView.ios.kt`): wraps `WKWebView` with a `WKNavigationDelegateProtocol` that cancels navigation on `ratatoskr://` URLs and forwards them to the Compose layer.
- **Desktop**: not wired (no production desktop target).

## Prerequisites

### 1. Create a Telegram Bot

1. Open Telegram and search for [@BotFather](https://t.me/botfather)
2. Send `/newbot` command
3. Follow the instructions to create your bot:
   - Choose a name for your bot (e.g., "Ratatoskr")
   - Choose a username for your bot (must end with 'bot', e.g., "ratatoskr_client_bot")
4. Save the bot token provided by BotFather (you'll need this for the backend)
5. Note the bot username (you'll need this for the mobile app)

### 2. Configure Bot for Login Widget

1. Send `/setdomain` to @BotFather
2. Select your bot
3. Enter your domain (e.g., `ratatoskr.example.com`)
   - For development, you can use `localhost` or your ngrok domain
4. This allows the Telegram Login Widget to work with your application

### 3. Get Bot ID

You can get the bot ID from the bot token:
- Token format: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`
- Bot ID is the first part: `123456789`

## Configuration

### 1. Set bot credentials in `local.properties`

The bot username, callback URL, and deep-link scheme all live in
[`AppConfig`](../core/common/src/commonMain/kotlin/com/po4yka/ratatoskr/util/config/AppConfig.kt)
under `AppConfig.Telegram`. Defaults ship in the file; production
overrides come from `local.properties` (gitignored):

```properties
telegram.bot.username=ratatoskr_client_bot
telegram.bot.id=123456789
```

These values are wired into Koin via `AppConfig.initializeFromProperties(...)`
during startup, so no source edits are required to swap bots.

The deep-link scheme `ratatoskr://telegram-auth` is hardcoded in
`AppConfig.Telegram.DEEP_LINK_SCHEME` / `DEEP_LINK_HOST` and used by
both platforms.

### 2. Where the auth UI lives

The Compose-based screen at
[`feature/auth/src/commonMain/.../TelegramAuthScreen.kt`](../feature/auth/src/commonMain/kotlin/com/po4yka/ratatoskr/feature/auth/ui/auth/TelegramAuthScreen.kt)
builds the login URL from `AppConfig.Telegram` and renders the platform
WebView actual:

```kotlin
val botUsername = AppConfig.Telegram.botUsername
val origin = AppConfig.Telegram.callbackUrl  // "ratatoskr://telegram-auth"
val loginUrl =
    "${AppConfig.Api.baseUrl}/v1/auth/login-widget?bot=$botUsername&origin=$origin"

WebView(
    url = loginUrl,
    onDeepLink = { url ->
        val authData = parseTelegramAuthData(url)
        if (authData != null) onLogin(authData) else onDismiss()
    },
)
```

The screen parses `id`, `hash`, `first_name`, `last_name`, `username`,
`photo_url`, and `auth_date` query parameters from the redirect URL
and passes them to `LoginViewModel.loginWithTelegram(authData)`.

### 3. Android: WebView actual

[`WebView.android.kt`](../feature/auth/src/androidMain/kotlin/com/po4yka/ratatoskr/feature/auth/ui/auth/WebView.android.kt)
hosts an `android.webkit.WebView` inside an `AndroidView`. The
`WebViewClient.shouldOverrideUrlLoading(...)` callback intercepts any
URL that starts with `ratatoskr://` and forwards it to
`onDeepLink` — the Compose layer handles the rest. JavaScript and DOM
storage are enabled for the Telegram widget to render.

The `ratatoskr` URL scheme is also declared in `AndroidManifest.xml`
for OS-level deep-link interop (widget links, Share Extension hand-off,
etc.):

```xml
<data android:scheme="ratatoskr" android:host="telegram-auth" />
```

### 4. iOS: WebView actual

[`WebView.ios.kt`](../feature/auth/src/iosMain/kotlin/com/po4yka/ratatoskr/feature/auth/ui/auth/WebView.ios.kt)
hosts a `WKWebView` inside `UIKitView`. A `WKNavigationDelegateProtocol`
implementation cancels navigation on `ratatoskr://` URLs (returning
`WKNavigationActionPolicyCancel`) and forwards the URL to `onDeepLink`.
On dispose the delegate is detached and the WebView is stopped to
avoid leaks.

The `ratatoskr` URL scheme is registered in `iosApp/iosApp/Info.plist`
under `CFBundleURLTypes`:

```xml
<key>CFBundleURLSchemes</key>
<array>
    <string>ratatoskr</string>
</array>
<key>CFBundleURLName</key>
<string>com.po4yka.ratatoskr</string>
```

This is set automatically by the regenerated `project.yml` /
`Info.plist`; no Xcode UI configuration is required for fresh
checkouts.

### 5. Manual smoke tests

**Android**:

```bash
./gradlew :androidApp:installDebug
# Tap "Login with Telegram", complete the widget flow.
# Or test the deep-link directly:
adb shell am start -W -a android.intent.action.VIEW \
  -d "ratatoskr://telegram-auth?id=123&hash=abc"
```

**iOS** (simulator):

```bash
xcrun simctl openurl booted "ratatoskr://telegram-auth?id=123&hash=abc"
```

Both should land in `TelegramAuthScreen` and forward the parsed auth
data to the backend.

## Authentication Flow

```
User taps "Login with Telegram"
    ↓
TelegramAuthScreen builds login URL from AppConfig.Telegram + AppConfig.Api
    ↓
Platform WebView (Android: android.webkit.WebView; iOS: WKWebView)
    loads .../v1/auth/login-widget?bot=...&origin=ratatoskr://telegram-auth
    ↓
User authenticates with Telegram inside the widget
    ↓
Telegram redirects to: ratatoskr://telegram-auth?id=...&hash=...
    ↓
WebView actual intercepts the URL → invokes onDeepLink callback
    ↓
TelegramAuthScreen.parseTelegramAuthData(url) extracts auth fields
    ↓
LoginViewModel.loginWithTelegram(authData) called
    ↓
Backend validates auth hash via /v1/auth/telegram-login, returns JWT pair
    ↓
Tokens stored via SecureStorage (Tink+DataStore on Android, Keychain on iOS)
    ↓
User is authenticated
```

## Security Considerations

### 1. Validate Auth Hash

The backend MUST validate the auth hash to ensure the authentication data is legitimate:

```kotlin
// Backend validation (pseudo-code)
fun validateTelegramAuth(authData: TelegramAuthData, botToken: String): Boolean {
    val dataCheckString = buildDataCheckString(authData)
    val secretKey = sha256(botToken)
    val hash = hmacSha256(dataCheckString, secretKey)
    return hash == authData.authHash
}
```

### 2. Check Auth Date

Verify that the authentication is recent (not a replay attack):

```kotlin
fun isAuthRecent(authDate: Long): Boolean {
    val now = System.currentTimeMillis() / 1000
    val maxAge = 86400  // 24 hours
    return (now - authDate) < maxAge
}
```

### 3. Secure Token Storage

Tokens are stored using the platform actuals defined in
`core/data/.../data/local/`:

- **Android**: DataStore Preferences with values encrypted at rest
  by a Tink AEAD primitive (AES-256-GCM, key wrapped by Android
  Keystore master key).
- **iOS**: `KeychainSettings` (multiplatform-settings) with service
  identifier `com.po4yka.ratatoskr.auth`.
- **Desktop**: in-memory `MapSettings` — DEVELOPMENT ONLY.

See [`docs/SECURITY.md`](SECURITY.md#implementation) for the full
implementation matrix.

## Token Refresh Mechanism

The app uses Ktor's built-in Auth plugin for automatic token refresh.

### How It Works

```
1. API request returns 401 Unauthorized
   ↓
2. Ktor Auth plugin triggers refreshTokens block
   ↓
3. POST /v1/auth/refresh with refresh_token
   ↓
4. Check response: success && data != null
   ↓
5a. Success: Save new tokens, retry original request
5b. Failure: Clear all tokens, redirect to login
```

### Implementation Details

```kotlin
// ApiClient.kt - Token refresh setup
install(Auth) {
    bearer {
        loadTokens {
            val accessToken = secureStorage.getAccessToken()
            val refreshToken = secureStorage.getRefreshToken()
            if (accessToken != null && refreshToken != null) {
                BearerTokens(accessToken, refreshToken)
            } else {
                null
            }
        }
        refreshTokens {
            val refreshToken = secureStorage.getRefreshToken()
            if (refreshToken != null) {
                val response = client.post("/v1/auth/refresh") {
                    setBody(mapOf("refresh_token" to refreshToken))
                }
                val parsed: ApiResponseDto<TokenRefreshResponseDto> =
                    Json.decodeFromString(response.bodyAsText())

                if (parsed.success && parsed.data != null) {
                    // Save new tokens
                    secureStorage.saveAccessToken(tokens.accessToken)
                    secureStorage.saveRefreshToken(tokens.refreshToken)
                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                } else {
                    // Explicit failure - clear tokens and force re-login
                    secureStorage.clearTokens()
                    null
                }
            } else {
                null
            }
        }
    }
}
```

### Key Behaviors

| Scenario | Action |
|----------|--------|
| Refresh succeeds | Store new tokens, retry request |
| Refresh fails (success=false) | Clear tokens, redirect to login |
| Refresh throws exception | Log error, return null (no retry) |
| No refresh token available | Return null immediately |

### Error Handling

The refresh mechanism includes explicit status checking:
- Checks `parsed.success == true` before using tokens
- Checks `parsed.data != null` to ensure valid response
- On failure, tokens are cleared to force re-authentication
- HTTP errors are logged with truncated body for debugging

## Troubleshooting

### Android: WebView shows blank page

**Problem**: The Compose sheet appears but the widget doesn't render.

**Solution**:
- Confirm `telegram.bot.username` in `local.properties` matches an existing bot on BotFather (`/setdomain` must include the API base URL host).
- Check Logcat for `chromium` warnings about CORS or mixed content.
- Confirm `AppConfig.Api.baseUrl` is reachable from the device (the widget is served from the backend, not directly from `oauth.telegram.org`).

### Android: Deep link callback never fires

**Problem**: The widget completes but `onDeepLink` is not invoked.

**Solution**:
- Confirm the `ratatoskr` scheme matches `AppConfig.Telegram.DEEP_LINK_SCHEME`.
- Test the deep-link path directly:
  `adb shell am start -W -a android.intent.action.VIEW -d "ratatoskr://telegram-auth?id=123&hash=abc"`.
- The interception happens in `WebView.android.kt`'s `shouldOverrideUrlLoading`. Set a breakpoint there to confirm the URL is being seen.

### iOS: WebView shows blank page

**Problem**: The authentication screen appears but the widget doesn't render.

**Solution**:
- Verify `telegram.bot.username` is set and the host is whitelisted on BotFather.
- Inspect Safari Web Inspector against the WKWebView — load failures usually show in the console.
- `Info.plist` must not enable `NSAllowsArbitraryLoads`. If you self-host the backend over HTTP for development, configure ATS exceptions explicitly.

### iOS: Deep link callback never fires

**Problem**: Telegram redirects but `onDeepLink` is not invoked.

**Solution**:
- The `ratatoskr` URL scheme must be present in `Info.plist` under `CFBundleURLTypes` (auto-generated by `iosApp/project.yml` + xcodegen).
- Test with `xcrun simctl openurl booted "ratatoskr://telegram-auth?id=123&hash=abc"`.
- Check that the navigation delegate set in `WebView.ios.kt` is still attached; `DisposableEffect` clears it on dispose.

### Backend: Invalid auth hash

**Problem**: Backend rejects the authentication data.

**Solution**:
- Ensure you're using the correct bot token for validation
- Verify the hash validation algorithm matches Telegram's specification
- Check that all auth parameters are included in the data check string
- Ensure parameters are sorted alphabetically when building the check string

## Next Steps

1. **Set up your Telegram bot** following the prerequisites.
2. **Set `telegram.bot.username` and `telegram.bot.id` in `local.properties`** — no source edits required.
3. **Configure backend validation** so `/v1/auth/telegram-login` verifies the auth hash with your bot token.
4. **Test the authentication flow** on both platforms (use the simulator/emulator deep-link commands above).
5. **Logout** is already implemented in `LoginViewModel` and clears tokens via `SecureStorage.clearTokens()`.

## Resources

- [Telegram Login Widget Documentation](https://core.telegram.org/widgets/login)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [Android Custom Tabs](https://developer.chrome.com/docs/android/custom-tabs/)
- [iOS WKWebView](https://developer.apple.com/documentation/webkit/wkwebview)
- [Deep Linking on Android](https://developer.android.com/training/app-links/deep-linking)
- [URL Schemes on iOS](https://developer.apple.com/documentation/xcode/defining-a-custom-url-scheme-for-your-app)
