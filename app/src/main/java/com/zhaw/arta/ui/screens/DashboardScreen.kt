package com.zhaw.arta.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zhaw.arta.data.formatRupiah
import com.zhaw.arta.ui.components.AnimatedRupiah
import com.zhaw.arta.ui.components.CategoryDonut
import com.zhaw.arta.ui.components.WeeklyBars
import com.zhaw.arta.ui.components.entrance
import com.zhaw.arta.ui.components.glassCard
import com.zhaw.arta.ui.components.popIn
import com.zhaw.arta.ui.components.pressable
import com.zhaw.arta.ui.theme.Arta
import com.zhaw.arta.ui.theme.ThemeMode
import com.zhaw.arta.ui.theme.heroBrush
import com.zhaw.arta.viewmodel.LedgerUiState

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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .entrance(0),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Arta", style = MaterialTheme.typography.displaySmall, color = t.accent)
                    Text(
                        "Kelola uangmu dengan tenang",
                        style = MaterialTheme.typography.bodyMedium,
                        color = t.textSecondary,
                    )
                }
                // Theme switcher pill — System / Light / Dark
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(t.accentSoft)
                        .pressable(onCycleTheme)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = when (themeMode) {
                            ThemeMode.System -> Icons.Rounded.BrightnessAuto
                            ThemeMode.Light -> Icons.Rounded.LightMode
                            ThemeMode.Dark -> Icons.Rounded.DarkMode
                        },
                        contentDescription = "Ganti tema",
                        tint = t.accent,
                        modifier = Modifier.size(17.dp).popIn(),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = when (themeMode) {
                            ThemeMode.System -> "Auto"
                            ThemeMode.Light -> "Terang"
                            ThemeMode.Dark -> "Gelap"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = t.accent,
                    )
                }
            }
        }

        item { Box(Modifier.entrance(1)) { BalanceCard(state) } }

        item {
            SectionCard(title = "Pengeluaran per Kategori", entranceIndex = 2) {
                if (state.byCategory.isEmpty()) {
                    EmptyHint("Belum ada pengeluaran bulan ini.\nTekan + untuk mencatat transaksi pertamamu.")
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CategoryDonut(
                            data = state.byCategory,
                            modifier = Modifier.size(150.dp),
                        )
                        Spacer(Modifier.width(18.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val total = state.byCategory.sumOf { it.second }.coerceAtLeast(1)
                            state.byCategory.take(5).forEachIndexed { i, (cat, amount) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.entrance(i + 3, baseDelay = 60),
                                ) {
                                    Box(Modifier.size(10.dp).clip(CircleShape).background(cat.color))
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(cat.label, style = MaterialTheme.typography.labelMedium, color = t.textPrimary)
                                        Text(
                                            "${amount * 100 / total}% \u00B7 ${formatRupiah(amount)}",
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
        }

        item {
            SectionCard(title = "Arus Kas 7 Hari", entranceIndex = 3) {
                if (state.transactions.isEmpty()) {
                    EmptyHint("Grafik mingguan akan muncul di sini.")
                } else {
                    WeeklyBars(days = state.last7Days, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        item {
            SectionCard(title = "Rasio Bulan Ini", entranceIndex = 4) {
                val total = (state.monthIncome + state.monthExpense).coerceAtLeast(1)
                val frac = state.monthExpense.toFloat() / total
                Column {
                    LinearProgressIndicator(
                        progress = { frac },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = t.negative,
                        trackColor = t.positive.copy(alpha = 0.35f),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (state.monthIncome == 0L && state.monthExpense == 0L)
                            "Belum ada aktivitas bulan ini."
                        else
                            "Pengeluaran ${state.monthExpense * 100 / total}% dari total arus kas bulan ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = t.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(state: LedgerUiState) {
    val t = Arta.tokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(heroBrush(t))
            .padding(24.dp),
    ) {
        Text(
            text = "TOTAL SALDO",
            style = MaterialTheme.typography.labelMedium,
            color = t.heroText.copy(alpha = 0.65f),
        )
        Spacer(Modifier.height(4.dp))
        AnimatedRupiah(
            target = state.balance,
            format = { formatRupiah(it) },
            style = MaterialTheme.typography.displaySmall,
            color = t.heroText,
        )
        Spacer(Modifier.height(18.dp))
        Row {
            FlowPill(
                icon = { Icon(Icons.Rounded.ArrowDownward, null, tint = t.positive, modifier = Modifier.size(16.dp)) },
                label = "Masuk",
                value = formatRupiah(state.monthIncome),
            )
            Spacer(Modifier.width(12.dp))
            FlowPill(
                icon = { Icon(Icons.Rounded.ArrowUpward, null, tint = t.negative, modifier = Modifier.size(16.dp)) },
                label = "Keluar",
                value = formatRupiah(state.monthExpense),
            )
        }
    }
}

@Composable
private fun FlowPill(icon: @Composable () -> Unit, label: String, value: String) {
    val t = Arta.tokens
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(t.pillOnHero)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = t.heroText.copy(alpha = 0.65f))
            Text(value, style = MaterialTheme.typography.labelLarge, color = t.heroText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SectionCard(title: String, entranceIndex: Int = 0, content: @Composable () -> Unit) {
    val t = Arta.tokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .entrance(entranceIndex)
            .glassCard(t)
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
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = t.textSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
