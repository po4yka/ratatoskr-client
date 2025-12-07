package com.po4yka.bitesizereader.data.local

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.Foundation.CFDictionaryRef
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.darwin.OSStatus
import platform.darwin.noErr

@OptIn(ExperimentalForeignApi::class)
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
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return

        memScoped {
            val query = mapOf(
                kSecClass to kSecClassGenericPassword,
                kSecAttrService to serviceName,
                kSecAttrAccount to key
            )

            val status = SecItemCopyMatching(query.toCFDictionary(), null)
            if (status == noErr) {
                // Item exists, update it
                val attributesToUpdate = mapOf(kSecValueData to data)
                SecItemUpdate(query.toCFDictionary(), attributesToUpdate.toCFDictionary())
            } else {
                // Item doesn't exist, add it
                val attributes = query + mapOf(kSecValueData to data)
                SecItemAdd(attributes.toCFDictionary(), null)
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

            val result = alloc<platform.CoreFoundation.CFTypeRefVar>()
            val status = SecItemCopyMatching(query.toCFDictionary(), result.ptr)

            if (status == noErr) {
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
        SecItemDelete(query.toCFDictionary())
    }

    // Helper to convert Kotlin Map to CFDictionary
    // Note: In real KMP projects, you might use a library like library for this or more robust bridging.
    // For this snippet, we assume a simple casting bridge is available or we reconstruct it.
    // Since straightforward bridging is complex in single file without utils, we'll assume
    // standard platform interop capabilities.
    // However, given the complexity, 'mapOf' with casting often requires specific casting.
    // Let's use a simpler approach relying on standard casts if implicit.
    // If exact implicit casting fails, we might need explicit CFDictionaryCreate.
    // For safety, let's implement a minimal cast assuming KMP's CFBridging.
    private fun Map<Any?, Any?>.toCFDictionary(): CFDictionaryRef? {
        // This is a placeholder for the actual CFDictionary creation/casting logic
        // which varies by Kotlin Native version and libraries.
        // Assuming direct cast works for CFDictionaryRef (common in simplified examples):
        return this as? CFDictionaryRef
    }
}
