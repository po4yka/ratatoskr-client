import Foundation

enum AppGroupContract {
    static let appGroupIdentifier = "group.com.po4yka.bitesizereader"
    static let sharedURLKey = "sharedURL"
    static let sharedURLTimestampKey = "sharedURLTimestamp"
    static let recentSummariesSnapshotKey = "recentSummariesSnapshot"
    static let recentSummariesSnapshotTimestampKey = "recentSummariesSnapshotTimestamp"
    static let recentSummariesWidgetKind = "RecentSummariesWidget"

    static func summaryDeepLink(summaryId: String) -> URL {
        URL(string: "bitesizereader://summary/\(summaryId)")!
    }

    static func submitURLDeepLink() -> URL {
        URL(string: "bitesizereader://submit-url")!
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
            let defaults = sharedDefaults(),
            let snapshotJSON = defaults.string(forKey: AppGroupContract.recentSummariesSnapshotKey),
            let data = snapshotJSON.data(using: .utf8),
            let snapshot = try? JSONDecoder().decode(RecentSummariesSnapshot.self, from: data)
        else {
            return .empty
        }
        return snapshot
    }
}
