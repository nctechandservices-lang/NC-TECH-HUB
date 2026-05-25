package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SleekPrimary,
    onPrimary = SleekOnPrimary,
    primaryContainer = SleekPrimaryContainer,
    onPrimaryContainer = SleekOnPrimaryContainer,
    secondary = SleekSecondary,
    onSecondary = SleekOnSecondary,
    secondaryContainer = SleekSecondaryContainer,
    background = SleekBackground,
    surface = SleekSurface,
    onBackground = SleekOnBackground,
    onSurface = SleekOnSurface,
    onSurfaceVariant = SleekOnSurfaceVariant,
    outline = SleekOutline,
    outlineVariant = SleekOutline.copy(alpha = 0.5f),
    error = AdminCrimson
)

private val LightColorScheme = lightColorScheme(
    primary = SleekLightPrimary,
    onPrimary = SleekLightOnPrimary,
    primaryContainer = SleekLightPrimaryContainer,
    onPrimaryContainer = SleekLightOnPrimaryContainer,
    background = SleekLightBackground,
    surface = SleekLightSurface,
    onBackground = SleekLightOnBackground,
    onSurface = SleekLightOnSurface,
    onSurfaceVariant = SleekLightOnSurfaceVariant,
    outline = SleekLightOutline,
    outlineVariant = SleekLightOutline.copy(alpha = 0.5f),
    error = SleekLightError
)

@Composable
fun MyApplicationTheme(
    themeMode: String = "Dark", // "System", "Light", "Dark"
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
