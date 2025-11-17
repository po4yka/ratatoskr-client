import WidgetKit
import SwiftUI
import Shared

/// Timeline entry for Recent Summaries widget
struct RecentSummariesEntry: TimelineEntry {
    let date: Date
    let summaries: [Summary]
}

/// Timeline provider for Recent Summaries widget
struct RecentSummariesProvider: TimelineProvider {
    typealias Entry = RecentSummariesEntry

    private let koinHelper: KoinHelper

    init() {
        // Initialize Koin if not already initialized
        // Widget extensions run in a separate process, so Koin needs to be initialized here
        if KoinApplication.shared.koin == nil {
            let koinApp = KoinInitializerKt.initKoin(appDeclaration: { _ in })
            koinHelper = KoinHelper(koin: koinApp.koin)
        } else {
            koinHelper = KoinHelper(koin: KoinApplication.shared.koin)
        }
    }

    /// Placeholder shown while widget is loading
    func placeholder(in context: Context) -> RecentSummariesEntry {
        RecentSummariesEntry(
            date: Date(),
            summaries: createPlaceholderSummaries()
        )
    }

    /// Snapshot for widget gallery
    func getSnapshot(in context: Context, completion: @escaping (RecentSummariesEntry) -> Void) {
        if context.isPreview {
            // Show placeholder in widget gallery
            let entry = RecentSummariesEntry(
                date: Date(),
                summaries: createPlaceholderSummaries()
            )
            completion(entry)
        } else {
            // Fetch real data for snapshot
            fetchSummaries { summaries in
                let entry = RecentSummariesEntry(date: Date(), summaries: summaries)
                completion(entry)
            }
        }
    }

    /// Timeline of entries
    func getTimeline(in context: Context, completion: @escaping (Timeline<RecentSummariesEntry>) -> Void) {
        fetchSummaries { summaries in
            let currentDate = Date()
            let entry = RecentSummariesEntry(date: currentDate, summaries: summaries)

            // Refresh widget every hour
            let nextUpdateDate = Calendar.current.date(byAdding: .hour, value: 1, to: currentDate)!
            let timeline = Timeline(entries: [entry], policy: .after(nextUpdateDate))

            completion(timeline)
        }
    }

    /// Fetch summaries from repository
    private func fetchSummaries(completion: @escaping ([Summary]) -> Void) {
        Task {
            do {
                guard let getSummariesUseCase = koinHelper.koin.get(
                    objCClass: GetSummariesUseCase.self,
                    qualifier: nil,
                    parameters: nil
                ) as? GetSummariesUseCase else {
                    print("[Widget] ERROR: Could not get GetSummariesUseCase from Koin")
                    completion([])
                    return
                }

                // Fetch recent summaries (limit to 5 for widget)
                let summariesFlow = getSummariesUseCase.invoke(
                    limit: 5,
                    offset: 0,
                    filters: SearchFilters()
                )

                // Get first emission from flow
                for try await summaries in summariesFlow {
                    completion(summaries)
                    return
                }

                completion([])
            } catch {
                print("[Widget] Failed to fetch summaries: \(error)")
                completion([])
            }
        }
    }

    /// Create placeholder summaries for widget preview
    private func createPlaceholderSummaries() -> [Summary] {
        let now = Instant.Companion.shared.fromEpochMilliseconds(epochMilliseconds: Int64(Date().timeIntervalSince1970 * 1000))

        return [
            Summary(
                id: 1,
                requestId: 1,
                title: "Sample Article Title",
                url: "https://example.com/article",
                domain: "example.com",
                tldr: "This is a sample TLDR summary that provides a brief overview of the article content.",
                summary250: "Sample summary",
                summary1000: nil,
                keyIdeas: [],
                topicTags: [],
                answeredQuestions: [],
                seoKeywords: [],
                readingTimeMin: 5,
                lang: "en",
                entities: nil,
                keyStats: [],
                readability: nil,
                isRead: false,
                isFavorite: false,
                createdAt: now,
                updatedAt: nil,
                syncStatus: SyncStatus.synced,
                locallyModified: false
            ),
            Summary(
                id: 2,
                requestId: 2,
                title: "Another Interesting Article",
                url: "https://example.com/article2",
                domain: "example.com",
                tldr: "Another sample TLDR providing insights into the article.",
                summary250: "Sample summary 2",
                summary1000: nil,
                keyIdeas: [],
                topicTags: [],
                answeredQuestions: [],
                seoKeywords: [],
                readingTimeMin: 3,
                lang: "en",
                entities: nil,
                keyStats: [],
                readability: nil,
                isRead: false,
                isFavorite: false,
                createdAt: now,
                updatedAt: nil,
                syncStatus: SyncStatus.synced,
                locallyModified: false
            )
        ]
    }
}

/// Widget configuration
@main
struct RecentSummariesWidget: Widget {
    let kind: String = "RecentSummariesWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: RecentSummariesProvider()) { entry in
            RecentSummariesView(entry: entry)
                .containerBackground(.fill.tertiary, for: .widget)
        }
        .configurationDisplayName("Recent Summaries")
        .description("Shows your recent article summaries")
        .supportedFamilies([.systemMedium, .systemLarge])
    }
}

/// Widget preview
#Preview(as: .systemMedium) {
    RecentSummariesWidget()
} timeline: {
    RecentSummariesEntry(
        date: Date(),
        summaries: []
    )
}
