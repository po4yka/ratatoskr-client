import SwiftUI
import WidgetKit

/// Widget view showing recent summaries
struct RecentSummariesView: View {
    let entry: RecentSummariesEntry

    var body: some View {
        if entry.snapshot.summaries.isEmpty {
            EmptyStateView()
        } else {
            SummariesListView(summaries: entry.snapshot.summaries)
        }
    }
}

/// Empty state when no summaries are available
private struct EmptyStateView: View {
    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "doc.text.magnifyingglass")
                .font(.system(size: 36))
                .foregroundColor(.secondary)

            Text("No summaries yet")
                .font(.headline)
                .foregroundColor(.primary)

            Text("Submit a URL to get started")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

/// List of summaries
private struct SummariesListView: View {
    let summaries: [RecentSummarySnapshotItem]

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Header
            HStack {
                Image(systemName: "book.fill")
                    .foregroundColor(.blue)
                Text("Recent Summaries")
                    .font(.headline)
                    .fontWeight(.bold)
                Spacer()
            }
            .padding(.bottom, 4)

            // Summaries
            ForEach(summaries, id: \.id) { summary in
                SummaryItemView(summary: summary)
            }

            Spacer()
        }
        .padding()
    }
}

/// Individual summary item
private struct SummaryItemView: View {
    let summary: RecentSummarySnapshotItem

    var body: some View {
        Link(destination: summary.deepLink) {
            VStack(alignment: .leading, spacing: 6) {
                Text(summary.title)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundColor(.primary)
                    .lineLimit(2)

                Text(summary.excerpt)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)

                HStack(spacing: 8) {
                    if let readingTimeMinutes = summary.readingTimeMinutes {
                        Label("\(readingTimeMinutes) min", systemImage: "clock")
                            .font(.caption2)
                            .foregroundColor(.blue)
                    }

                    if let domain = summary.domain {
                        Label(domain, systemImage: "globe")
                            .font(.caption2)
                            .foregroundColor(.secondary)
                            .lineLimit(1)
                    }

                    Spacer()
                }
            }
            .padding(10)
            .background(Color(.systemBackground))
            .cornerRadius(8)
        }
    }
}

/// Preview for widget gallery
@available(iOS 17.0, *)
#Preview(as: .systemMedium) {
    RecentSummariesWidget()
} timeline: {
    RecentSummariesEntry(
        date: Date(),
        snapshot: RecentSummariesSnapshot(
            generatedAtEpochSeconds: 0,
            summaries: [
                RecentSummarySnapshotItem(
                    id: "1",
                    title: "Understanding Compose Multiplatform",
                    excerpt: "A comprehensive guide to building cross-platform UIs with Compose.",
                    domain: "example.com",
                    readingTimeMinutes: 5
                ),
                RecentSummarySnapshotItem(
                    id: "2",
                    title: "WidgetKit Best Practices",
                    excerpt: "Learn how to build lightweight widgets backed by an app-group snapshot.",
                    domain: "example.com",
                    readingTimeMinutes: 3
                ),
            ]
        )
    )
}
