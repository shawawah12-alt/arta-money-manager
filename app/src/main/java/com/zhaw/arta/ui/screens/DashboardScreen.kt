package com.zhaw.arta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zhaw.arta.data.formatRupiah
import com.zhaw.arta.ui.components.CategoryDonut
import com.zhaw.arta.ui.components.WeeklyBars
import com.zhaw.arta.ui.theme.ChampagneGold
import com.zhaw.arta.ui.theme.Charcoal
import com.zhaw.arta.ui.theme.EmeraldIn
import com.zhaw.arta.ui.theme.GoldSoft
import com.zhaw.arta.ui.theme.Mist
import com.zhaw.arta.ui.theme.Obsidian
import com.zhaw.arta.ui.theme.RoseOut
import com.zhaw.arta.viewmodel.LedgerUiState

@Composable
fun DashboardScreen(state: LedgerUiState, contentPadding: androidx.compose.foundation.layout.PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Arta",
                    style = MaterialTheme.typography.displaySmall,
                    color = ChampagneGold,
                )
                Text(
                    text = "Kelola uangmu dengan tenang",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Mist,
                )
            }
        }

        item { BalanceCard(state) }

        item {
            SectionCard(title = "Pengeluaran per Kategori") {
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
                            for ((cat, amount) in state.byCategory.take(5)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(cat.color),
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = cat.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        Text(
                                            text = "${amount * 100 / total}% \u00B7 ${formatRupiah(amount)}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Mist,
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
            SectionCard(title = "Arus Kas 7 Hari") {
                if (state.transactions.isEmpty()) {
                    EmptyHint("Grafik mingguan akan muncul di sini.")
                } else {
                    WeeklyBars(days = state.last7Days, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        item {
            SectionCard(title = "Rasio Bulan Ini") {
                val total = (state.monthIncome + state.monthExpense).coerceAtLeast(1)
                val frac = state.monthExpense.toFloat() / total
                Column {
                    LinearProgressIndicator(
                        progress = { frac },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = RoseOut,
                        trackColor = EmeraldIn.copy(alpha = 0.35f),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (state.monthIncome == 0L && state.monthExpense == 0L)
                            "Belum ada aktivitas bulan ini."
                        else
                            "Pengeluaran ${state.monthExpense * 100 / total}% dari total arus kas bulan ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Mist,
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(state: LedgerUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(GoldSoft, ChampagneGold, Color(0xFFB08D3E)))
                )
                .padding(24.dp),
        ) {
            Text(
                text = "TOTAL SALDO",
                style = MaterialTheme.typography.labelMedium,
                color = Obsidian.copy(alpha = 0.7f),
                letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatRupiah(state.balance),
                style = MaterialTheme.typography.displaySmall,
                color = Obsidian,
            )
            Spacer(Modifier.height(18.dp))
            Row {
                FlowPill(
                    icon = { Icon(Icons.Rounded.ArrowDownward, null, tint = EmeraldIn, modifier = Modifier.size(16.dp)) },
                    label = "Masuk",
                    value = formatRupiah(state.monthIncome),
                )
                Spacer(Modifier.width(12.dp))
                FlowPill(
                    icon = { Icon(Icons.Rounded.ArrowUpward, null, tint = RoseOut, modifier = Modifier.size(16.dp)) },
                    label = "Keluar",
                    value = formatRupiah(state.monthExpense),
                )
            }
        }
    }
}

@Composable
private fun FlowPill(icon: @Composable () -> Unit, label: String, value: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Obsidian.copy(alpha = 0.22f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(Modifier.width(6.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Obsidian.copy(alpha = 0.7f))
            Text(value, style = MaterialTheme.typography.labelLarge, color = Obsidian, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun EmptyHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Mist,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
