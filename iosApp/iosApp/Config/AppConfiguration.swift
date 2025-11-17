import Foundation

/// Centralized application configuration for iOS
///
/// This configuration matches the Kotlin AppConfig in the shared module.
/// Update these values to match your deployment environment.
enum AppConfiguration {

    /// API Configuration
    enum API {
        /// Base URL for the API server
        /// Update this with your actual API endpoint
        static let baseURL = "https://api.bitesizereader.example.com"

        /// Enable API logging
        static let loggingEnabled = true

        /// Request timeout in seconds
        static let requestTimeout: TimeInterval = 30.0

        /// Connection timeout in seconds
        static let connectionTimeout: TimeInterval = 15.0
    }

    /// Telegram Authentication Configuration
    enum Telegram {
        /// Telegram bot username (without @)
        ///
        /// To set up your bot:
        /// 1. Create a bot via @BotFather on Telegram
        /// 2. Use /setdomain to set your app's domain
        /// 3. Set the username here (without @)
        ///
        /// Example: "bitesizereader_bot"
        static let botUsername = "bitesizereader_bot"

        /// Telegram bot ID (numeric)
        static let botID = ""

        /// Deep link scheme for auth callbacks
        static let deepLinkScheme = "bitesizereader"

        /// Deep link host for Telegram auth
        static let deepLinkHost = "telegram-auth"

        /// Full callback URL for Telegram auth
        static var callbackURL: String {
            "\(deepLinkScheme)://\(deepLinkHost)"
        }
    }

    /// App Configuration
    enum App {
        /// App name
        static let name = "Bite-Size Reader"

        /// App package/bundle identifier
        static let bundleID = "com.po4yka.bitesizereader"

        /// App version
        static let version = "0.1.0"

        /// Support email
        static let supportEmail = "support@bitesizereader.example.com"

        /// Website URL
        static let websiteURL = "https://bitesizereader.example.com"
    }

    /// Feature Flags
    enum Features {
        /// Enable offline reading mode
        static let offlineReadingEnabled = true

        /// Enable reading statistics
        static let statisticsEnabled = true

        /// Enable topic collections
        static let collectionsEnabled = false

        /// Enable export functionality
        static let exportEnabled = false
    }
}
