package com.po4yka.bitesizereader.ui.components

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
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
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

    val readStatus = if (summary.isRead) "Read" else "Unread"
    val favoriteStatus = if (summary.isFavorited) ", Favorited" else ""
    val readingTime = summary.readingTimeMin?.let { ", $it minute read" } ?: ""
    val cardDescription = "${summary.title}. $readStatus article$favoriteStatus$readingTime"

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .semantics {
                    contentDescription = cardDescription
                    customActions =
                        listOf(
                            CustomAccessibilityAction("Archive article") {
                                onArchiveClick()
                                true
                            },
                            CustomAccessibilityAction("Delete article") {
                                onDelete()
                                true
                            },
                            CustomAccessibilityAction("Mark as read") {
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
                    contentDescription = "Mark as read",
                    tint = Carbon.theme.textOnColor,
                    modifier =
                        Modifier
                            .padding(start = 24.dp)
                            .size(24.dp),
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
                    contentDescription = "Archive",
                    tint = Carbon.theme.textOnColor,
                    modifier =
                        Modifier
                            .padding(end = 24.dp)
                            .size(24.dp),
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
