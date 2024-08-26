package cz.jaro.gymceska.rozvrh
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.onPointerEvent(
    coroutineScope: CoroutineScope,
    scrollStateX: ScrollState,
    scrollStateY: ScrollState,
): Modifier = onPointerEvent(
    eventType = PointerEventType.Scroll,
) { event ->
    val scrollDelta = event.changes.fold(Offset.Zero) { acc, c -> acc + c.scrollDelta }
    coroutineScope.launch {
        scrollStateX.scrollBy(-scrollDelta.x)
    }
    coroutineScope.launch {
        scrollStateY.scrollBy(-scrollDelta.y)
    }
}