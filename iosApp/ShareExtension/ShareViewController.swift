import UIKit
import Social
import MobileCoreServices
import UniformTypeIdentifiers

/**
 * Share Extension View Controller
 * Receives URLs shared from Safari, Chrome, and other apps
 */
class ShareViewController: SLComposeServiceViewController {

    override func isContentValid() -> Bool {
        // Always valid since we just extract and forward the URL
        return true
    }

    override func didSelectPost() {
        // Extract URL from share context
        if let item = extensionContext?.inputItems.first as? NSExtensionItem,
           let attachments = item.attachments {

            for attachment in attachments {
                // Check for URL type
                if attachment.hasItemConformingToTypeIdentifier(UTType.url.identifier) {
                    attachment.loadItem(forTypeIdentifier: UTType.url.identifier, options: nil) { [weak self] (url, error) in
                        if let shareURL = url as? URL {
                            self?.saveSharedURL(shareURL.absoluteString)
                        }
                        self?.extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
                    }
                    return
                }

                // Check for web page (fallback for some browsers)
                if attachment.hasItemConformingToTypeIdentifier(UTType.propertyList.identifier) {
                    attachment.loadItem(forTypeIdentifier: UTType.propertyList.identifier, options: nil) { [weak self] (data, error) in
                        if let dictionary = data as? [String: Any],
                           let results = dictionary[NSExtensionJavaScriptPreprocessingResultsKey] as? [String: Any],
                           let urlString = results["URL"] as? String {
                            self?.saveSharedURL(urlString)
                        }
                        self?.extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
                    }
                    return
                }
            }
        }

        // No valid URL found - still complete the request
        extensionContext?.completeRequest(returningItems: [], completionHandler: nil)
    }

    override func configurationItems() -> [Any]! {
        // No additional configuration UI needed
        return []
    }

    override func presentationAnimationDidFinish() {
        // Auto-post immediately when opened (no user interaction needed)
        didSelectPost()
    }

    // MARK: - Private Methods

    /// Save shared URL to UserDefaults (shared with main app via App Group)
    private func saveSharedURL(_ url: String) {
        // Use App Group to share data between extension and main app
        if let sharedDefaults = UserDefaults(suiteName: "group.com.po4yka.bitesizereader") {
            sharedDefaults.set(url, forKey: "sharedURL")
            sharedDefaults.set(Date(), forKey: "sharedURLTimestamp")
            sharedDefaults.synchronize()

            print("[ShareExtension] Saved URL to shared storage: \(url)")
        } else {
            print("[ShareExtension] ERROR: Could not access shared UserDefaults")
        }
    }
}
