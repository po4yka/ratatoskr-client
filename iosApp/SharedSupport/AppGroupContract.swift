import Foundation

enum AppGroupContract {
    static let appGroupIdentifier = "group.com.po4yka.ratatoskr"
    static let sharedURLKey = "sharedURL"
    static let sharedURLTimestampKey = "sharedURLTimestamp"
    /// File name under the app group container holding the JSON snapshot.
    /// The Kotlin publisher writes it with
    /// `NSDataWritingFileProtectionCompleteUntilFirstUserAuthentication`
    /// and excludes it from iCloud/iTunes backup, so the file is unreadable
    /// before first unlock and is not persisted in device backups.
    static let recentSummariesSnapshotFileName = "recent-summaries-snapshot.json"
    static let recentSummariesWidgetKind = "RecentSummariesWidget"

    static func summaryDeepLink(summaryId: String) -> URL {
        URL(string: "ratatoskr://summary/\(summaryId)")!
    }

    static func submitURLDeepLink() -> URL {
        URL(string: "ratatoskr://submit-url")!
    }

    static func recentSummariesSnapshotURL() -> URL? {
        FileManager.default
            .containerURL(forSecurityApplicationGroupIdentifier: appGroupIdentifier)?
            .appendingPathComponent(recentSummariesSnapshotFileName)
    }
}

struct RecentSummariesSnapshot: Codable {
    let generatedAtEpochSeconds: Int64
    let summaries: [RecentSummarySnapshotItem]

    static let empty = RecentSummariesSnapshot(generatedAtEpochSeconds: 0, summaries: [])
}

struct RecentSummarySnapshotItem: Codable, Identifiable {
    let id: String
    let title: String
    let excerpt: String
    let domain: String?
    let readingTimeMinutes: Int?

    var deepLink: URL {
        AppGroupContract.summaryDeepLink(summaryId: id)
    }
}

enum AppGroupStore {
    static func sharedDefaults() -> UserDefaults? {
        UserDefaults(suiteName: AppGroupContract.appGroupIdentifier)
    }

    static func storeSharedURL(_ url: URL, date: Date = Date()) {
        guard let defaults = sharedDefaults() else { return }
        defaults.set(url.absoluteString, forKey: AppGroupContract.sharedURLKey)
        defaults.set(date.timeIntervalSince1970, forKey: AppGroupContract.sharedURLTimestampKey)
    }

    static func consumeSharedURL() -> String? {
        guard let defaults = sharedDefaults() else { return nil }
        let sharedURL = defaults.string(forKey: AppGroupContract.sharedURLKey)
        defaults.removeObject(forKey: AppGroupContract.sharedURLKey)
        defaults.removeObject(forKey: AppGroupContract.sharedURLTimestampKey)
        return sharedURL
    }

    static func loadRecentSummariesSnapshot() -> RecentSummariesSnapshot {
        guard
            let url = AppGroupContract.recentSummariesSnapshotURL(),
            let data = try? Data(contentsOf: url, options: [.mappedIfSafe]),
            let snapshot = try? JSONDecoder().decode(RecentSummariesSnapshot.self, from: data)
        else {
            return .empty
        }
        return snapshot
    }
}
