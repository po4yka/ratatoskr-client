import SwiftUI
import Shared

/// Summary list view with pagination and pull-to-refresh
struct SummaryListView: View {
    @ObservedObject var viewModel: SummaryListViewModelWrapper
    let onSummaryTap: (Int32) -> Void
    let onSubmitUrlTap: () -> Void
    let onSearchTap: () -> Void

    @State private var showFilterSheet = false

    var body: some View {
        NavigationView {
            ZStack {
                if let error = viewModel.state.error, viewModel.state.summaries.isEmpty {
                    ErrorView(message: error) {
                        viewModel.loadSummaries(refresh: true)
                    }
                } else if viewModel.state.summaries.isEmpty && !viewModel.state.isLoading {
                    EmptyStateView(
                        title: "No summaries yet",
                        message: "Submit a URL to generate your first summary"
                    )
                } else {
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(viewModel.state.summaries, id: \.id) { summary in
                                SummaryCardView(summary: summary) {
                                    onSummaryTap(summary.id)
                                }
                                .padding(.horizontal)

                                // Load more when near bottom
                                if summary.id == viewModel.state.summaries.last?.id {
                                    if viewModel.state.hasMore && !viewModel.state.isLoadingMore {
                                        ProgressView()
                                            .padding()
                                            .onAppear {
                                                viewModel.loadMore()
                                            }
                                    }
                                }
                            }
                        }
                        .padding(.vertical)
                    }
                    .refreshable {
                        viewModel.refresh()
                    }
                }

                // Loading overlay
                if viewModel.state.isLoading && viewModel.state.summaries.isEmpty {
                    ProgressView()
                        .scaleEffect(1.5)
                }
            }
            .navigationTitle("Summaries")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: onSearchTap) {
                        Image(systemName: "magnifyingglass")
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showFilterSheet = true }) {
                        Image(systemName: "line.3.horizontal.decrease.circle")
                    }
                }
                ToolbarItem(placement: .bottomBar) {
                    Button(action: onSubmitUrlTap) {
                        Label("Submit URL", systemImage: "plus.circle.fill")
                            .font(.headline)
                    }
                    .buttonStyle(.borderedProminent)
                }
            }
            .sheet(isPresented: $showFilterSheet) {
                FilterSheetView(viewModel: viewModel)
            }
        }
    }
}

/// Filter bottom sheet
struct FilterSheetView: View {
    @ObservedObject var viewModel: SummaryListViewModelWrapper
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Reading Status")) {
                    Button(action: {
                        viewModel.setReadFilter(readStatus: viewModel.state.filters.readStatus == "read" ? nil : "read")
                    }) {
                        HStack {
                            Text("Read")
                            Spacer()
                            if viewModel.state.filters.readStatus == "read" {
                                Image(systemName: "checkmark")
                                    .foregroundColor(.accentColor)
                            }
                        }
                    }

                    Button(action: {
                        viewModel.setReadFilter(readStatus: viewModel.state.filters.readStatus == "unread" ? nil : "unread")
                    }) {
                        HStack {
                            Text("Unread")
                            Spacer()
                            if viewModel.state.filters.readStatus == "unread" {
                                Image(systemName: "checkmark")
                                    .foregroundColor(.accentColor)
                            }
                        }
                    }
                }

                Section {
                    Button(action: {
                        viewModel.clearFilters()
                        dismiss()
                    }) {
                        Text("Clear Filters")
                            .foregroundColor(.red)
                    }
                }
            }
            .navigationTitle("Filters")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
        }
    }
}
