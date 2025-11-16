import SwiftUI
import Shared

/// SwiftUI wrapper for LoginViewModel using SKIE for Flow conversion
@MainActor
class LoginViewModelWrapper: ObservableObject {
    @Published var state: LoginState = LoginState(
        isLoading: false,
        isAuthenticated: false,
        user: nil,
        error: nil
    )

    private let viewModel: LoginViewModel
    private var stateTask: Task<Void, Never>?

    init(viewModel: LoginViewModel) {
        self.viewModel = viewModel
        observeState()
    }

    deinit {
        stateTask?.cancel()
    }

    private func observeState() {
        stateTask = Task { [weak self] in
            // SKIE converts Flow<T> to AsyncSequence
            for await newState in viewModel.state {
                self?.state = newState
            }
        }
    }

    func loginWithTelegram() {
        viewModel.loginWithTelegram()
    }

    func loginWithTelegram(
        telegramUserId: Int64,
        authHash: String,
        authDate: Int64,
        username: String?,
        firstName: String?,
        lastName: String?,
        photoUrl: String?,
        clientId: String
    ) {
        viewModel.loginWithTelegram(
            telegramUserId: telegramUserId,
            authHash: authHash,
            authDate: authDate,
            username: username,
            firstName: firstName,
            lastName: lastName,
            photoUrl: photoUrl,
            clientId: clientId
        )
    }

    func logout() {
        viewModel.logout()
    }
}
