package com.po4yka.bitesizereader.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import bitesizereader.core.ui.generated.resources.Res
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_favorited
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_read_article
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_reading_time
import bitesizereader.core.ui.generated.resources.summary_card_accessibility_unread_article
import bitesizereader.core.ui.generated.resources.summary_card_mark_read
import bitesizereader.core.ui.generated.resources.swipeable_summary_archive
import bitesizereader.core.ui.generated.resources.swipeable_summary_archive_action
import bitesizereader.core.ui.generated.resources.swipeable_summary_delete_action
import bitesizereader.core.ui.generated.resources.swipeable_summary_mark_read_action
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.core.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.core.ui.theme.Dimensions
import com.po4yka.bitesizereader.core.ui.theme.IconSizes
import com.po4yka.bitesizereader.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

private const val SWIPE_THRESHOLD = 100f
private const val MAX_SWIPE = 150f

@Suppress("FunctionNaming", "LongParameterList")
@Composable
fun SwipeableSummaryCard(
    summary: Summary,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onMarkRead: () -> Unit,
    onFavoriteClick: () -> Unit = {},
    onAddToCollectionClick: () -> Unit = {},
    onArchiveClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 200),
        label = "swipe_offset",
    )

    // Background colors based on swipe direction
    val leftBackgroundColor by animateColorAsState(
        targetValue =
            if (offsetX < -SWIPE_THRESHOLD / 2) {
                Carbon.theme.supportWarning
            } else {
                Carbon.theme.layer02
            },
        label = "left_bg_color",
    )

    val rightBackgroundColor by animateColorAsState(
        targetValue =
            if (offsetX > SWIPE_THRESHOLD / 2) {
                Carbon.theme.supportSuccess
            } else {
                Carbon.theme.layer02
            },
        label = "right_bg_color",
    )

    val cardDescription =
        buildString {
            append(summary.title)
            append(". ")
            append(
                if (summary.isRead) {
                    stringResource(Res.string.summary_card_accessibility_read_article)
                } else {
                    stringResource(Res.string.summary_card_accessibility_unread_article)
                },
            )
            if (summary.isFavorited) {
                append(" ")
                append(stringResource(Res.string.summary_card_accessibility_favorited))
            }
            summary.readingTimeMin?.let {
                append(" ")
                append(stringResource(Res.string.summary_card_accessibility_reading_time, it))
            }
        }
    val archiveActionLabel = stringResource(Res.string.swipeable_summary_archive_action)
    val deleteActionLabel = stringResource(Res.string.swipeable_summary_delete_action)
    val markReadActionLabel = stringResource(Res.string.swipeable_summary_mark_read_action)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Dimensions.cardCornerRadius))
                .semantics {
                    contentDescription = cardDescription
                    customActions =
                        listOf(
                            CustomAccessibilityAction(archiveActionLabel) {
                                onArchiveClick()
                                true
                            },
                            CustomAccessibilityAction(deleteActionLabel) {
                                onDelete()
                                true
                            },
                            CustomAccessibilityAction(markReadActionLabel) {
                                onMarkRead()
                                true
                            },
                        )
                },
    ) {
        // Background action indicators
        Row(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(Color.Transparent),
        ) {
            // Right swipe background (mark read)
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(rightBackgroundColor),
                contentAlignment = Alignment.CenterStart,
            ) {
                Icon(
                    imageVector = CarbonIcons.Checkmark,
                    contentDescription = stringResource(Res.string.summary_card_mark_read),
                    tint = Carbon.theme.textOnColor,
                    modifier =
                        Modifier
                            .padding(start = Spacing.lg)
                            .size(Dimensions.stageIndicatorSize),
                )
            }

            // Left swipe background (archive)
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(leftBackgroundColor),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = CarbonIcons.Archive,
                    contentDescription = stringResource(Res.string.swipeable_summary_archive),
                    tint = Carbon.theme.textOnColor,
                    modifier =
                        Modifier
                            .padding(end = Spacing.lg)
                            .size(Dimensions.stageIndicatorSize),
                )
            }
        }

        // Foreground card
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                when {
                                    offsetX > SWIPE_THRESHOLD -> {
                                        onMarkRead()
                                        offsetX = 0f
                                    }
                                    offsetX < -SWIPE_THRESHOLD -> {
                                        onArchiveClick()
                                        offsetX = 0f
                                    }
                                    else -> {
                                        offsetX = 0f
                                    }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                offsetX = (offsetX + dragAmount).coerceIn(-MAX_SWIPE, MAX_SWIPE)
                            },
                        )
                    },
        ) {
            SummaryCard(
                summary = summary,
                onClick = onClick,
                onDeleteClick = onDelete,
                onMarkReadClick = onMarkRead,
                onFavoriteClick = onFavoriteClick,
                onAddToCollectionClick = onAddToCollectionClick,
            )
        }
    }
}
