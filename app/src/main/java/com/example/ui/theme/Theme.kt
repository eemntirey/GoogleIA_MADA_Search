package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MadaElectricBlue,
    onPrimary = Color.White,
    primaryContainer = MadaSurfaceVariantDark,
    onPrimaryContainer = MadaCyan,
    secondary = MadaCyan,
    onSecondary = MadaDeepDark,
    secondaryContainer = MadaSurfaceHighlight,
    onSecondaryContainer = MadaTextPrimary,
    background = MadaDeepDark,
    onBackground = MadaTextPrimary,
    surface = MadaSurfaceDark,
    onSurface = MadaTextPrimary,
    surfaceVariant = MadaSurfaceVariantDark,
    onSurfaceVariant = MadaTextSecondary,
    outline = MadaBorder,
    error = MadaError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MadaElectricBlue,
    onPrimary = Color.White,
    primaryContainer = MadaSurfaceVariantLight,
    onPrimaryContainer = MadaElectricBlue,
    secondary = MadaElectricBlue,
    onSecondary = Color.White,
    background = MadaSurfaceLight,
    onBackground = MadaTextPrimaryLight,
    surface = Color.White,
    onSurface = MadaTextPrimaryLight,
    surfaceVariant = MadaSurfaceVariantLight,
    onSurfaceVariant = MadaTextSecondaryLight,
    outline = Color(0xFFCBD5E1),
    error = MadaError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek tech dark as per MadaSearch specifications
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
