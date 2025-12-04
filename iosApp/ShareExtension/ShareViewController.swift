import UIKit
import Social
import MobileCoreServices
import Shared // Assuming you have a shared framework available

class ShareViewController: SLComposeServiceViewController {

    override func isContentValid() -> Bool {
        // Do validation of contentText and/or NSExtensionContext attachments here
        return true
    }

    override fun didSelectPost() {
        // This is called after the user selects Post. Do the upload of contentText and/or NSExtensionContext attachments.
        
        guard let extensionContext = self.extensionContext else { return }
        
        let content = contentText
        
        // Find URL in attachments
        if let item = extensionContext.inputItems.first as? NSExtensionItem,
           let attachments = item.attachments {
            
            for provider in attachments {
                if provider.hasItemConformingToTypeIdentifier(kUTTypeURL as String) {
                    provider.loadItem(forTypeIdentifier: kUTTypeURL as String, options: nil) { [weak self] (data, error) in
                        if let url = data as? URL {
                            self?.submitUrl(url.absoluteString)
                        }
                    }
                }
            }
        }
        
        // Inform the host that we're done, so it un-blocks its UI. Note: Alternatively you could call super's -didSelectPost, which will similarly complete the extension context.
        self.extensionContext!.completeRequest(returningItems: [], completionHandler: nil)
    }

    override func configurationItems() -> [Any]! {
        // To add configuration options via table cells at the bottom of the sheet, return an array of SLComposeSheetConfigurationItem here.
        return []
    }
    
    private func submitUrl(_ url: String) {
        // Logic to call Shared Kotlin code to submit URL
        // Note: This requires the Shared framework to be linked to the Extension target
        // and potentially sharing data via App Groups (NSUserDefaults suite) if direct network call isn't desired/possible immediately.
        print("Submitting URL from Share Extension: \(url)")
        
        // Example: Save to shared defaults to be picked up by main app
        if let userDefaults = UserDefaults(suiteName: "group.com.po4yka.bitesizereader") {
            var pendingUrls = userDefaults.array(forKey: "pending_urls") as? [String] ?? []
            pendingUrls.append(url)
            userDefaults.set(pendingUrls, forKey: "pending_urls")
        }
    }
}