import SwiftUI
import WidgetKit

// Frost design system color constants for widget surfaces.
// INK flips between light and dark; PAGE inverts; SPARK never changes.
private extension Color {
    static let frostInk = Color(light: Color(red: 0.11, green: 0.14, blue: 0.17),
                                dark: Color(red: 0.91, green: 0.93, blue: 0.94))
    static let frostPage = Color(light: Color(red: 0.94, green: 0.95, blue: 0.96),
                                 dark: Color(red: 0.07, green: 0.09, blue: 0.11))
    static let frostInkMuted = Color(light: Color(red: 0.29, green: 0.33, blue: 0.41),
                                     dark: Color(red: 0.63, green: 0.68, blue: 0.75))

    init(light: Color, dark: Color) {
        self.init(uiColor: UIColor { traits in
            traits.userInterfaceStyle == .dark
                ? UIColor(dark)
                : UIColor(light)
        })
    }
}

/// Widget view showing recent summaries
struct RecentSummariesView: View {
    let entry: RecentSummariesEntry
    @Environment(\.colorScheme) private var colorScheme

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
        ZStack {
            Color.frostPage
            VStack(spacing: 8) {
                Text("NO SUMMARIES YET")
                    .font(.system(.caption, design: .monospaced))
                    .fontWeight(.bold)
                    .foregroundColor(.frostInk)

                Text("Submit a URL to get started")
                    .font(.system(.caption2, design: .monospaced))
                    .foregroundColor(.frostInkMuted)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

/// List of summaries
private struct SummariesListView: View {
    let summaries: [RecentSummarySnapshotItem]

    var body: some View {
        ZStack {
            Color.frostPage
            VStack(alignment: .leading, spacing: 0) {
                // Header
                HStack {
                    Text("RECENT SUMMARIES")
                        .font(.system(.caption2, design: .monospaced))
                        .fontWeight(.bold)
                        .foregroundColor(.frostInk)
                    Spacer()
                }
                .padding(.horizontal, 12)
                .padding(.top, 10)
                .padding(.bottom, 6)

                // Hairline border under header
                Color.frostInk
                    .frame(height: 1)

                // Summaries
                ForEach(summaries, id: \.id) { summary in
                    SummaryItemView(summary: summary)
                    Color.frostInk
                        .frame(height: 1)
                }

                Spacer(minLength: 0)
            }
        }
    }
}

/// Individual summary item
private struct SummaryItemView: View {
    let summary: RecentSummarySnapshotItem

    var body: some View {
        Link(destination: summary.deepLink) {
            VStack(alignment: .leading, spacing: 4) {
                Text(summary.title)
                    .font(.system(.caption, design: .monospaced))
                    .fontWeight(.bold)
                    .foregroundColor(.frostInk)
                    .lineLimit(2)

                Text(summary.excerpt)
                    .font(.system(.caption2, design: .monospaced))
                    .foregroundColor(.frostInkMuted)
                    .lineLimit(2)

                HStack(spacing: 8) {
                    if let readingTimeMinutes = summary.readingTimeMinutes {
                        Text("\(readingTimeMinutes) MIN")
                            .font(.system(size: 9, design: .monospaced))
                            .foregroundColor(.frostInkMuted)
                    }

                    if let domain = summary.domain {
                        Text(domain.uppercased())
                            .font(.system(size: 9, design: .monospaced))
                            .foregroundColor(.frostInkMuted)
                            .lineLimit(1)
                    }

                    Spacer()
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
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
