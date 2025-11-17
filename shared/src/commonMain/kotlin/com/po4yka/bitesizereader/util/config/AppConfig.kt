package com.po4yka.bitesizereader.util.config

/**
 * Centralized application configuration
 *
 * This object holds all configuration values that can be customized
 * for different environments (development, staging, production).
 *
 * To customize these values:
 * 1. Create a local.properties file in the project root (gitignored)
 * 2. Add your configuration:
 *    api.base.url=https://your-api.com
 *    telegram.bot.username=your_bot
 * 3. Or set via Koin properties during initialization
 */
object AppConfig {
    /**
     * API Configuration
     */
    object Api {
        /**
         * Base URL for the API server
         * Default: example.com (should be replaced in production)
         */
        var baseUrl: String = "https://api.bitesizereader.example.com"

        /**
         * Enable API request/response logging
         * Default: true for debug builds
         */
        var loggingEnabled: Boolean = true

        /**
         * API request timeout in milliseconds
         */
        const val REQUEST_TIMEOUT_MS: Long = 30_000

        /**
         * API connection timeout in milliseconds
         */
        const val CONNECT_TIMEOUT_MS: Long = 15_000
    }

    /**
     * Telegram Authentication Configuration
     */
    object Telegram {
        /**
         * Telegram bot username (without @)
         * This is required for Telegram Login Widget
         *
         * To set up your bot:
         * 1. Create a bot via @BotFather on Telegram
         * 2. Use /setdomain to set your app's domain
         * 3. Set the username here (without @)
         *
         * Example: "bitesizereader_bot"
         */
        var botUsername: String = "bitesizereader_bot"

        /**
         * Telegram bot ID (numeric)
         * This is the unique identifier for your bot
         */
        var botId: String = ""

        /**
         * Deep link scheme for auth callbacks
         */
        const val DEEP_LINK_SCHEME = "bitesizereader"

        /**
         * Deep link host for Telegram auth
         */
        const val DEEP_LINK_HOST = "telegram-auth"

        /**
         * Full callback URL for Telegram auth
         */
        val callbackUrl: String
            get() = "$DEEP_LINK_SCHEME://$DEEP_LINK_HOST"
    }

    /**
     * App Configuration
     */
    object App {
        /**
         * App name for sharing and external integrations
         */
        const val NAME = "Bite-Size Reader"

        /**
         * App package/bundle identifier
         */
        const val PACKAGE_ID = "com.po4yka.bitesizereader"

        /**
         * App version (should match build.gradle.kts)
         */
        const val VERSION = "0.1.0"

        /**
         * Support email
         */
        const val SUPPORT_EMAIL = "support@bitesizereader.example.com"

        /**
         * Website URL
         */
        const val WEBSITE_URL = "https://bitesizereader.example.com"
    }

    /**
     * Feature Flags
     */
    object Features {
        /**
         * Enable offline reading mode
         */
        var offlineReadingEnabled: Boolean = true

        /**
         * Enable reading statistics
         */
        var statisticsEnabled: Boolean = true

        /**
         * Enable topic collections
         */
        var collectionsEnabled: Boolean = false

        /**
         * Enable export functionality
         */
        var exportEnabled: Boolean = false
    }

    /**
     * Initialize configuration from Koin properties
     */
    fun initializeFromProperties(properties: Map<String, Any>) {
        properties["api.base.url"]?.let { Api.baseUrl = it.toString() }
        properties["api.logging.enabled"]?.let { Api.loggingEnabled = it.toString().toBoolean() }
        properties["telegram.bot.username"]?.let { Telegram.botUsername = it.toString() }
        properties["telegram.bot.id"]?.let { Telegram.botId = it.toString() }
    }
}
