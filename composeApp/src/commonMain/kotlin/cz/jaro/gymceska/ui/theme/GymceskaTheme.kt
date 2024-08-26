package cz.jaro.gymceska.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun GymceskaTheme(
    useDarkTheme: Boolean,
    useDynamicColor: Boolean,
    content: @Composable () -> Unit,
) {

    val colorScheme = when {
        areDynamicColorsSupported() && useDynamicColor -> when {
            useDarkTheme -> dynamicDarkColorScheme()
            else -> dynamicLightColorScheme()
        }

        else -> when {
            useDarkTheme -> DarkColors
            else -> LightColors
        }
    }

    SetStatusBarColor(colorScheme.background, !useDarkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
