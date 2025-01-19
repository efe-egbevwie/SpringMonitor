package common.ui.modifiers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.hoverClickable(
    color: Color = MaterialTheme.colorScheme.secondary,
    normalAlpha: Float = 0f,
    hoverAlpha: Float = 0.8f,
    shape: Shape = RoundedCornerShape(10.dp),
    onClicked: () -> Unit
): Modifier {
    var isHovered by remember { mutableStateOf(false) }
    val backgroundAlpha = if (isHovered) {
        hoverAlpha
    } else {
        normalAlpha
    }

    return this
        .background(color.copy(alpha = backgroundAlpha), shape = shape)
        .clickable {
            onClicked()
        }
        .pointerMoveFilter(
            onEnter = {
                isHovered = true
                false
            },
            onExit = {
                isHovered = false
                false
            }
        )
}
