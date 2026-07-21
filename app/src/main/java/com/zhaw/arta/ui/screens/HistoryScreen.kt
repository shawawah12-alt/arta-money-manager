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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zhaw.arta.data.Transaction
import com.zhaw.arta.data.TxType
import com.zhaw.arta.data.formatRupiahSigned
import com.zhaw.arta.ui.theme.ChampagneGold
import com.zhaw.arta.ui.theme.Charcoal
import com.zhaw.arta.ui.theme.EmeraldIn
import com.zhaw.arta.ui.theme.Mist
import com.zhaw.arta.ui.theme.Obsidian
import com.zhaw.arta.ui.theme.RoseOut
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class Filter(val label: String) { Semua("Semua"), Masuk("Masuk"), Keluar("Keluar") }

@Composable
fun HistoryScreen(
    transactions: List<Transaction>,
    onDelete: (Long) -> Unit,
    contentPadding: androidx.compose.foundation.layout.PaddingValues,
) {
    var filter by remember { mutableStateOf(Filter.Semua) }

    val visible = when (filter) {
        Filter.Semua -> transactions
        Filter.Masuk -> transactions.filter { it.txType == TxType.Income }
        Filter.Keluar -> transactions.filter { it.txType == TxType.Expense }
    }

    Column(modifier = Modifier.fillMaxSize().padding(contentPadding)) {
        Text(
            text = "Riwayat",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (f in Filter.entries) {
                FilterChip(
                    selected = filter == f,
                    onClick = { filter = f },
                    label = { Text(f.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChampagneGold,
                        selectedLabelColor = Obsidian,
                    ),
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        if (visible.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Belum ada transaksi.\nTekan + untuk mulai mencatat.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Mist,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(visible, key = { it.id }) { tx ->
                    TransactionRow(tx = tx, onDelete = { onDelete(tx.id) })
                }
            }
        }
    }
}

private val dateFmt = SimpleDateFormat("d MMM yyyy \u00B7 HH:mm", Locale.forLanguageTag("id-ID"))

@Composable
fun TransactionRow(tx: Transaction, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(tx.cat.color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = tx.cat.icon,
                    contentDescription = tx.cat.label,
                    tint = tx.cat.color,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.note.ifBlank { tx.cat.label },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                )
                Text(
                    text = "${tx.cat.label} \u00B7 ${dateFmt.format(Date(tx.epochMillis))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Mist,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatRupiahSigned(tx),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (tx.txType == TxType.Income) EmeraldIn else RoseOut,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Hapus",
                    tint = Mist,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
