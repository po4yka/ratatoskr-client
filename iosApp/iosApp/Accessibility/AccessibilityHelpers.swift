import SwiftUI

/// Accessibility helpers for VoiceOver support
struct AccessibilityHelpers {

    /// Create accessibility label for summary card
    static func summaryCardLabel(
        title: String,
        domain: String,
        readingTime: Int,
        isRead: Bool,
        topicTags: [String]
    ) -> String {
        let readStatus = isRead ? "Read" : "Unread"
        let topics = topicTags.isEmpty ? "" : "Topics: \(topicTags.joined(separator: ", "))"

        var label = "\(readStatus) article. \(title). From \(domain). \(readingTime) minute read."
        if !topics.isEmpty {
            label += " \(topics)"
        }
        return label
    }

    /// Create accessibility label for button with state
    static func buttonLabel(_ title: String, isLoading: Bool = false, isEnabled: Bool = true) -> String {
        var label = title
        if isLoading {
            label += ". Loading"
        } else if !isEnabled {
            label += ". Disabled"
        }
        return label
    }

    /// Create accessibility label for filter chip
    static func filterChipLabel(_ filter: String, isSelected: Bool) -> String {
        let state = isSelected ? "selected" : "not selected"
        return "\(filter) filter, \(state)"
    }

    /// Create accessibility hint for interactive elements
    static func interactionHint(_ action: String) -> String {
        return "Double tap to \(action)"
    }
}

/// SwiftUI View extension for accessibility
extension View {
    /// Add accessibility label and hint
    func accessibilityElement(label: String, hint: String? = nil, traits: AccessibilityTraits = []) -> some View {
        self
            .accessibilityLabel(label)
            .accessibilityHint(hint ?? "")
            .accessibilityAddTraits(traits)
    }

    /// Mark as heading for VoiceOver
    func accessibilityHeading(_ label: String) -> some View {
        self
            .accessibilityLabel(label)
            .accessibilityAddTraits(.isHeader)
    }

    /// Add accessibility action
    func accessibilityAction(named name: String, action: @escaping () -> Void) -> some View {
        self.accessibilityAction(named: Text(name), action)
    }
}
