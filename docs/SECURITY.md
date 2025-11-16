# Security Guidelines

Security best practices for the Bite-Size Reader mobile client.

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
- Use Keychain (iOS) or EncryptedSharedPreferences/Keystore (Android)
- Clear tokens on logout
- Validate tokens before use
- Refresh before expiration

### Implementation

**Android (EncryptedSharedPreferences)**:

```kotlin
// androidMain/kotlin/security/SecureTokenStorage.kt
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AndroidSecureTokenStorage(private val context: Context) : SecureTokenStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveAccessToken(token: String) {
        encryptedPrefs.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .apply()
    }

    override fun getAccessToken(): String? {
        return encryptedPrefs.getString(KEY_ACCESS_TOKEN, null)
    }

    override fun saveRefreshToken(token: String) {
        encryptedPrefs.edit()
            .putString(KEY_REFRESH_TOKEN, token)
            .apply()
    }

    override fun getRefreshToken(): String? {
        return encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
    }

    override fun clearTokens() {
        encryptedPrefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
```

**iOS (Keychain)**:

```swift
// iosApp/Security/KeychainTokenStorage.swift
import Security

class KeychainTokenStorage {
    private let service = "com.bitesizereader.tokens"

    func saveAccessToken(_ token: String) {
        save(key: "access_token", value: token)
    }

    func getAccessToken() -> String? {
        return load(key: "access_token")
    }

    func saveRefreshToken(_ token: String) {
        save(key: "refresh_token", value: token)
    }

    func getRefreshToken() -> String? {
        return load(key: "refresh_token")
    }

    func clearTokens() {
        delete(key: "access_token")
        delete(key: "refresh_token")
    }

    private func save(key: String, value: String) {
        let data = value.data(using: .utf8)!

        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key,
            kSecValueData as String: data,
            kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlockedThisDeviceOnly
        ]

        SecItemDelete(query as CFDictionary)  // Delete old value
        SecItemAdd(query as CFDictionary, nil)
    }

    private func load(key: String) -> String? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key,
            kSecReturnData as String: true
        ]

        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)

        guard status == errSecSuccess,
              let data = result as? Data,
              let value = String(data: data, encoding: .utf8) else {
            return nil
        }

        return value
    }

    private func delete(key: String) {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key
        ]

        SecItemDelete(query as CFDictionary)
    }
}
```

### Token Refresh Logic

```kotlin
// data/remote/TokenProvider.kt
class TokenProviderImpl(
    private val secureStorage: SecureTokenStorage,
    private val authApi: AuthApi
) : TokenProvider {

    private val tokenExpiryBuffer = 5.minutes  // Refresh 5 min before expiry

    override suspend fun getTokens(): AuthTokens {
        val accessToken = secureStorage.getAccessToken()
            ?: throw UnauthorizedException("No access token")

        val refreshToken = secureStorage.getRefreshToken()
            ?: throw UnauthorizedException("No refresh token")

        // Check if token needs refresh
        if (isTokenExpiringSoon(accessToken)) {
            return refreshToken(refreshToken)
        }

        return AuthTokens(accessToken, refreshToken)
    }

    override suspend fun refreshToken(): AuthTokens {
        val refreshToken = secureStorage.getRefreshToken()
            ?: throw UnauthorizedException("No refresh token")

        val response = authApi.refreshToken(refreshToken)

        // Save new tokens
        secureStorage.saveAccessToken(response.accessToken)
        response.refreshToken?.let {
            secureStorage.saveRefreshToken(it)
        }

        return AuthTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken ?: refreshToken
        )
    }

    private fun isTokenExpiringSoon(token: String): Boolean {
        try {
            val parts = token.split(".")
            if (parts.size != 3) return true

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val json = Json.parseToJsonElement(payload).jsonObject
            val exp = json["exp"]?.jsonPrimitive?.long ?: return true

            val expiryTime = Instant.fromEpochSeconds(exp)
            val now = Clock.System.now()

            return (expiryTime - now) < tokenExpiryBuffer
        } catch (e: Exception) {
            return true  // Assume expired if can't parse
        }
    }
}
```

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
            "bite_reader.db",
            Context.MODE_PRIVATE,
            null,
            supportFactory.create()
        )

        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "bite_reader.db",
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
        private const val KEY_ALIAS = "bite_reader_db_key"
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
                            "api.bite-size-reader.com",
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
-keep class com.bitesizereader.data.remote.dto.** { *; }
-keep class com.bitesizereader.domain.model.** { *; }

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

- [ ] Tokens stored in Keychain/EncryptedSharedPreferences
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

**Last Updated**: 2025-11-16
