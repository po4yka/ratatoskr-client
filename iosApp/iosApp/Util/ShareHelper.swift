import SwiftUI

/// Helper for sharing content on iOS using UIActivityViewController
struct ShareHelper {
    /// Share plain text
    static func shareText(_ text: String) {
        share(items: [text])
    }

    /// Share a URL with optional title
    static func shareURL(_ urlString: String, title: String? = nil) {
        var items: [Any] = []
        if let title = title {
            items.append(title)
        }
        if let url = URL(string: urlString) {
            items.append(url)
        } else {
            items.append(urlString)
        }
        share(items: items)
    }

    /// Present the share sheet
    private static func share(items: [Any]) {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootViewController = windowScene.windows.first?.rootViewController else {
            return
        }

        let activityViewController = UIActivityViewController(
            activityItems: items,
            applicationActivities: nil
        )

        // For iPad, we need to configure the popover
        if let popoverController = activityViewController.popoverPresentationController {
            popoverController.sourceView = rootViewController.view
            popoverController.sourceRect = CGRect(
                x: rootViewController.view.bounds.midX,
                y: rootViewController.view.bounds.midY,
                width: 0,
                height: 0
            )
            popoverController.permittedArrowDirections = []
        }

        rootViewController.present(activityViewController, animated: true)
    }
}
