package com.zhaw.arta.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ===== Brand =====
val ChampagneGold = Color(0xFFD9B45B)
val GoldSoft = Color(0xFFEACB85)
val GoldDeep = Color(0xFFB08D3E)
val EmeraldIn = Color(0xFF34B676)
val RoseOut = Color(0xFFE5484D)
val Azure = Color(0xFF5B9BD9)
val Amethyst = Color(0xFFA78BFA)
val Coral = Color(0xFFF08C5A)
val Teal = Color(0xFF3FBFB4)

// ===== Extended design tokens (kaya CSS custom properties) =====
// Ini yang bikin theming fleksibel: tiap mode punya token lengkap,
// bukan cuma colorScheme bawaan Material.
data class ArtaTokens(
    val isDark: Boolean,
    val canvas: Color,            // background paling bawah
    val canvasGradient: List<Color>, // gradient halus di belakang konten
    val card: Color,              // surface kartu
    val cardBorder: Color,        // border 1dp ala CSS border
    val glass: Color,             // glassmorphism overlay
    val textPrimary: Color,
    val textSecondary: Color,
    val textFaint: Color,
    val divider: Color,
    val shimmerBase: Color,
    val shimmerHighlight: Color,
    val navBar: Color,
    val heroGradient: List<Color>, // kartu saldo
    val heroText: Color,
    val pillOnHero: Color,
    val positive: Color,
    val negative: Color,
    val accent: Color,
    val accentSoft: Color,        // accent dengan alpha utk bg chip/badge
)

val DarkTokens = ArtaTokens(
    isDark = true,
    canvas = Color(0xFF0D0F14),
    canvasGradient = listOf(Color(0xFF12151D), Color(0xFF0D0F14), Color(0xFF0E1018)),
    card = Color(0xFF171B24),
    cardBorder = Color(0xFF262C3A),
    glass = Color(0xFFFFFFFF).copy(alpha = 0.045f),
    textPrimary = Color(0xFFF4F1E9),
    textSecondary = Color(0xFF9AA3B2),
    textFaint = Color(0xFF5C6472),
    divider = Color(0xFF232936),
    shimmerBase = Color(0xFF1E232E),
    shimmerHighlight = Color(0xFF2C3342),
    navBar = Color(0xFF12151D),
    heroGradient = listOf(Color(0xFFEACB85), Color(0xFFD9B45B), Color(0xFF9C7C36)),
    heroText = Color(0xFF14110A),
    pillOnHero = Color(0xFF14110A).copy(alpha = 0.18f),
    positive = EmeraldIn,
    negative = RoseOut,
    accent = ChampagneGold,
    accentSoft = ChampagneGold.copy(alpha = 0.16f),
)

val LightTokens = ArtaTokens(
    isDark = false,
    canvas = Color(0xFFF7F5F0),
    canvasGradient = listOf(Color(0xFFFDFBF6), Color(0xFFF7F5F0), Color(0xFFF2EEE5)),
    card = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFE8E3D7),
    glass = Color(0xFF14110A).copy(alpha = 0.03f),
    textPrimary = Color(0xFF1C1917),
    textSecondary = Color(0xFF6B7280),
    textFaint = Color(0xFFA8ADB8),
    divider = Color(0xFFECE8DE),
    shimmerBase = Color(0xFFEDEAE2),
    shimmerHighlight = Color(0xFFFAF8F2),
    navBar = Color(0xFFFFFFFF),
    heroGradient = listOf(Color(0xFF2A2415), Color(0xFF3D3418), Color(0xFF1F1B10)),
    heroText = Color(0xFFF6EBCF),
    pillOnHero = Color(0xFFF6EBCF).copy(alpha = 0.14f),
    positive = Color(0xFF14915B),
    negative = Color(0xFFD03A3F),
    accent = Color(0xFFA07E2E),
    accentSoft = Color(0xFFA07E2E).copy(alpha = 0.13f),
)

val LocalArtaTokens = staticCompositionLocalOf { DarkTokens }

/** Akses token di composable manapun: `Arta.card`, `Arta.textPrimary`, dst. */
object Arta {
    val tokens: ArtaTokens
        @Composable @ReadOnlyComposable get() = LocalArtaTokens.current
}

fun heroBrush(tokens: ArtaTokens): Brush = Brush.linearGradient(tokens.heroGradient)
