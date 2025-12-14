import Foundation
import Shared

/// Helper class to access Koin dependencies from Swift
class KoinHelper {
    let koin: Koin_coreKoin

    init(koin: Koin_coreKoin) {
        self.koin = koin
    }
}
