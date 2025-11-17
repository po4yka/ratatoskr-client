import SwiftUI
import Shared

/// SwiftUI wrapper for SubmitURLViewModel
@MainActor
class SubmitURLViewModelWrapper: ObservableObject {
    @Published var state: SubmitURLState = SubmitURLState(
        url: "",
        isSubmitting: false,
        validationError: nil,
        error: nil,
        request: nil,
        isPolling: false
    )

    private let viewModel: SubmitURLViewModel
    private var stateTask: Task<Void, Never>?

    init(viewModel: SubmitURLViewModel) {
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

    func setURL(_ url: String) {
        viewModel.setURL(url: url)
    }

    func onUrlChange(_ url: String) {
        viewModel.onUrlChange(url: url)
    }

    func submitUrl() {
        viewModel.submitUrl()
    }

    func cancelRequest() {
        viewModel.cancelRequest()
    }

    func reset() {
        viewModel.reset()
    }
}
