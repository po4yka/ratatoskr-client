import SwiftUI
import Shared

/// SwiftUI wrapper for SearchViewModel
@MainActor
class SearchViewModelWrapper: ObservableObject {
    @Published var state: SearchState = SearchState(
        query: "",
        results: [],
        isSearching: false,
        error: nil,
        trendingTopics: []
    )

    private let viewModel: SearchViewModel
    private var stateTask: Task<Void, Never>?

    init(viewModel: SearchViewModel) {
        self.viewModel = viewModel
        observeState()
    }

    deinit {
        stateTask?.cancel()
        viewModel.onCleared()
    }

    private func observeState() {
        stateTask = Task { [weak self] in
            for await newState in viewModel.state {
                self?.state = newState
            }
        }
    }

    func onQueryChange(_ query: String) {
        viewModel.onQueryChange(query: query)
    }

    func search() {
        viewModel.search()
    }

    func clearSearch() {
        viewModel.clearSearch()
    }
}
