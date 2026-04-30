package com.po4yka.ratatoskr.core.ui.components.foundation

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.frostLight
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Frost press indication: draws a 1dp inset ink border while the target is pressed.
 *
 * No ripple, no color change — pure brutalist bracket press.
 * Implements DESIGN.md § Components — click-press at alpha 1.00 ink hairline.
 * Uses [IndicationNodeFactory] (Compose Foundation 1.7+ API).
 */
object FrostIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        FrostIndicationNode(interactionSource, frostLight.ink)

    override fun hashCode(): Int = -1

    override fun equals(other: Any?): Boolean = other === this
}

private class FrostIndicationNode(
    private val interactionSource: InteractionSource,
    private val inkColor: Color,
) : Modifier.Node(), DrawModifierNode {
    private var isPressed = false

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        isPressed = true
                        invalidateDraw()
                    }
                    is PressInteraction.Release, is PressInteraction.Cancel -> {
                        isPressed = false
                        invalidateDraw()
                    }
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (isPressed) {
            drawRect(
                color = inkColor,
                style = Stroke(width = 1.dp.toPx()),
            )
        }
    }
}
