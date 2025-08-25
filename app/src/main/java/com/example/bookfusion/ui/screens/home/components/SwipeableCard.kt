package com.example.bookfusion.ui.screens.home.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Eine Karte, die man seitlich wegwischen (swipen) kann.
 *
 * Die Karte bewegt sich mit dem Finger, dreht sich leicht
 * und wird kleiner. Wenn der Swipe weit genug geht,
 * wird [onSwiped] ausgelöst.
 *
 * @param modifier Modifikatoren für die Karte
 * @param onSwiped Aktion, die ausgeführt wird, wenn die Karte
 *                 weit genug geswiped wurde
 * @param content Inhalt, der in der Karte angezeigt wird
 */
@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    onSwiped: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }

    val threshold = with(LocalDensity.current) { 100.dp.toPx() }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .rotate(offsetX / 20)
            .scale(1f - (abs(offsetX) / 2000f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                    },
                    onDragEnd = {
                        if (abs(offsetX) > threshold) {
                            onSwiped()
                        } else {
                            offsetX = 0f
                        }
                    }
                )
            }
    ) {
        content()
    }
}







