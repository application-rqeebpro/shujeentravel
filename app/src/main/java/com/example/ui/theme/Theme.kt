package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ShajeenSkyBlue,
    onPrimary = Color.White,
    primaryContainer = ShajeenDarkCard,
    onPrimaryContainer = ShajeenGoldLight,
    secondary = ShajeenGold,
    onSecondary = ShajeenDarkBlue,
    background = ShajeenDarkBackground,
    onBackground = ShajeenDarkTextPrimary,
    surface = ShajeenDarkSurface,
    onSurface = ShajeenDarkTextPrimary,
    surfaceVariant = ShajeenDarkCard,
    onSurfaceVariant = ShajeenDarkTextSecondary,
    outline = Color(0xFF2A4D73)
)

private val LightColorScheme = lightColorScheme(
    primary = ShajeenDarkBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF4FC),
    onPrimaryContainer = ShajeenDarkBlue,
    secondary = ShajeenSkyBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCEAF9),
    onSecondaryContainer = ShajeenDarkBlue,
    tertiary = ShajeenGold,
    onTertiary = Color.White,
    background = ShajeenBackgroundLight,
    onBackground = ShajeenTextPrimary,
    surface = ShajeenSurfaceLight,
    onSurface = ShajeenTextPrimary,
    surfaceVariant = Color(0xFFEDF2F7),
    onSurfaceVariant = ShajeenTextSecondary,
    outline = ShajeenCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand identity by default
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
