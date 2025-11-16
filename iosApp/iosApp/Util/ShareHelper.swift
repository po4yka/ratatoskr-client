import SwiftUI
import Shared

/// Helper for sharing content on iOS using UIActivityViewController
struct ShareHelper {
    /// Share a summary
    static func shareSummary(_ summary: Summary) {
        let shareText = buildShareText(for: summary)
        let url = URL(string: summary.url)

        var items: [Any] = [shareText]
        if let url = url {
            items.append(url)
        }

        share(items: items)
    }

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

    /// Build shareable text from a summary
    private static func buildShareText(for summary: Summary) -> String {
        var text = ""

        // Title
        text += summary.title + "\n\n"

        // TLDR
        if !summary.tldr.isEmpty {
            text += "Summary:\n" + summary.tldr + "\n\n"
        }

        // Key Points
        if !summary.keyPoints.isEmpty {
            text += "Key Points:\n"
            for point in summary.keyPoints {
                text += "• \(point)\n"
            }
            text += "\n"
        }

        // URL
        text += "Read more: \(summary.url)\n"

        // Tags
        if !summary.topicTags.isEmpty {
            text += "\nTags: " + summary.topicTags.map { "#\($0)" }.joined(separator: ", ")
        }

        return text
    }
}
