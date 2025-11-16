import SwiftUI
import Shared

/// SwiftUI wrapper for SummaryListViewModel
@MainActor
class SummaryListViewModelWrapper: ObservableObject {
    @Published var state: SummaryListState = SummaryListState(
        summaries: [],
        isLoading: false,
        isRefreshing: false,
        isLoadingMore: false,
        error: nil,
        hasMore: true,
        filters: SearchFilters(topicTags: [], readStatus: nil, language: nil, dateFrom: nil, dateTo: nil),
        syncState: nil
    )

    private let viewModel: SummaryListViewModel
    private var stateTask: Task<Void, Never>?

    init(viewModel: SummaryListViewModel) {
        self.viewModel = viewModel
        observeState()
    }

    deinit {
        stateTask?.cancel()
    }

    private func observeState() {
        stateTask = Task { [weak self] in
            for await newState in viewModel.state {
                self?.state = newState
            }
        }
    }

    func loadSummaries(refresh: Bool = false) {
        viewModel.loadSummaries(refresh: refresh)
    }

    func loadMore() {
        viewModel.loadMore()
    }

    func refresh() {
        viewModel.refresh()
    }

    func markAsRead(id: Int32, isRead: Bool) {
        viewModel.markAsRead(id: id, isRead: isRead)
    }

    func sync(forceFullSync: Bool = false) {
        viewModel.sync(forceFullSync: forceFullSync)
    }

    func toggleTagFilter(tag: String) {
        viewModel.toggleTagFilter(tag: tag)
    }

    func setReadFilter(readStatus: String?) {
        viewModel.setReadFilter(readStatus: readStatus)
    }

    func clearFilters() {
        viewModel.clearFilters()
    }
}
