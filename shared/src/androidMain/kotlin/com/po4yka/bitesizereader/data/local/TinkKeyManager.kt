package com.po4yka.bitesizereader.data.local

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.ConfigurationV0
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * Manages Tink AEAD encryption keys using Android Keystore.
 *
 * The keyset is stored in SharedPreferences, encrypted with a master key
 * stored in Android Keystore (hardware-backed on supported devices).
 */
object TinkKeyManager {
    private const val KEYSET_NAME = "secure_storage_keyset"
    private const val PREF_FILE_NAME = "secure_storage_keyset_prefs"
    private const val MASTER_KEY_URI = "android-keystore://secure_storage_master_key"

    @Volatile
    private var aeadInstance: Aead? = null

    /**
     * Returns a thread-safe singleton Aead instance for encryption/decryption.
     *
     * @throws TinkInitializationException if Tink configuration or key creation fails
     */
    @Throws(TinkInitializationException::class)
    fun getAead(context: Context): Aead {
        return aeadInstance ?: synchronized(this) {
            aeadInstance ?: createAead(context).also { aeadInstance = it }
        }
    }

    private fun createAead(context: Context): Aead {
        try {
            TinkConfig.register()
            AeadConfig.register()

            val keysetHandle =
                AndroidKeysetManager.Builder()
                    .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
                    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                    .withMasterKeyUri(MASTER_KEY_URI)
                    .build()
                    .keysetHandle

            return keysetHandle.getPrimitive(ConfigurationV0.get(), Aead::class.java)
        } catch (e: GeneralSecurityException) {
            throw TinkInitializationException("Failed to initialize Tink encryption", e)
        } catch (e: IOException) {
            throw TinkInitializationException("Failed to access keyset storage", e)
        }
    }
}

/**
 * Exception thrown when Tink encryption initialization fails.
 */
class TinkInitializationException(
    message: String,
    cause: Throwable,
) : RuntimeException(message, cause)
