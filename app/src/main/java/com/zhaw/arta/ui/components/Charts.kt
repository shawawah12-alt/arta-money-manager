package com.zhaw.arta.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zhaw.arta.data.Category
import com.zhaw.arta.data.formatRupiah
import com.zhaw.arta.viewmodel.DaySpend
import com.zhaw.arta.ui.theme.EmeraldIn
import com.zhaw.arta.ui.theme.Mist
import com.zhaw.arta.ui.theme.RoseOut
import com.zhaw.arta.ui.theme.SlateHigh
import kotlin.math.max

/** Donut chart pengeluaran per kategori dengan animasi sweep. */
@Composable
fun CategoryDonut(
    data: List<Pair<Category, Long>>,
    modifier: Modifier = Modifier,
) {
    val total = data.sumOf { it.second }.coerceAtLeast(1)
    val sweep by animateFloatAsState(
        targetValue = if (data.isEmpty()) 0f else 1f,
        animationSpec = tween(900),
        label = "donutSweep",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = size.minDimension * 0.13f
            val inset = stroke / 2 + 4.dp.toPx()
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
            val topLeft = Offset(inset, inset)

            if (data.isEmpty()) {
                drawArc(
                    color = SlateHigh,
                    startAngle = 0f, sweepAngle = 360f, useCenter = false,
                    topLeft = topLeft, size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                )
            } else {
                var start = -90f
                for ((cat, amount) in data) {
                    val angle = 360f * (amount.toFloat() / total) * sweep
                    drawArc(
                        color = cat.color,
                        startAngle = start,
                        sweepAngle = (angle - 2f).coerceAtLeast(0.5f),
                        useCenter = false,
                        topLeft = topLeft, size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Butt),
                    )
                    start += angle
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Bulan ini",
                style = MaterialTheme.typography.labelMedium,
                color = Mist,
            )
            Text(
                text = formatRupiah(data.sumOf { it.second }),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/** Grouped bar chart 7 hari terakhir: hijau = masuk, merah = keluar. */
@Composable
fun WeeklyBars(
    days: List<DaySpend>,
    modifier: Modifier = Modifier,
) {
    val maxVal = max(days.maxOfOrNull { max(it.expense, it.income) } ?: 0L, 1L)
    val grow by animateFloatAsState(targetValue = 1f, animationSpec = tween(800), label = "barGrow")

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            for (d in days) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Bar(heightFrac = d.income.toFloat() / maxVal * grow, color = EmeraldIn)
                        Bar(heightFrac = d.expense.toFloat() / maxVal * grow, color = RoseOut)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = d.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = Mist,
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LegendDot(EmeraldIn, "Masuk")
            Spacer(Modifier.width(16.dp))
            LegendDot(RoseOut, "Keluar")
        }
    }
}

@Composable
private fun Bar(heightFrac: Float, color: Color) {
    val h = (104.dp * heightFrac.coerceIn(0f, 1f)).coerceAtLeast(3.dp)
    Box(
        modifier = Modifier
            .width(9.dp)
            .height(h)
            .background(color = color, shape = MaterialTheme.shapes.small),
    )
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(color = color, shape = MaterialTheme.shapes.small),
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = Mist)
    }
}

