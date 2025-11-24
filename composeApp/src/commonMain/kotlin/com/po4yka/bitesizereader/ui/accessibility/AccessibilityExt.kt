package com.po4yka.bitesizereader.ui.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics

/**
 * Accessibility extension functions for Compose UI
 */

/**
 * Mark an element as a heading for screen readers
 */
fun Modifier.accessibilityHeading(description: String? = null): Modifier =
    semantics {
        heading()
        description?.let { contentDescription = it }
    }

/**
 * Add a content description for screen readers
 */
fun Modifier.accessibilityLabel(label: String): Modifier =
    semantics {
        contentDescription = label
    }

/**
 * Create accessibility description for a summary card
 */
fun createSummaryCardDescription(
    title: String,
    domain: String,
    readingTime: Int,
    isRead: Boolean,
    topicTags: List<String>,
): String {
    val readStatus = if (isRead) "Read" else "Unread"
    val topics =
        if (topicTags.isNotEmpty()) {
            "Topics: ${topicTags.joinToString(", ")}"
        } else {
            ""
        }

    return buildString {
        append("$readStatus article. ")
        append(title)
        append(". From $domain. ")
        append("$readingTime minute read. ")
        if (topics.isNotEmpty()) {
            append(topics)
        }
    }
}

/**
 * Create accessibility description for a button with state
 */
fun createButtonDescription(
    label: String,
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
): String {
    return buildString {
        append(label)
        when {
            isLoading -> append(". Loading")
            !isEnabled -> append(". Disabled")
        }
    }
}

/**
 * Create accessibility description for filter chips
 */
fun createFilterChipDescription(
    filter: String,
    isSelected: Boolean,
): String {
    val state = if (isSelected) "selected" else "not selected"
    return "$filter filter, $state"
}
