package cz.jaro.gymceska.rozvrh

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope

expect fun Modifier.onPointerEvent(
    coroutineScope: CoroutineScope,
    scrollStateX: ScrollState,
    scrollStateY: ScrollState,
): Modifier