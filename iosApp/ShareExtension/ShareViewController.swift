import Social
import UIKit
import UniformTypeIdentifiers

final class ShareViewController: SLComposeServiceViewController {

    override func isContentValid() -> Bool {
        return true
    }

    override func didSelectPost() {
        extractSharedURL { [weak self] url in
            guard let self else { return }

            if let url {
                AppGroupStore.storeSharedURL(url)
                openHostApp()
            }

            self.extensionContext?.completeRequest(returningItems: nil)
        }
    }

    override func configurationItems() -> [Any]! {
        return []
    }

    private func extractSharedURL(completion: @escaping (URL?) -> Void) {
        if let textURL = url(from: contentText) {
            completion(textURL)
            return
        }

        guard
            let item = extensionContext?.inputItems.first as? NSExtensionItem,
            let attachments = item.attachments
        else {
            completion(nil)
            return
        }

        if let urlProvider = attachments.first(where: { $0.hasItemConformingToTypeIdentifier(UTType.url.identifier) }) {
            urlProvider.loadItem(forTypeIdentifier: UTType.url.identifier, options: nil) { item, _ in
                let sharedURL = (item as? URL) ?? (item as? NSURL as URL?)
                DispatchQueue.main.async {
                    completion(sharedURL)
                }
            }
            return
        }

        if let textProvider = attachments.first(where: { $0.hasItemConformingToTypeIdentifier(UTType.text.identifier) }) {
            textProvider.loadItem(forTypeIdentifier: UTType.text.identifier, options: nil) { item, _ in
                let sharedURL = self.url(from: item as? String)
                DispatchQueue.main.async {
                    completion(sharedURL)
                }
            }
            return
        }

        completion(nil)
    }

    private func url(from rawValue: String?) -> URL? {
        guard let rawValue else { return nil }
        let trimmed = rawValue.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return nil }
        return URL(string: trimmed)
    }

    private func openHostApp() {
        extensionContext?.open(AppGroupContract.submitURLDeepLink(), completionHandler: nil)
    }
}
