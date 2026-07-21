package com.zhaw.arta.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.zhaw.arta.data.Category
import com.zhaw.arta.data.formatRupiah
import com.zhaw.arta.ui.theme.Arta
import com.zhaw.arta.viewmodel.DaySpend
import kotlin.math.max

/** Donut chart dengan sweep animasi easing + rounded caps per segmen. */
@Composable
fun CategoryDonut(
    data: List<Pair<Category, Long>>,
    modifier: Modifier = Modifier,
) {
    val t = Arta.tokens
    val total = data.sumOf { it.second }.coerceAtLeast(1)
    val sweep = remember { Animatable(0f) }
    LaunchedEffect(data) {
        sweep.snapTo(0f)
        sweep.animateTo(1f, tween(1000, easing = EaseOutCubic))
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = size.minDimension * 0.14f
            val inset = stroke / 2 + 4.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)

            // track halus di belakang (kaya CSS ring track)
            drawArc(
                color = t.divider,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(width = stroke * 0.55f, cap = StrokeCap.Round),
            )

            if (data.isNotEmpty()) {
                var start = -90f
                for ((cat, amount) in data) {
                    val full = 360f * (amount.toFloat() / total)
                    val angle = full * sweep.value
                    drawArc(
                        color = cat.color,
                        startAngle = start,
                        sweepAngle = (angle - 3f).coerceAtLeast(0.5f),
                        useCenter = false,
                        topLeft = topLeft, size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                    start += full * sweep.value
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Bulan ini",
                style = MaterialTheme.typography.labelMedium,
                color = t.textSecondary,
            )
            AnimatedRupiah(
                target = data.sumOf { it.second },
                format = { formatRupiah(it) },
                style = MaterialTheme.typography.titleLarge,
                color = t.textPrimary,
            )
        }
    }
}

/** Bar chart 7 hari: batang tumbuh berurutan dengan spring + nilai maks di-highlight. */
@Composable
fun WeeklyBars(
    days: List<DaySpend>,
    modifier: Modifier = Modifier,
) {
    val t = Arta.tokens
    val maxVal = max(days.maxOfOrNull { max(it.expense, it.income) } ?: 0L, 1L)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            days.forEachIndexed { i, d ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        GrowingBar(frac = d.income.toFloat() / maxVal, color = t.positive, delayMs = i * 60)
                        GrowingBar(frac = d.expense.toFloat() / maxVal, color = t.negative, delayMs = i * 60 + 30)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = d.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (i == days.lastIndex) t.accent else t.textSecondary,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LegendDot(t.positive, "Masuk")
            Spacer(Modifier.width(16.dp))
            LegendDot(t.negative, "Keluar")
        }
    }
}

@Composable
private fun GrowingBar(frac: Float, color: Color, delayMs: Int) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(frac) {
        anim.animateTo(frac.coerceIn(0f, 1f), tween(600, delayMillis = delayMs, easing = EaseOutCubic))
    }
    val h = (104.dp * anim.value).coerceAtLeast(3.dp)
    Box(
        modifier = Modifier
            .width(9.dp)
            .height(h)
            .background(color, RoundedCornerShape(4.dp)),
    )
}

@Composable
private fun LegendDot(color: Color, label: String) {
    val t = Arta.tokens
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).background(color, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = t.textSecondary)
    }
}
