package com.zhaw.arta.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =====================================================================
// Arta palette v2 — "quiet money"
// Prinsip: nyaris monokrom, SATU warna aksen (emerald ink) dipakai hemat,
// angka & typography yang jadi hero — bukan gradient.
// =====================================================================

val AccentLight = Color(0xFF0B7A55)   // emerald ink — dipakai dikit aja
val AccentDark = Color(0xFF3ECF8E)

data class ArtaTokens(
    val isDark: Boolean,
    val canvas: Color,
    val canvasGradient: List<Color>,   // sekarang flat (satu warna) — no gradient
    val card: Color,
    val cardBorder: Color,
    val glass: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textFaint: Color,
    val divider: Color,
    val shimmerBase: Color,
    val shimmerHighlight: Color,
    val navBar: Color,
    val heroGradient: List<Color>,
    val heroText: Color,
    val pillOnHero: Color,
    val positive: Color,
    val negative: Color,
    val accent: Color,
    val accentSoft: Color,
)

val LightTokens = ArtaTokens(
    isDark = false,
    canvas = Color(0xFFFAFAF8),
    canvasGradient = listOf(Color(0xFFFAFAF8), Color(0xFFFAFAF8)),
    card = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFEBEAE6),
    glass = Color.Transparent,
    textPrimary = Color(0xFF191A1E),
    textSecondary = Color(0xFF75767C),
    textFaint = Color(0xFFB4B5BA),
    divider = Color(0xFFF0EFEB),
    shimmerBase = Color(0xFFF1F0EC),
    shimmerHighlight = Color(0xFFFBFAF7),
    navBar = Color(0xFFFFFFFF),
    heroGradient = listOf(Color(0xFFFAFAF8), Color(0xFFFAFAF8)), // hero = flat canvas, typography yang kerja
    heroText = Color(0xFF191A1E),
    pillOnHero = Color(0xFFF1F0EC),
    positive = Color(0xFF0B7A55),
    negative = Color(0xFFC63D42),
    accent = AccentLight,
    accentSoft = AccentLight.copy(alpha = 0.09f),
)

val DarkTokens = ArtaTokens(
    isDark = true,
    canvas = Color(0xFF121316),
    canvasGradient = listOf(Color(0xFF121316), Color(0xFF121316)),
    card = Color(0xFF1A1B1F),
    cardBorder = Color(0xFF26272C),
    glass = Color.Transparent,
    textPrimary = Color(0xFFEDEDEA),
    textSecondary = Color(0xFF8E8F95),
    textFaint = Color(0xFF55565C),
    divider = Color(0xFF222327),
    shimmerBase = Color(0xFF1E1F24),
    shimmerHighlight = Color(0xFF2A2B31),
    navBar = Color(0xFF17181C),
    heroGradient = listOf(Color(0xFF121316), Color(0xFF121316)),
    heroText = Color(0xFFEDEDEA),
    pillOnHero = Color(0xFF1E1F24),
    positive = Color(0xFF3ECF8E),
    negative = Color(0xFFE5646A),
    accent = AccentDark,
    accentSoft = AccentDark.copy(alpha = 0.10f),
)

val LocalArtaTokens = staticCompositionLocalOf { LightTokens }

object Arta {
    val tokens: ArtaTokens
        @Composable @ReadOnlyComposable get() = LocalArtaTokens.current
}

fun heroBrush(tokens: ArtaTokens): Brush = Brush.verticalGradient(tokens.heroGradient)
