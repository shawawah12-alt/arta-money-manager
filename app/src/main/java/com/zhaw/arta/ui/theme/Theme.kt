package com.zhaw.arta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ThemeMode { System, Light, Dark }

private val LightScheme = lightColorScheme(
    primary = LightTokens.accent,
    onPrimary = LightTokens.card,
    background = LightTokens.canvas,
    onBackground = LightTokens.textPrimary,
    surface = LightTokens.card,
    onSurface = LightTokens.textPrimary,
    surfaceVariant = LightTokens.shimmerBase,
    onSurfaceVariant = LightTokens.textSecondary,
    outline = LightTokens.cardBorder,
    error = LightTokens.negative,
)

private val DarkScheme = darkColorScheme(
    primary = DarkTokens.accent,
    onPrimary = DarkTokens.canvas,
    background = DarkTokens.canvas,
    onBackground = DarkTokens.textPrimary,
    surface = DarkTokens.card,
    onSurface = DarkTokens.textPrimary,
    surfaceVariant = DarkTokens.shimmerBase,
    onSurfaceVariant = DarkTokens.textSecondary,
    outline = DarkTokens.cardBorder,
    error = DarkTokens.negative,
)

// Typography v2 — hierarchy lewat ukuran & weight, BUKAN lewat caps/tracking.
// Angka besar pakai weight berat + tracking rapat (kaya Copilot Money).
private val ArtaTypography = Typography(
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 40.sp, letterSpacing = (-1.2).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = (-0.5).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 19.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, letterSpacing = 0.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.sp),
)

private val ArtaShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

@Composable
fun ArtaTheme(
    mode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit,
) {
    val dark = when (mode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    val tokens = if (dark) DarkTokens else LightTokens

    CompositionLocalProvider(LocalArtaTokens provides tokens) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            typography = ArtaTypography,
            shapes = ArtaShapes,
            content = content,
        )
    }
}
