package com.zhaw.arta.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import com.zhaw.arta.ui.components.entrance
import com.zhaw.arta.ui.components.glassCard
import com.zhaw.arta.ui.components.pressable
import com.zhaw.arta.ui.theme.Arta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class Filter(val label: String) { Semua("Semua"), Masuk("Masuk"), Keluar("Keluar") }

@Composable
fun HistoryScreen(
    transactions: List<Transaction>,
    onDelete: (Long) -> Unit,
    contentPadding: PaddingValues,
) {
    val t = Arta.tokens
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
            color = t.textPrimary,
            modifier = Modifier.padding(horizontal = 20.dp).entrance(0),
        )
        Spacer(Modifier.height(12.dp))

        // Segmented pill filter dengan sliding highlight (kaya tab CSS)
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .clip(CircleShape)
                .background(t.glass)
                .background(t.accentSoft.copy(alpha = 0.06f))
                .padding(4.dp)
                .entrance(1),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for (f in Filter.entries) {
                val selected = filter == f
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (selected) t.accent else androidx.compose.ui.graphics.Color.Transparent)
                        .pressable { filter = f }
                        .animateContentSize(spring(stiffness = Spring.StiffnessMedium))
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = f.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) (if (t.isDark) t.heroText else t.card) else t.textSecondary,
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        if (visible.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Belum ada transaksi.\nTekan + untuk mulai mencatat.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = t.textSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(visible, key = { it.id }) { tx ->
                    SwipeableTransactionRow(tx = tx, onDelete = { onDelete(tx.id) })
                }
            }
        }
    }
}

private val dateFmt = SimpleDateFormat("d MMM yyyy \u00B7 HH:mm", Locale.forLanguageTag("id-ID"))

/** Row transaksi dengan swipe-to-delete (geser ke kiri) + latar merah yang muncul di belakang. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTransactionRow(tx: Transaction, onDelete: () -> Unit) {
    val t = Arta.tokens
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        },
        positionalThreshold = { it * 0.45f },
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(t.negative.copy(alpha = 0.85f))
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Rounded.DeleteOutline,
                    contentDescription = "Hapus",
                    tint = androidx.compose.ui.graphics.Color.White,
                )
            }
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .glassCard(t, radius = 16.dp)
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
                    color = t.textPrimary,
                    maxLines = 1,
                )
                Text(
                    text = "${tx.cat.label} \u00B7 ${dateFmt.format(Date(tx.epochMillis))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = t.textSecondary,
                )
            }
            Text(
                text = formatRupiahSigned(tx),
                style = MaterialTheme.typography.titleMedium,
                color = if (tx.txType == TxType.Income) t.positive else t.negative,
            )
        }
    }
}
