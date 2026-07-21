package com.zhaw.arta.ui.components

// =============================================================
//  ARTA UI FOUNDATION — "CSS-in-Kotlin"
//  Modifier & komponen reusable ala styled-components / Tailwind:
//  glass card, pressable spring scale, shimmer, animated counter,
//  staggered entrance, gradient canvas.
// =============================================================

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import com.zhaw.arta.ui.theme.Arta
import com.zhaw.arta.ui.theme.ArtaTokens
import androidx.compose.material3.Text
import kotlin.math.roundToInt
import kotlin.math.roundToLong

// ---------- Gradient page canvas (kaya body { background: linear-gradient }) ----------
@Composable
fun GradientCanvas(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val t = Arta.tokens
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(t.canvasGradient)),
        content = content,
    )
}

// ---------- Glass card: blur-ish translucent + border tipis (glassmorphism) ----------
fun Modifier.glassCard(tokens: ArtaTokens, radius: Dp = 20.dp): Modifier =
    this
        .clip(RoundedCornerShape(radius))
        .background(tokens.card)
        .background(tokens.glass)
        .border(1.dp, tokens.cardBorder, RoundedCornerShape(radius))

// ---------- Pressable: spring scale-down saat ditekan (micro-interaction) ----------
fun Modifier.pressable(onClick: () -> Unit): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "pressScale",
    )
    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .clickable(interactionSource = interaction, indication = null, onClick = onClick)
}

// ---------- Shimmer sweep (kaya CSS keyframes background-position) ----------
fun Modifier.shimmer(tokens: ArtaTokens, enabled: Boolean = true): Modifier = composed {
    if (!enabled) return@composed this
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmerX",
    )
    drawBehind {
        val w = size.width
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, tokens.shimmerHighlight.copy(alpha = 0.55f), Color.Transparent),
                start = Offset(w * progress, 0f),
                end = Offset(w * (progress + 0.6f), size.height),
            )
        )
    }
}

// ---------- Staggered entrance: fade + slide-up berurutan (kaya framer-motion) ----------
fun Modifier.entrance(index: Int, baseDelay: Int = 70): Modifier = composed {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val alpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 420, delayMillis = index * baseDelay, easing = EaseOutCubic),
        label = "entranceAlpha",
    )
    val slide by animateFloatAsState(
        targetValue = if (started) 0f else 40f,
        animationSpec = tween(durationMillis = 480, delayMillis = index * baseDelay, easing = EaseOutCubic),
        label = "entranceSlide",
    )
    this
        .alpha(alpha)
        .offsetY { slide }
}

private fun Modifier.offsetY(y: () -> Float): Modifier =
    this.then(Modifier.composed {
        Modifier.graphicsLayer { translationY = y() * density }
    })

// ---------- Animated number counter (count-up kaya react-countup) ----------
@Composable
fun AnimatedRupiah(
    target: Long,
    format: (Long) -> String,
    style: TextStyle,
    color: Color,
) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(target) {
        anim.animateTo(
            targetValue = target.toFloat(),
            animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        )
    }
    Text(
        text = format(anim.value.roundToLong()),
        style = style,
        color = color,
    )
}

// ---------- Bouncy pop-in utk elemen kecil (badge, ikon) ----------
fun Modifier.popIn(delayMs: Int = 0): Modifier = composed {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val scale by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "popIn",
    )
    graphicsLayer { scaleX = scale; scaleY = scale }
}
