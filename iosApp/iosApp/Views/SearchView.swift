import SwiftUI
import Shared

/// Search view with search bar and results
struct SearchView: View {
    @ObservedObject var viewModel: SearchViewModelWrapper
    let onSummaryTap: (Int32) -> Void
    let onBack: () -> Void

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Search bar
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.secondary)

                    TextField("Search summaries...", text: Binding(
                        get: { viewModel.state.query },
                        set: { viewModel.onQueryChange($0) }
                    ))
                    .textFieldStyle(.plain)

                    if !viewModel.state.query.isEmpty {
                        Button(action: { viewModel.clearSearch() }) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.secondary)
                        }
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(10)
                .padding()

                // Results
                if let error = viewModel.state.error {
                    ErrorView(message: error) {
                        viewModel.search()
                    }
                } else if viewModel.state.isSearching {
                    ProgressView()
                        .scaleEffect(1.5)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if viewModel.state.results.isEmpty && !viewModel.state.query.isEmpty {
                    EmptyStateView(
                        title: "No results found",
                        message: "Try different keywords or check your spelling",
                        systemImage: "magnifyingglass"
                    )
                } else if viewModel.state.results.isEmpty {
                    EmptyStateView(
                        title: "Search Summaries",
                        message: "Enter keywords to search through your summaries",
                        systemImage: "magnifyingglass"
                    )
                } else {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 12) {
                            Text("\(viewModel.state.results.count) results found")
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .padding(.horizontal)

                            LazyVStack(spacing: 12) {
                                ForEach(viewModel.state.results, id: \.id) { summary in
                                    SummaryCardView(summary: summary) {
                                        onSummaryTap(summary.id)
                                    }
                                    .padding(.horizontal)
                                }
                            }
                        }
                        .padding(.vertical)
                    }
                }
            }
            .navigationTitle("Search")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onBack) {
                        Image(systemName: "chevron.left")
                    }
                }
            }
        }
        .navigationBarBackButtonHidden(true)
    }
}
