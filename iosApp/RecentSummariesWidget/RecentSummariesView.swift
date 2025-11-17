import SwiftUI
import WidgetKit
import Shared

/// Widget view showing recent summaries
struct RecentSummariesView: View {
    let entry: RecentSummariesEntry

    var body: some View {
        if entry.summaries.isEmpty {
            EmptyStateView()
        } else {
            SummariesListView(summaries: entry.summaries)
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
    let summaries: [Summary]

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
    let summary: Summary

    var body: some View {
        Link(destination: createDeepLink(summaryId: summary.id)) {
            VStack(alignment: .leading, spacing: 6) {
                // Title
                Text(summary.title)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundColor(.primary)
                    .lineLimit(2)

                // TLDR
                Text(summary.tldr)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)

                // Metadata
                HStack(spacing: 8) {
                    // Reading time
                    Label("\(summary.readingTimeMin) min", systemImage: "clock")
                        .font(.caption2)
                        .foregroundColor(.blue)

                    // Domain
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

    /// Create deep link to open summary in app
    private func createDeepLink(summaryId: Int32) -> URL {
        // Using custom URL scheme
        // The app will need to handle this URL scheme in iOSApp.swift
        URL(string: "bitesizereader://summary/\(summaryId)")!
    }
}

/// Preview for widget gallery
#Preview(as: .systemMedium) {
    RecentSummariesWidget()
} timeline: {
    let now = Instant.Companion.shared.fromEpochMilliseconds(epochMilliseconds: Int64(Date().timeIntervalSince1970 * 1000))

    RecentSummariesEntry(
        date: Date(),
        summaries: [
            Summary(
                id: 1,
                requestId: 1,
                title: "Understanding Compose Multiplatform",
                url: "https://example.com/article",
                domain: "example.com",
                tldr: "A comprehensive guide to building cross-platform UIs with Compose.",
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
                title: "iOS WidgetKit Best Practices",
                url: "https://example.com/article2",
                domain: "example.com",
                tldr: "Learn how to create beautiful and performant widgets for iOS.",
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
    )
}
