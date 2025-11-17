import SwiftUI
import Shared

/// Summary detail view with scrollable content
struct SummaryDetailView: View {
    @ObservedObject var viewModel: SummaryDetailViewModelWrapper
    let onBack: () -> Void
    let onShare: () -> Void

    var body: some View {
        ZStack {
            if let error = viewModel.state.error {
                ErrorView(message: error) {
                    viewModel.retry()
                }
            } else if viewModel.state.isLoading {
                ProgressView()
                    .scaleEffect(1.5)
            } else if let summary = viewModel.state.summary {
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        // Title
                        Text(summary.title)
                            .font(.title)
                            .fontWeight(.bold)

                        // Metadata
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Text(summary.sourceDomain ?? "Unknown source")
                                    .font(.caption)
                                    .foregroundColor(.secondary)

                                Spacer()

                                if let readingTime = summary.readingTime {
                                    Text("\(readingTime) min read")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                            }

                            Text(formatDate(summary.createdAt))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }

                        // Topic Tags
                        if !summary.topicTags.isEmpty {
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 8) {
                                    ForEach(summary.topicTags, id: \.self) { tag in
                                        TagChipView(tag: tag)
                                    }
                                }
                            }
                        }

                        Divider()

                        // TL;DR
                        SectionView(title: "TL;DR") {
                            Text(summary.tldr)
                                .font(.body)
                                .fontWeight(.semibold)
                        }

                        // 250-word Summary
                        SectionView(title: "Summary") {
                            Text(summary.summary250)
                                .font(.body)
                        }

                        // Key Ideas
                        if !summary.keyIdeas.isEmpty {
                            SectionView(title: "Key Ideas") {
                                VStack(alignment: .leading, spacing: 8) {
                                    ForEach(Array(summary.keyIdeas.enumerated()), id: \.offset) { index, idea in
                                        HStack(alignment: .top) {
                                            Text("\(index + 1).")
                                                .fontWeight(.bold)
                                            Text(idea)
                                        }
                                    }
                                }
                            }
                        }

                        // Entities
                        if let entities = summary.entities, !entities.isEmpty {
                            SectionView(title: "Key Entities") {
                                VStack(alignment: .leading, spacing: 4) {
                                    ForEach(entities, id: \.self) { entity in
                                        Text("• \(entity)")
                                    }
                                }
                            }
                        }

                        // Quotes
                        if let quotes = summary.quotes, !quotes.isEmpty {
                            SectionView(title: "Notable Quotes") {
                                VStack(spacing: 12) {
                                    ForEach(quotes, id: \.self) { quote in
                                        Text("\"\(quote)\"")
                                            .italic()
                                            .padding()
                                            .background(Color(.systemGray6))
                                            .cornerRadius(8)
                                    }
                                }
                            }
                        }

                        // Original URL
                        if let sourceUrl = summary.sourceUrl {
                            Divider()
                            SectionView(title: "Original Article") {
                                Link(sourceUrl, destination: URL(string: sourceUrl)!)
                                    .font(.caption)
                                    .foregroundColor(.accentColor)
                            }
                        }
                    }
                    .padding()
                }
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(action: onBack) {
                            Image(systemName: "chevron.left")
                        }
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            viewModel.toggleReadStatus()
                        }) {
                            Image(systemName: summary.isRead ? "checkmark.circle.fill" : "circle")
                                .foregroundColor(summary.isRead ? .green : .secondary)
                        }
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: onShare) {
                            Image(systemName: "square.and.arrow.up")
                        }
                    }
                }
            }
        }
        .navigationBarBackButtonHidden(true)
    }

    private func formatDate(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp / 1000))
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM dd, yyyy"
        return formatter.string(from: date)
    }
}

/// Section view with title
struct SectionView<Content: View>: View {
    let title: String
    let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(.headline)
                .fontWeight(.bold)

            content()
        }
    }
}
