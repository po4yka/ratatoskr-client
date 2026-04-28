package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.loading.Loading
import kotlin.math.roundToInt

private const val REFRESH_TRIGGER_PX = 200f
private const val MAX_PULL_PX = 300f

@Suppress("FunctionNaming")
@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    var pullOffset by remember { mutableFloatStateOf(0f) }
    var isTriggered by remember { mutableStateOf(false) }

    // Reset offset when refreshing completes
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && pullOffset > 0f) {
            animate(pullOffset, 0f) { value, _ ->
                pullOffset = value
            }
            isTriggered = false
        }
    }

    val nestedScrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    // When pulling down and we have offset, consume the scroll to reduce offset
                    if (pullOffset > 0f && available.y < 0f) {
                        val consumed = available.y.coerceAtLeast(-pullOffset)
                        pullOffset += consumed
                        return Offset(0f, consumed)
                    }
                    return Offset.Zero
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    // When there's leftover downward scroll (at top of list), use it to pull
                    if (available.y > 0f && !isRefreshing) {
                        val newOffset = (pullOffset + available.y * 0.5f).coerceIn(0f, MAX_PULL_PX)
                        pullOffset = newOffset
                        return Offset(0f, available.y)
                    }
                    return Offset.Zero
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    if (pullOffset >= REFRESH_TRIGGER_PX && !isRefreshing && !isTriggered) {
                        isTriggered = true
                        onRefresh()
                    } else if (!isRefreshing) {
                        animate(pullOffset, 0f) { value, _ ->
                            pullOffset = value
                        }
                    }
                    return Velocity.Zero
                }
            }
        }

    Box(
        modifier = modifier.nestedScroll(nestedScrollConnection),
    ) {
        // Refresh indicator
        if (pullOffset > 0f || isRefreshing) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                Loading(
                    modifier =
                        Modifier
                            .offset {
                                IntOffset(0, ((pullOffset / 2f) - 24.dp.toPx()).roundToInt())
                            },
                )
            }
        }

        // Content
        Box(
            modifier =
                Modifier.offset {
                    IntOffset(0, pullOffset.roundToInt())
                },
            content = content,
        )
    }
}
