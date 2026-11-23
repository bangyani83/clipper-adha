package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ClipperDarkColorScheme = darkColorScheme(
    primary = RoyalPurple,
    onPrimary = Color.White,
    primaryContainer = DeepPurple,
    onPrimaryContainer = SoftLavender,
    secondary = LuxuryGold,
    onSecondary = Color(0xFF1A1200),
    secondaryContainer = DarkGoldContainer,
    onSecondaryContainer = SoftGold,
    tertiary = NeonViolet,
    onTertiary = Color.White,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = ObsidianCard,
    surfaceContainerHigh = ObsidianElevated,
    outline = ObsidianBorder,
    error = AlertRed,
    onError = Color.White
)

@Composable
fun ClipperAdhaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClipperDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
