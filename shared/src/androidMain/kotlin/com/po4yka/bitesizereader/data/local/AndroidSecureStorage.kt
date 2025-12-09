package com.po4yka.bitesizereader.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidSecureStorage(context: Context) : SecureStorage {
    private val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    private val keyAlias = "bite_size_reader_secure_key"
    private val cipherTransformation = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    override suspend fun saveAccessToken(token: String) {
        saveEncrypted("access_token", token)
    }

    override suspend fun getAccessToken(): String? {
        return loadDecrypted("access_token")
    }

    override suspend fun saveRefreshToken(token: String) {
        saveEncrypted("refresh_token", token)
    }

    override suspend fun getRefreshToken(): String? {
        return loadDecrypted("refresh_token")
    }

    override suspend fun clearTokens() {
        prefs.edit().clear().apply()
    }

    private fun saveEncrypted(
        key: String,
        value: String,
    ) {
        val secretKey = getOrCreateKey()
        val cipher =
            Cipher.getInstance(cipherTransformation).apply {
                init(Cipher.ENCRYPT_MODE, secretKey)
            }
        val iv = cipher.iv
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val payload =
            ByteBuffer.allocate(4 + iv.size + encrypted.size).apply {
                putInt(iv.size)
                put(iv)
                put(encrypted)
            }.array()
        prefs.edit().putString(key, Base64.encodeToString(payload, Base64.NO_WRAP)).apply()
    }

    private fun loadDecrypted(key: String): String? {
        val stored = prefs.getString(key, null) ?: return null
        val payload = runCatching { Base64.decode(stored, Base64.NO_WRAP) }.getOrNull() ?: return null
        val buffer = ByteBuffer.wrap(payload)
        val ivSize = buffer.int
        val iv = ByteArray(ivSize).also { buffer.get(it) }
        val ciphertext = ByteArray(buffer.remaining()).also { buffer.get(it) }

        val secretKey = getOrCreateKey()
        val cipher =
            Cipher.getInstance(cipherTransformation).apply {
                init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
            }
        return runCatching { cipher.doFinal(ciphertext).toString(Charsets.UTF_8) }.getOrNull()
    }

    private fun getOrCreateKey(): SecretKey {
        val existing = keyStore.getKey(keyAlias, null) as? SecretKey
        if (existing != null) return existing

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val parameterSpec =
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build()

        keyGenerator.init(parameterSpec)
        return keyGenerator.generateKey()
    }
}
