import WidgetKit
import SwiftUI
import Shared

struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(date: Date(), summaries: [])
    }

    func getSnapshot(in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        let entry = SimpleEntry(date: Date(), summaries: [
            Summary(id: "1", title: "Snapshot Summary", content: "Content", sourceUrl: "", imageUrl: nil, createdAt: Int64(Date().timeIntervalSince1970), isRead: false, tags: [])
        ])
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        // Fetch actual data from Shared Logic
        // Note: Requires Shared framework to be embedded in Widget Extension
        
        // Mocking data fetch for now
        let summaries = [
            Summary(id: "1", title: "Summary 1", content: "Content", sourceUrl: "", imageUrl: nil, createdAt: Int64(Date().timeIntervalSince1970), isRead: false, tags: []),
            Summary(id: "2", title: "Summary 2", content: "Content", sourceUrl: "", imageUrl: nil, createdAt: Int64(Date().timeIntervalSince1970), isRead: false, tags: [])
        ]
        
        let entry = SimpleEntry(date: Date(), summaries: summaries)
        let timeline = Timeline(entries: [entry], policy: .atEnd)
        completion(timeline)
    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let summaries: [Summary]
}

struct RecentSummariesWidgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        VStack(alignment: .leading) {
            Text("Recent Summaries")
                .font(.headline)
                .padding(.bottom, 4)
            
            ForEach(entry.summaries.prefix(3), id: \.id) { summary in
                Text(summary.title)
                    .font(.caption)
                    .lineLimit(1)
                Divider()
            }
        }
        .padding()
    }
}

@main
struct RecentSummariesWidget: Widget {
    let kind: String = "RecentSummariesWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            RecentSummariesWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("Recent Summaries")
        .description("View your latest summaries.")
    }
}