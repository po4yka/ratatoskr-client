# Security Guidelines

Security best practices for the Ratatoskr mobile client.

## Table of Contents

1. [Overview](#overview)
2. [Token Management](#token-management)
3. [Secure Storage](#secure-storage)
4. [Network Security](#network-security)
5. [Data Protection](#data-protection)
6. [Input Validation](#input-validation)
7. [Platform-Specific Security](#platform-specific-security)
8. [Security Checklist](#security-checklist)

---

## Overview

### Security Principles

1. **Defense in Depth**: Multiple layers of security
2. **Least Privilege**: Minimal permissions required
3. **Secure by Default**: Security enabled out of the box
4. **No Secrets in Code**: All secrets from configuration
5. **Encrypted at Rest**: Local data encrypted
6. **Encrypted in Transit**: HTTPS only

### Threat Model

| Threat | Mitigation |
|--------|------------|
| Token theft | Secure storage (Keychain/Keystore) |
| Network interception | TLS 1.2+, certificate pinning |
| Local data access | Encryption at rest, app sandboxing |
| Unauthorized API access | JWT validation, user whitelist |
| Replay attacks | Token expiration, correlation IDs |
| Man-in-the-middle | Certificate validation, pinning |

---

## Token Management

### JWT Token Lifecycle

```
Login → Receive Tokens → Store Securely → Use in Requests → Refresh Before Expiry → Logout & Clear
```

### Token Types

**Access Token**:
- **Lifetime**: 60 minutes
- **Purpose**: API authentication
- **Storage**: Secure encrypted storage
- **Usage**: `Authorization: Bearer <token>` header

**Refresh Token**:
- **Lifetime**: 30 days
- **Purpose**: Obtain new access tokens
- **Storage**: Secure encrypted storage
- **Usage**: Refresh endpoint only

### Token Storage Requirements

**NEVER**:
- Store in SharedPreferences (Android) or UserDefaults (iOS) without encryption
- Store in plain text files
- Log tokens to console in production
- Include in crash reports
- Store in source code or build config

**ALWAYS**:
- Use Keychain (iOS) or Tink AEAD + DataStore (Android)
- Clear tokens on logout
- Validate tokens before use
- Refresh before expiration

### Implementation

The `SecureStorage` interface in
[`core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/data/local/SecureStorage.kt`](../core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/data/local/SecureStorage.kt)
defines the cross-platform contract. Each platform provides its own actual:

| Platform | File | Backing store |
|----------|------|---------------|
| Android | [`AndroidSecureStorage.kt`](../core/data/src/androidMain/kotlin/com/po4yka/ratatoskr/data/local/AndroidSecureStorage.kt) | DataStore Preferences with values encrypted at rest using a Tink AEAD primitive ([`TinkKeyManager.kt`](../core/data/src/androidMain/kotlin/com/po4yka/ratatoskr/data/local/TinkKeyManager.kt) — AES-256-GCM, key wrapped by Android Keystore master key, hardware-backed on supported devices). |
| iOS | [`IosSecureStorage.kt`](../core/data/src/iosMain/kotlin/com/po4yka/ratatoskr/data/local/IosSecureStorage.kt) | `KeychainSettings` (multiplatform-settings) with service identifier `com.po4yka.ratatoskr.auth`. Tokens never leave the Keychain in plaintext. |
| Desktop | [`DesktopSecureStorage.kt`](../core/data/src/desktopMain/kotlin/com/po4yka/ratatoskr/data/local/DesktopSecureStorage.kt) | **In-memory `MapSettings` — DEVELOPMENT ONLY.** Not persisted, not encrypted. The desktop target exists for Compose hot-reload work, not production use. |

**Why Tink AEAD + DataStore on Android instead of `EncryptedSharedPreferences`?**
Google deprecated `androidx.security:security-crypto` in 2024. Tink is
Google's actively maintained crypto library; pairing it with DataStore
gives an asynchronous, coroutine-friendly API plus hardware-backed
key wrapping via `android-keystore://` URIs.

**What gets stored**: access token, refresh token, and developer
credentials (`userId` / `clientId` / `secret` for the dev secret-login
flow). All values encrypt-then-encode (Base64 NO_WRAP) before landing
in DataStore.

### Token Refresh Logic

Refresh is wired through Ktor's `Auth` plugin in
[`core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/data/remote/ApiClient.kt`](../core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/data/remote/ApiClient.kt).
On a 401 response Ktor invokes the `refreshTokens` block, which
`POST`s to `/v1/auth/refresh`, persists the new bearer pair to
`SecureStorage`, and retries the original call. If refresh itself
fails (`success=false` or HTTP error) the storage is wiped to force
re-authentication. See
[`docs/AUTHENTICATION.md`](AUTHENTICATION.md#token-refresh-mechanism)
for the full sequence and the Kotlin snippet.

---

## Secure Storage

### Database Encryption

Encrypt the local SQLite database to protect summaries at rest.

**Android (SQLCipher)**:

```kotlin
// androidMain/kotlin/data/local/DatabaseDriverFactory.kt
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        // Generate encryption key from Android Keystore
        val encryptionKey = getOrCreateEncryptionKey()

        val supportFactory = SupportFactory(
            SQLiteDatabase.getBytes(encryptionKey.toCharArray())
        )

        val database = context.openOrCreateDatabase(
            "ratatoskr.db",
            Context.MODE_PRIVATE,
            null,
            supportFactory.create()
        )

        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "ratatoskr.db",
            factory = supportFactory
        )
    }

    private fun getOrCreateEncryptionKey(): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )

            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build()
            )

            keyGenerator.generateKey()
        }

        // Return key as Base64 string
        // (Implementation depends on key extraction method)
        return "..." // Actual key derivation
    }

    companion object {
        private const val KEY_ALIAS = "ratatoskr_db_key"
    }
}
```

**iOS (Encrypted Core Data or custom encryption)**:

```swift
// iosApp/Data/SecureDatabaseDriver.swift
class SecureDatabaseDriver {
    private let encryptionKey: String

    init() {
        self.encryptionKey = getOrCreateEncryptionKey()
    }

    private func getOrCreateEncryptionKey() -> String {
        let keychain = KeychainTokenStorage()

        if let existingKey = keychain.load(key: "db_encryption_key") {
            return existingKey
        }

        // Generate new 256-bit key
        var bytes = [UInt8](repeating: 0, count: 32)
        _ = SecRandomCopyBytes(kSecRandomDefault, bytes.count, &bytes)
        let key = bytes.map { String(format: "%02x", $0) }.joined()

        keychain.save(key: "db_encryption_key", value: key)
        return key
    }
}
```

---

## Network Security

### HTTPS Only

**Enforce HTTPS in production**:

```kotlin
// data/remote/ApiClient.kt
val httpClient = HttpClient(engine) {
    install(HttpsRedirect) {
        // Only allow HTTPS in production
        if (BuildConfig.BUILD_TYPE == "release") {
            allowHttps = true
            allowHttp = false
        }
    }
}
```

### Certificate Pinning

Pin backend server certificates to prevent MITM attacks:

**Android**:

```kotlin
// androidMain/kotlin/network/CertificatePinning.kt
val httpClient = HttpClient(OkHttp) {
    engine {
        config {
            // Pin certificate for production API
            if (BuildConfig.BUILD_TYPE == "release") {
                certificatePinner(
                    CertificatePinner.Builder()
                        .add(
                            "api.ratatoskr.po4yka.com",
                            "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
                        )
                        .build()
                )
            }
        }
    }
}
```

**iOS**:

```swift
// iosApp/Network/CertificatePinning.swift
class PinningDelegate: NSObject, URLSessionDelegate {
    let pinnedCertificates: Set<Data>

    init(certificates: [Data]) {
        self.pinnedCertificates = Set(certificates)
    }

    func urlSession(
        _ session: URLSession,
        didReceive challenge: URLAuthenticationChallenge,
        completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void
    ) {
        guard challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust,
              let serverTrust = challenge.protectionSpace.serverTrust else {
            completionHandler(.cancelAuthenticationChallenge, nil)
            return
        }

        // Validate against pinned certificates
        if validateServerTrust(serverTrust) {
            completionHandler(.useCredential, URLCredential(trust: serverTrust))
        } else {
            completionHandler(.cancelAuthenticationChallenge, nil)
        }
    }

    private func validateServerTrust(_ serverTrust: SecTrust) -> Bool {
        // Certificate pinning logic
        // Compare server cert against pinned certs
        return true  // Implement actual validation
    }
}
```

### TLS Configuration

```kotlin
// Require TLS 1.2 or higher
val httpClient = HttpClient(OkHttp) {
    engine {
        config {
            connectionSpecs(
                listOf(
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
                        .build()
                )
            )
        }
    }
}
```

---

## Data Protection

### Sensitive Data Handling

**Never log sensitive data**:

```kotlin
// Bad
Logger.d("Access token: $accessToken")

// Good
Logger.d("Access token: [REDACTED]")
```

**Redact sensitive fields**:

```kotlin
data class User(
    val id: Long,
    val username: String
) {
    override fun toString(): String {
        return "User(id=$id, username=[REDACTED])"
    }
}
```

### Screen Security

**Prevent screenshots of sensitive screens** (Android):

```kotlin
// In Activity
window.setFlags(
    WindowManager.LayoutParams.FLAG_SECURE,
    WindowManager.LayoutParams.FLAG_SECURE
)
```

**Blur app in task switcher**:

```kotlin
// Application class
registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {
        // Blur content when app goes to background
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
})
```

### Memory Security

**Clear sensitive data from memory**:

```kotlin
fun clearSensitiveData() {
    val tokenBytes = token.toByteArray()
    tokenBytes.fill(0)  // Overwrite memory
}
```

---

## Input Validation

### URL Validation

Validate URLs before submission:

```kotlin
// domain/util/UrlValidator.kt
object UrlValidator {
    private val urlPattern = Regex(
        "^https?://[a-zA-Z0-9.-]+(:[0-9]+)?(/.*)?$"
    )

    fun isValid(url: String): Boolean {
        // Basic format check
        if (!urlPattern.matches(url)) {
            return false
        }

        // Prevent local/private IPs in production
        if (BuildConfig.BUILD_TYPE == "release") {
            val host = Url(url).host
            if (isPrivateIP(host)) {
                return false
            }
        }

        return true
    }

    private fun isPrivateIP(host: String): Boolean {
        return host == "localhost" ||
               host.startsWith("127.") ||
               host.startsWith("10.") ||
               host.startsWith("192.168.") ||
               host.startsWith("172.16.")
    }
}
```

### SQL Injection Prevention

Use parameterized queries with SQLDelight:

```sql
-- Good (SQLDelight handles escaping)
SELECT * FROM Summary WHERE id = ?;

-- Bad (Never build SQL strings manually)
-- "SELECT * FROM Summary WHERE id = $id"
```

---

## Platform-Specific Security

### Android

**ProGuard/R8 Obfuscation**:

```proguard
# proguard-rules.pro
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep models for serialization
-keep class com.po4yka.ratatoskr.data.remote.dto.** { *; }
-keep class com.po4yka.ratatoskr.domain.model.** { *; }

# Obfuscate everything else
-repackageclasses 'o'
```

**Root Detection**:

```kotlin
fun isDeviceRooted(): Boolean {
    // Check for su binary
    val suLocations = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su"
    )

    return suLocations.any { File(it).exists() }
}

// Warn user if device is rooted
if (isDeviceRooted()) {
    showWarning("Device is rooted. App security may be compromised.")
}
```

### iOS

**Jailbreak Detection**:

```swift
func isJailbroken() -> Bool {
    #if targetEnvironment(simulator)
    return false
    #else

    let jailbreakPaths = [
        "/Applications/Cydia.app",
        "/Library/MobileSubstrate/MobileSubstrate.dylib",
        "/bin/bash",
        "/usr/sbin/sshd",
        "/etc/apt",
        "/private/var/lib/apt/"
    ]

    for path in jailbreakPaths {
        if FileManager.default.fileExists(atPath: path) {
            return true
        }
    }

    return false
    #endif
}
```

**App Transport Security**:

```xml
<!-- Info.plist -->
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <false/>
    <key>NSAllowsLocalNetworking</key>
    <true/>  <!-- Only for development -->
</dict>
```

---

## Security Checklist

### Development

- [ ] No secrets in source code
- [ ] No API keys in git history
- [ ] Debug logging disabled in release builds
- [ ] ProGuard/R8 enabled for Android
- [ ] Code signing configured for both platforms
- [ ] `.gitignore` includes `local.properties`

### Authentication

- [ ] Tokens stored in Keychain (iOS) / Tink AEAD + DataStore (Android)
- [ ] Token refresh implemented
- [ ] Logout clears all tokens
- [ ] Tokens never logged
- [ ] Client ID configured

### Network

- [ ] HTTPS enforced in production
- [ ] Certificate pinning configured
- [ ] TLS 1.2+ required
- [ ] Timeouts configured
- [ ] Network security config (Android)
- [ ] ATS configured (iOS)

### Data

- [ ] Database encrypted at rest
- [ ] Sensitive fields redacted in logs
- [ ] Screen security enabled
- [ ] No sensitive data in crash reports
- [ ] Memory cleared after use

### Input

- [ ] URL validation implemented
- [ ] SQL injection prevented (parameterized queries)
- [ ] User input sanitized

### Platform

- [ ] Root/jailbreak detection
- [ ] App obfuscation enabled
- [ ] Minimum OS version enforced
- [ ] Permissions minimized

### Testing

- [ ] Security tests in CI/CD
- [ ] Dependency vulnerability scanning
- [ ] OWASP Mobile Top 10 reviewed
- [ ] Penetration testing performed

---

**Last Updated**: 2026-04-28
