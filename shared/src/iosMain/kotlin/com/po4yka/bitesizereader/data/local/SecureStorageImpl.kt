@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.po4yka.bitesizereader.data.local

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*
import platform.posix.memcpy

/**
 * iOS implementation using Keychain Services
 */
actual class SecureStorageImpl actual constructor() : SecureStorage {
    private val service = "com.po4yka.bitesizereader.tokens"

    actual override fun saveAccessToken(token: String) {
        save(KEY_ACCESS_TOKEN, token)
    }

    actual override fun getAccessToken(): String? {
        return load(KEY_ACCESS_TOKEN)
    }

    actual override fun saveRefreshToken(token: String) {
        save(KEY_REFRESH_TOKEN, token)
    }

    actual override fun getRefreshToken(): String? {
        return load(KEY_REFRESH_TOKEN)
    }

    actual override fun clearTokens() {
        delete(KEY_ACCESS_TOKEN)
        delete(KEY_REFRESH_TOKEN)
    }

    actual override fun saveString(
        key: String,
        value: String,
    ) {
        save(key, value)
    }

    actual override fun getString(key: String): String? {
        return load(key)
    }

    actual override fun remove(key: String) {
        delete(key)
    }

    private fun save(
        key: String,
        value: String,
    ) {
        val data = value.encodeToByteArray().toNSData()

        val query =
            mapOf<Any?, Any?>(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to service,
                kSecAttrAccount to key,
                kSecValueData to data,
                kSecAttrAccessible to kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
            )

        // Delete old value first
        SecItemDelete(query as CFDictionaryRef)

        // Add new value
        SecItemAdd(query as CFDictionaryRef, null)
    }

    private fun load(key: String): String? {
        val query =
            mapOf<Any?, Any?>(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to service,
                kSecAttrAccount to key,
                kSecReturnData to kCFBooleanTrue,
                kSecMatchLimit to kSecMatchLimitOne,
            )

        val result =
            memScoped {
                val resultPtr = alloc<CFTypeRefVar>()
                val status = SecItemCopyMatching(query as CFDictionaryRef, resultPtr.ptr)

                if (status == errSecSuccess) {
                    val data = resultPtr.value as? NSData
                    data?.toByteArray()?.decodeToString()
                } else {
                    null
                }
            }

        return result
    }

    private fun delete(key: String) {
        val query =
            mapOf<Any?, Any?>(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to service,
                kSecAttrAccount to key,
            )

        SecItemDelete(query as CFDictionaryRef)
    }

    private fun ByteArray.toNSData(): NSData {
        return this.usePinned {
            NSData.create(
                bytes = it.addressOf(0),
                length = this.size.toULong(),
            )
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        return ByteArray(length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), bytes, length)
            }
        }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
