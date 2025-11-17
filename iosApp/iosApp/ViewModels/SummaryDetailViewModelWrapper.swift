import SwiftUI
import Shared

/// SwiftUI wrapper for SummaryDetailViewModel
@MainActor
class SummaryDetailViewModelWrapper: ObservableObject {
    @Published var state: SummaryDetailState = SummaryDetailState(
        summary: nil,
        isLoading: false,
        error: nil
    )

    private let viewModel: SummaryDetailViewModel
    private var stateTask: Task<Void, Never>?

    init(viewModel: SummaryDetailViewModel) {
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

    func loadSummary() {
        viewModel.loadSummary()
    }

    func toggleReadStatus() {
        viewModel.toggleReadStatus()
    }

    func retry() {
        viewModel.retry()
    }
}
