# Authentication Setup Guide

This document explains how to set up and configure Telegram Login Widget authentication for the Bite-Size Reader application.

## Overview

The application uses Telegram Login Widget for user authentication, providing a secure OAuth-based login flow. The implementation is platform-specific:

- **Android**: Custom Tabs with deep link callbacks
- **iOS**: WKWebView with JavaScript message handlers

## Prerequisites

### 1. Create a Telegram Bot

1. Open Telegram and search for [@BotFather](https://t.me/botfather)
2. Send `/newbot` command
3. Follow the instructions to create your bot:
   - Choose a name for your bot (e.g., "Bite-Size Reader")
   - Choose a username for your bot (must end with 'bot', e.g., "bitesizereader_bot")
4. Save the bot token provided by BotFather (you'll need this for the backend)
5. Note the bot username (you'll need this for the mobile app)

### 2. Configure Bot for Login Widget

1. Send `/setdomain` to @BotFather
2. Select your bot
3. Enter your domain (e.g., `bitesizereader.com`)
   - For development, you can use `localhost` or your ngrok domain
4. This allows the Telegram Login Widget to work with your application

### 3. Get Bot ID

You can get the bot ID from the bot token:
- Token format: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`
- Bot ID is the first part: `123456789`

## Android Configuration

### 1. Update TelegramAuthHelper

Edit `composeApp/src/androidMain/kotlin/com/po4yka/bitesizereader/auth/TelegramAuthHelper.kt`:

```kotlin
object TelegramAuthHelper {
    // Replace with your actual Telegram bot username
    private const val TELEGRAM_BOT_USERNAME = "your_bot_username"  // e.g., "bitesizereader_bot"

    private const val CALLBACK_URL = "bitesizereader://telegram-auth"

    private fun buildTelegramAuthUrl(): String {
        return buildString {
            append("https://oauth.telegram.org/auth")
            append("?bot_id=123456789")  // Replace with your bot ID
            append("&origin=android")
            append("&embed=1")
            append("&request_access=write")
            append("&return_to=")
            append(Uri.encode(CALLBACK_URL))
        }
    }
}
```

### 2. Deep Link Configuration

The deep link scheme is already configured in `AndroidManifest.xml`:

```xml
<data android:scheme="bitesizereader" android:host="telegram-auth" />
```

This creates the callback URL: `bitesizereader://telegram-auth`

### 3. Test on Android

1. Build and run the app on an Android device or emulator
2. Tap "Login with Telegram"
3. A Custom Tab will open with the Telegram Login Widget
4. Authenticate with Telegram
5. You'll be redirected back to the app via the deep link
6. The app should automatically log you in

## iOS Configuration

### 1. Update TelegramAuthHelper

Edit `iosApp/iosApp/Auth/TelegramAuthWebView.swift`:

```swift
struct TelegramAuthHelper {
    // Replace with your actual Telegram bot username
    private static let botUsername = "your_bot_username"  // e.g., "bitesizereader_bot"
}
```

### 2. Configure URL Scheme

The URL scheme needs to be configured in Xcode:

1. Open the iOS project in Xcode
2. Select the `iosApp` target
3. Go to the "Info" tab
4. Expand "URL Types"
5. Add a new URL Type:
   - Identifier: `com.po4yka.bitesizereader`
   - URL Schemes: `bitesizereader`
   - Role: `Editor`

### 3. Test on iOS

1. Build and run the app on an iOS device or simulator
2. Tap "Login with Telegram"
3. A sheet will appear with the Telegram Login Widget in a WKWebView
4. Authenticate with Telegram
5. The authentication data will be passed via JavaScript message handler
6. The app should automatically log you in

## Authentication Flow

### Android Flow

```
User taps "Login with Telegram"
    ↓
TelegramAuthHelper.launchTelegramAuth()
    ↓
Custom Tab opens with Telegram OAuth URL
    ↓
User authenticates with Telegram
    ↓
Telegram redirects to: bitesizereader://telegram-auth?id=...&hash=...
    ↓
TelegramAuthActivity receives deep link
    ↓
Parses auth parameters and calls LoginViewModel.loginWithTelegram()
    ↓
Backend validates auth data and returns JWT token
    ↓
Token stored in SecureStorage
    ↓
User is authenticated
```

### iOS Flow

```
User taps "Login with Telegram"
    ↓
AuthView shows sheet with TelegramAuthWebView
    ↓
WKWebView loads HTML with Telegram Login Widget
    ↓
User authenticates with Telegram
    ↓
Telegram widget calls onTelegramAuth(user) JavaScript function
    ↓
JavaScript posts message to native code via WKScriptMessageHandler
    ↓
TelegramAuthWebView.Coordinator receives message
    ↓
Parses auth data and calls onAuthSuccess callback
    ↓
LoginViewModelWrapper.loginWithTelegram() is called
    ↓
Backend validates auth data and returns JWT token
    ↓
Token stored in SecureStorage
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

Tokens are stored using:
- **Android**: EncryptedSharedPreferences
- **iOS**: Keychain

Both provide hardware-backed encryption when available.

## Troubleshooting

### Android: Custom Tab doesn't open

**Problem**: Clicking "Login with Telegram" does nothing.

**Solution**:
- Ensure Chrome or another Custom Tabs compatible browser is installed
- Check Logcat for exceptions
- Verify the bot ID and origin in the auth URL

### Android: Deep link not working

**Problem**: After authentication, nothing happens.

**Solution**:
- Verify the deep link configuration in AndroidManifest.xml
- Check that the scheme and host match in both TelegramAuthHelper and AndroidManifest
- Use `adb shell am start -W -a android.intent.action.VIEW -d "bitesizereader://telegram-auth?id=123&hash=abc"` to test deep link handling

### iOS: WebView shows blank page

**Problem**: The authentication sheet appears but shows a blank page.

**Solution**:
- Check the bot username is correct in TelegramAuthHelper
- Verify the HTML is loading correctly (check WKWebView console logs)
- Ensure network requests are allowed (check Info.plist for NSAppTransportSecurity)

### iOS: Authentication data not received

**Problem**: User authenticates but nothing happens.

**Solution**:
- Check that the WKScriptMessageHandler is properly registered
- Verify the JavaScript message handler name matches ("telegramAuthHandler")
- Check the data parsing in TelegramAuthData.from(dict:)

### Backend: Invalid auth hash

**Problem**: Backend rejects the authentication data.

**Solution**:
- Ensure you're using the correct bot token for validation
- Verify the hash validation algorithm matches Telegram's specification
- Check that all auth parameters are included in the data check string
- Ensure parameters are sorted alphabetically when building the check string

## Environment Variables

For production deployment, use environment variables or a configuration file:

### Android (local.properties or BuildConfig)

```properties
TELEGRAM_BOT_USERNAME=bitesizereader_bot
TELEGRAM_BOT_ID=123456789
```

### iOS (Configuration file or Info.plist)

```swift
enum Config {
    static let telegramBotUsername = ProcessInfo.processInfo.environment["TELEGRAM_BOT_USERNAME"] ?? "bitesizereader_bot"
}
```

## Next Steps

1. **Set up your Telegram bot** following the prerequisites
2. **Update bot credentials** in both Android and iOS code
3. **Configure backend validation** to verify auth hash
4. **Test the authentication flow** on both platforms
5. **Set up proper error handling** for network failures
6. **Implement token refresh** logic if needed
7. **Add logout functionality** (already implemented in LoginViewModel)

## Resources

- [Telegram Login Widget Documentation](https://core.telegram.org/widgets/login)
- [Telegram Bot API](https://core.telegram.org/bots/api)
- [Android Custom Tabs](https://developer.chrome.com/docs/android/custom-tabs/)
- [iOS WKWebView](https://developer.apple.com/documentation/webkit/wkwebview)
- [Deep Linking on Android](https://developer.android.com/training/app-links/deep-linking)
- [URL Schemes on iOS](https://developer.apple.com/documentation/xcode/defining-a-custom-url-scheme-for-your-app)
