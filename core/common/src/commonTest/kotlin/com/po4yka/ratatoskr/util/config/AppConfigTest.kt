package com.po4yka.ratatoskr.util.config

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppConfigTest {
    @AfterTest
    fun resetApiConfig() {
        AppConfig.Api.isReleaseBuild = false
        AppConfig.Api.loggingEnabled = false
    }

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

    @Test
    fun releaseBuildClampsLoggingEnabledToFalseEvenWhenPropertyForcesTrue() {
        AppConfig.Api.isReleaseBuild = true
        AppConfig.initializeFromProperties(mapOf("api.logging.enabled" to true))
        assertFalse(
            AppConfig.Api.loggingEnabled,
            "Release builds must never observe loggingEnabled=true regardless of properties",
        )
    }

    @Test
    fun debugBuildHonoursLoggingEnabledPropertyAsBefore() {
        AppConfig.Api.isReleaseBuild = false
        AppConfig.initializeFromProperties(mapOf("api.logging.enabled" to true))
        assertTrue(
            AppConfig.Api.loggingEnabled,
            "Debug builds must keep current behaviour and honour the property",
        )
    }
}
