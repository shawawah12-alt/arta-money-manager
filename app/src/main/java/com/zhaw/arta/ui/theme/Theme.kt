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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

enum class ThemeMode { System, Light, Dark }

private val DarkScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = DarkTokens.heroText,
    secondary = EmeraldIn,
    background = DarkTokens.canvas,
    onBackground = DarkTokens.textPrimary,
    surface = DarkTokens.card,
    onSurface = DarkTokens.textPrimary,
    surfaceVariant = DarkTokens.shimmerBase,
    onSurfaceVariant = DarkTokens.textSecondary,
    outline = DarkTokens.cardBorder,
    error = RoseOut,
)

private val LightScheme = lightColorScheme(
    primary = LightTokens.accent,
    onPrimary = LightTokens.card,
    secondary = LightTokens.positive,
    background = LightTokens.canvas,
    onBackground = LightTokens.textPrimary,
    surface = LightTokens.card,
    onSurface = LightTokens.textPrimary,
    surfaceVariant = LightTokens.shimmerBase,
    onSurfaceVariant = LightTokens.textSecondary,
    outline = LightTokens.cardBorder,
    error = LightTokens.negative,
)

private val ArtaTypography = Typography(
    displaySmall = TextStyle(fontWeight = FontWeight.Black, fontSize = 34.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, letterSpacing = (-0.25).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 0.3.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.4.sp),
)

private val ArtaShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
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
