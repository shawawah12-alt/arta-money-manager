package com.zhaw.arta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ArtaScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = Obsidian,
    secondary = EmeraldIn,
    onSecondary = Obsidian,
    tertiary = Azure,
    onTertiary = Obsidian,
    background = Obsidian,
    onBackground = Ivory,
    surface = Charcoal,
    onSurface = Ivory,
    surfaceVariant = Slate,
    onSurfaceVariant = Mist,
    outline = SlateHigh,
    error = RoseOut,
    onError = Ivory,
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

@Composable
fun AgonAppTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ArtaScheme,
        typography = ArtaTypography,
        content = content,
    )
}
