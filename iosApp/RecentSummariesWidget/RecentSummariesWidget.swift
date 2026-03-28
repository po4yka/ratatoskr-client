import WidgetKit
import SwiftUI

struct RecentSummariesTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> RecentSummariesEntry {
        RecentSummariesEntry(date: Date(), snapshot: .empty)
    }

    func getSnapshot(in context: Context, completion: @escaping (RecentSummariesEntry) -> Void) {
        completion(loadEntry())
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<RecentSummariesEntry>) -> Void) {
        let entry = loadEntry()
        let refreshDate = Calendar.current.date(byAdding: .minute, value: 30, to: Date()) ?? Date().addingTimeInterval(1800)
        let timeline = Timeline(entries: [entry], policy: .after(refreshDate))
        completion(timeline)
    }

    private func loadEntry() -> RecentSummariesEntry {
        RecentSummariesEntry(
            date: Date(),
            snapshot: AppGroupStore.loadRecentSummariesSnapshot()
        )
    }
}

struct RecentSummariesEntry: TimelineEntry {
    let date: Date
    let snapshot: RecentSummariesSnapshot
}

struct RecentSummariesWidget: Widget {
    static let kind = AppGroupContract.recentSummariesWidgetKind

    let kind: String = RecentSummariesWidget.kind

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: RecentSummariesTimelineProvider()) { entry in
            RecentSummariesView(entry: entry)
        }
        .configurationDisplayName("Recent Summaries")
        .description("View your latest synced summaries.")
        .supportedFamilies([.systemMedium, .systemLarge])
    }
}
