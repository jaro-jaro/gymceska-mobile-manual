package cz.jaro.gymceska.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

actual fun areDynamicColorsSupported() = false

@Composable
actual fun dynamicDarkColorScheme(): ColorScheme = throw IllegalArgumentException("Dynamic colors are not supported on desktop")

@Composable
actual fun dynamicLightColorScheme(): ColorScheme = throw IllegalArgumentException("Dynamic colors are not supported on desktop")

@Composable
actual fun SetStatusBarColor(statusBarColor: Color, isAppearanceLightStatusBars: Boolean) = Unit