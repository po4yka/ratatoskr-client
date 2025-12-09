@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.po4yka.bitesizereader.data.local

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFTypeRefVar
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.CoreFoundation.CFDictionaryRef

/**
 * iOS implementation using Keychain Services
 */
class IosSecureStorage : SecureStorage {
    private val serviceName = "com.po4yka.bitesizereader.auth"

    override suspend fun saveAccessToken(token: String) {
        save(key = "access_token", value = token)
    }

    override suspend fun getAccessToken(): String? {
        return load(key = "access_token")
    }

    override suspend fun saveRefreshToken(token: String) {
        save(key = "refresh_token", value = token)
    }

    override suspend fun getRefreshToken(): String? {
        return load(key = "refresh_token")
    }

    override suspend fun clearTokens() {
        delete(key = "access_token")
        delete(key = "refresh_token")
    }

    private fun save(key: String, value: String) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return

        memScoped {
            val query = mapOf(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to serviceName,
                kSecAttrAccount to key
            )

            @Suppress("UNCHECKED_CAST")
            val cfQuery = query as CFDictionaryRef

            val status = SecItemCopyMatching(cfQuery, null)
            if (status == errSecSuccess) {
                // Item exists, update it
                val attributesToUpdate = mapOf(kSecValueData to data)
                @Suppress("UNCHECKED_CAST")
                SecItemUpdate(cfQuery, attributesToUpdate as CFDictionaryRef)
            } else {
                // Item doesn't exist, add it
                val attributes = query + mapOf(kSecValueData to data)
                @Suppress("UNCHECKED_CAST")
                SecItemAdd(attributes as CFDictionaryRef, null)
            }
        }
    }

    private fun load(key: String): String? {
        return memScoped {
            val query = mapOf(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to serviceName,
                kSecAttrAccount to key,
                kSecReturnData to true,
                kSecMatchLimit to kSecMatchLimitOne
            )

            val result = alloc<CFTypeRefVar>()
            @Suppress("UNCHECKED_CAST")
            val status = SecItemCopyMatching(query as CFDictionaryRef, result.ptr)

            if (status == errSecSuccess) {
                val data = result.value as? NSData
                data?.let {
                    NSString.create(data = it, encoding = NSUTF8StringEncoding)?.toString()
                }
            } else {
                null
            }
        }
    }

    private fun delete(key: String) {
        val query = mapOf(
            kSecClass to kSecClassGenericPassword,
            kSecAttrService to serviceName,
            kSecAttrAccount to key
        )
        @Suppress("UNCHECKED_CAST")
        SecItemDelete(query as CFDictionaryRef)
    }
}
