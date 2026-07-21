package com.zhaw.arta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zhaw.arta.data.formatRupiah
import com.zhaw.arta.ui.components.AnimatedRupiah
import com.zhaw.arta.ui.components.CategoryDonut
import com.zhaw.arta.ui.components.WeeklyBars
import com.zhaw.arta.ui.components.entrance
import com.zhaw.arta.ui.components.pressable
import com.zhaw.arta.ui.theme.Arta
import com.zhaw.arta.ui.theme.ThemeMode
import com.zhaw.arta.viewmodel.LedgerUiState

// ============================================================
// Dashboard v2 — "quiet money"
// Saldo = hero typography langsung di canvas (kaya Copilot Money),
// bukan kartu gradient. Section = card putih flat dengan border
// hairline 1dp. Satu aksen warna, dipakai hemat.
// ============================================================

@Composable
fun DashboardScreen(
    state: LedgerUiState,
    themeMode: ThemeMode,
    onCycleTheme: () -> Unit,
    contentPadding: PaddingValues,
) {
    val t = Arta.tokens

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header: wordmark kecil + theme toggle ikon doang (nggak teriak-teriak)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .entrance(0),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Arta",
                    style = MaterialTheme.typography.titleLarge,
                    color = t.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .pressable(onCycleTheme)
                        .padding(8.dp),
                ) {
                    Icon(
                        imageVector = when (themeMode) {
                            ThemeMode.System -> Icons.Rounded.BrightnessAuto
                            ThemeMode.Light -> Icons.Rounded.LightMode
                            ThemeMode.Dark -> Icons.Rounded.DarkMode
                        },
                        contentDescription = "Ganti tema",
                        tint = t.textSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        // Hero: saldo langsung di canvas — typography yang kerja
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 4.dp)
                    .entrance(1),
            ) {
                Text(
                    text = "Total saldo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = t.textSecondary,
                )
                Spacer(Modifier.height(2.dp))
                AnimatedRupiah(
                    target = state.balance,
                    format = { formatRupiah(it) },
                    style = MaterialTheme.typography.displaySmall,
                    color = t.textPrimary,
                )
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InlineStat(label = "Masuk bulan ini", value = formatRupiah(state.monthIncome), color = t.positive)
                    Spacer(Modifier.width(24.dp))
                    InlineStat(label = "Keluar bulan ini", value = formatRupiah(state.monthExpense), color = t.negative)
                }
            }
        }

        item { Spacer(Modifier.height(4.dp)) }

        item {
            FlatSection(title = "Pengeluaran per kategori", entranceIndex = 2) {
                if (state.byCategory.isEmpty()) {
                    EmptyHint("Belum ada pengeluaran bulan ini.")
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CategoryDonut(
                            data = state.byCategory,
                            modifier = Modifier.size(132.dp),
                        )
                        Spacer(Modifier.width(20.dp))
                        Column {
                            val total = state.byCategory.sumOf { it.second }.coerceAtLeast(1)
                            state.byCategory.take(5).forEachIndexed { i, (cat, amount) ->
                                if (i > 0) HorizontalDivider(color = t.divider, modifier = Modifier.padding(vertical = 7.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(8.dp).clip(CircleShape).background(cat.color))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        cat.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = t.textPrimary,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text(
                                        "${amount * 100 / total}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = t.textSecondary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            FlatSection(title = "Arus kas 7 hari", entranceIndex = 3) {
                if (state.transactions.isEmpty()) {
                    EmptyHint("Grafik mingguan akan muncul di sini.")
                } else {
                    WeeklyBars(days = state.last7Days, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun InlineStat(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    val t = Arta.tokens
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = t.textFaint)
        Spacer(Modifier.height(1.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
    }
}

/** Section flat: card polos + border hairline 1dp. Tanpa glass, tanpa gradient. */
@Composable
fun FlatSection(title: String, entranceIndex: Int = 0, content: @Composable () -> Unit) {
    val t = Arta.tokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .entrance(entranceIndex)
            .clip(RoundedCornerShape(16.dp))
            .background(t.card)
            .border(1.dp, t.cardBorder, RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = t.textPrimary)
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
fun EmptyHint(text: String) {
    val t = Arta.tokens
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = t.textFaint,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
