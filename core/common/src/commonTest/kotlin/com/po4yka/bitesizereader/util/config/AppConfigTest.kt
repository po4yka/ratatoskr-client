package com.po4yka.bitesizereader.util.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AppConfigTest {
    @Test
    fun initializeFromPropertiesOverridesAndroidBuildConfigValues() {
        val originalBaseUrl = AppConfig.Api.baseUrl
        val originalLoggingEnabled = AppConfig.Api.loggingEnabled
        val originalClientId = AppConfig.App.clientId

        try {
            AppConfig.initializeFromProperties(
                mapOf(
                    "api.base.url" to "https://api.example.test",
                    "api.logging.enabled" to false,
                    "client.id" to "android-test-client",
                ),
            )

            assertEquals("https://api.example.test", AppConfig.Api.baseUrl)
            assertFalse(AppConfig.Api.loggingEnabled)
            assertEquals("android-test-client", AppConfig.App.clientId)
        } finally {
            AppConfig.Api.baseUrl = originalBaseUrl
            AppConfig.Api.loggingEnabled = originalLoggingEnabled
            AppConfig.App.clientId = originalClientId
        }
    }
}
