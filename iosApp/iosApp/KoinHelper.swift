import Foundation
import Shared

/// Helper class to access Koin dependencies from Swift
class KoinHelper {
    private let koin: Koin_coreKoin

    init(koin: Koin_coreKoin) {
        self.koin = koin
    }

    func getLoginViewModel() -> LoginViewModel {
        return koin.get(objCClass: LoginViewModel.self, qualifier: nil, parameters: nil) as! LoginViewModel
    }

    func getSummaryListViewModel() -> SummaryListViewModel {
        return koin.get(objCClass: SummaryListViewModel.self, qualifier: nil, parameters: nil) as! SummaryListViewModel
    }

    func getSummaryDetailViewModel(summaryId: Int32) -> SummaryDetailViewModel {
        return koin.get(
            objCClass: SummaryDetailViewModel.self,
            qualifier: nil,
            parameters: { KotlinArray(size: 1, init: { _ in summaryId as AnyObject }) }
        ) as! SummaryDetailViewModel
    }

    func getSubmitURLViewModel() -> SubmitURLViewModel {
        return koin.get(objCClass: SubmitURLViewModel.self, qualifier: nil, parameters: nil) as! SubmitURLViewModel
    }

    func getSearchViewModel() -> SearchViewModel {
        return koin.get(objCClass: SearchViewModel.self, qualifier: nil, parameters: nil) as! SearchViewModel
    }
}
