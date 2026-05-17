package com.po4yka.ratatoskr.data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.kCFBooleanFalse
import platform.Foundation.CFBridgingRetain
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
import platform.Security.kSecAttrService
import platform.Security.kSecAttrSynchronizable

/**
 * iOS-backed [SecureStorage] using the system Keychain via
 * `multiplatform-settings`.
 *
 * The default `KeychainSettings(service = ...)` convenience constructor
 * leaves two security-relevant attributes at their library defaults:
 *
 * - `kSecAttrAccessible` defaults to `kSecAttrAccessibleWhenUnlocked`,
 *   which is fine, but `AfterFirstUnlockThisDeviceOnly` is the more
 *   conservative choice — items survive background relaunches yet stay
 *   pinned to this physical device.
 * - `kSecAttrSynchronizable` defaults to `false` at the keychain layer,
 *   BUT iCloud Keychain can still pick items up depending on the user's
 *   account configuration when the attribute is absent. Setting it
 *   explicitly to false makes the device-only intent unambiguous and
 *   guarantees the item is never offered for iCloud sync. (MASVS-STORAGE-1.)
 *
 * Both attributes are passed via the `KeychainSettings` vararg primary
 * constructor.
 */
@OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class, ExperimentalForeignApi::class)
class IosSecureStorage : SecureStorage {
    private val settings =
        KeychainSettings(
            kSecAttrService to (CFBridgingRetain(SERVICE_NAME) as CFTypeRef),
            kSecAttrAccessible to (kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly as CFTypeRef),
            kSecAttrSynchronizable to (kCFBooleanFalse as CFTypeRef),
        ).toSuspendSettings()

    override suspend fun saveAccessToken(token: String) {
        settings.putString(KEY_ACCESS_TOKEN, token)
    }

    override suspend fun getAccessToken(): String? {
        return settings.getStringOrNull(KEY_ACCESS_TOKEN)
    }

    override suspend fun saveRefreshToken(token: String) {
        settings.putString(KEY_REFRESH_TOKEN, token)
    }

    override suspend fun getRefreshToken(): String? {
        return settings.getStringOrNull(KEY_REFRESH_TOKEN)
    }

    override suspend fun clearTokens() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    override suspend fun saveDeveloperCredentials(
        userId: Int,
        clientId: String,
        secret: String,
    ) {
        settings.putInt(KEY_DEV_USER_ID, userId)
        settings.putString(KEY_DEV_CLIENT_ID, clientId)
        settings.putString(KEY_DEV_SECRET, secret)
    }

    override suspend fun getDeveloperCredentials(): DeveloperCredentials? {
        val userId = settings.getIntOrNull(KEY_DEV_USER_ID) ?: return null
        val clientId = settings.getStringOrNull(KEY_DEV_CLIENT_ID) ?: return null
        val secret = settings.getStringOrNull(KEY_DEV_SECRET) ?: return null
        return DeveloperCredentials(userId, clientId, secret)
    }

    override suspend fun clearDeveloperCredentials() {
        settings.remove(KEY_DEV_USER_ID)
        settings.remove(KEY_DEV_CLIENT_ID)
        settings.remove(KEY_DEV_SECRET)
    }

    private companion object {
        const val SERVICE_NAME = "com.po4yka.ratatoskr.auth"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_DEV_USER_ID = "developer_user_id"
        const val KEY_DEV_CLIENT_ID = "developer_client_id"
        const val KEY_DEV_SECRET = "developer_secret"
    }
}
