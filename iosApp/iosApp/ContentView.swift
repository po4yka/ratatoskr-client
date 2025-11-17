import SwiftUI
import Shared

/// Main content view with Decompose navigation
struct ContentView: View {
    @StateObject private var navigator = NavigationState()
    private let rootComponent: RootComponent
    private let koinHelper: KoinHelper

    init(rootComponent: RootComponent, koinHelper: KoinHelper) {
        self.rootComponent = rootComponent
        self.koinHelper = koinHelper
    }

    var body: some View {
        ObservingView(rootComponent.stack) { childStack in
            let child = childStack.active.instance

            switch child {
            case is Screen.Auth:
                let viewModel = LoginViewModelWrapper(
                    viewModel: koinHelper.getLoginViewModel()
                )
                AuthView(viewModel: viewModel) {
                    rootComponent.navigateToSummaryList()
                }

            case is Screen.SummaryList:
                let viewModel = SummaryListViewModelWrapper(
                    viewModel: koinHelper.getSummaryListViewModel()
                )
                SummaryListView(
                    viewModel: viewModel,
                    onSummaryTap: { id in
                        rootComponent.navigateToSummaryDetail(id: id)
                    },
                    onSubmitUrlTap: {
                        rootComponent.navigateToSubmitUrl()
                    },
                    onSearchTap: {
                        rootComponent.navigateToSearch()
                    }
                )

            case let screen as Screen.SummaryDetail:
                let viewModel = SummaryDetailViewModelWrapper(
                    viewModel: koinHelper.getSummaryDetailViewModel(summaryId: screen.id)
                )
                SummaryDetailView(
                    viewModel: viewModel,
                    onBack: { rootComponent.pop() },
                    onShare: {
                        if let summary = viewModel.state.summary {
                            ShareHelper.shareSummary(summary)
                        }
                    }
                )

            case let screen as Screen.SubmitUrl:
                let viewModel = SubmitURLViewModelWrapper(
                    viewModel: koinHelper.getSubmitURLViewModel()
                )

                // Set prefilled URL if provided (from share extension)
                if let prefilledUrl = screen.prefilledUrl {
                    viewModel.setURL(prefilledUrl)
                }

                SubmitURLView(
                    viewModel: viewModel,
                    onBack: { rootComponent.pop() },
                    onSuccess: { summaryId in
                        rootComponent.pop()
                        rootComponent.navigateToSummaryDetail(id: summaryId)
                    }
                )

            case is Screen.Search:
                let viewModel = SearchViewModelWrapper(
                    viewModel: koinHelper.getSearchViewModel()
                )
                SearchView(
                    viewModel: viewModel,
                    onSummaryTap: { id in
                        rootComponent.navigateToSummaryDetail(id: id)
                    },
                    onBack: { rootComponent.pop() }
                )

            default:
                EmptyView()
            }
        }
    }
}

/// Helper for observing Decompose state changes
struct ObservingView<T: AnyObject, Content: View>: View {
    @StateObject private var observer: Observer<T>
    private let content: (T) -> Content

    init(_ value: SkieSwiftStateFlow<T>, @ViewBuilder content: @escaping (T) -> Content) {
        _observer = StateObject(wrappedValue: Observer(value: value))
        self.content = content
    }

    var body: some View {
        content(observer.value)
    }
}

@MainActor
private class Observer<T: AnyObject>: ObservableObject {
    @Published var value: T
    private var task: Task<Void, Never>?

    init(value: SkieSwiftStateFlow<T>) {
        self.value = value.value
        task = Task { [weak self] in
            for await newValue in value {
                self?.value = newValue
            }
        }
    }

    deinit {
        task?.cancel()
    }
}

/// Navigation state
class NavigationState: ObservableObject {
    @Published var currentScreen: Screen = Screen.Auth()
}

