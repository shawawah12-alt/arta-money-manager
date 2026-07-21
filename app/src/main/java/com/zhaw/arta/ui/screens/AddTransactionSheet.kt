package com.zhaw.arta.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zhaw.arta.data.Category
import com.zhaw.arta.data.TxType
import com.zhaw.arta.ui.components.popIn
import com.zhaw.arta.ui.components.pressable
import com.zhaw.arta.ui.theme.Arta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    onDismiss: () -> Unit,
    onSave: (amount: Long, category: Category, note: String, type: TxType) -> Unit,
) {
    val t = Arta.tokens
    var type by remember { mutableStateOf(TxType.Expense) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf<Category?>(null) }

    val cats = Category.of(type)
    val amount = amountText.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val valid = amount > 0 && selectedCat != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = t.card,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = "Catat Transaksi",
                style = MaterialTheme.typography.titleLarge,
                color = t.textPrimary,
            )
            Spacer(Modifier.height(16.dp))

            // Segmented switcher custom dengan animasi warna
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(t.glass)
                    .background(t.accentSoft.copy(alpha = 0.07f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                TypeTab("Pengeluaran", type == TxType.Expense, Modifier.weight(1f)) {
                    type = TxType.Expense; selectedCat = null
                }
                TypeTab("Pemasukan", type == TxType.Income, Modifier.weight(1f)) {
                    type = TxType.Income; selectedCat = null
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { input -> amountText = input.filter { it.isDigit() }.take(12) },
                label = { Text("Nominal (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = t.accent,
                    focusedLabelColor = t.accent,
                    cursorColor = t.accent,
                    unfocusedBorderColor = t.cardBorder,
                ),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it.take(60) },
                label = { Text("Catatan (opsional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = t.accent,
                    focusedLabelColor = t.accent,
                    cursorColor = t.accent,
                    unfocusedBorderColor = t.cardBorder,
                ),
            )

            Spacer(Modifier.height(16.dp))
            Text("Kategori", style = MaterialTheme.typography.labelLarge, color = t.textSecondary)
            Spacer(Modifier.height(10.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (cats.size > 4) 176.dp else 88.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(cats) { cat ->
                    val selected = cat == selectedCat
                    val bg by animateColorAsState(
                        targetValue = if (selected) cat.color.copy(alpha = 0.22f) else t.glass,
                        animationSpec = tween(200),
                        label = "catBg",
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(bg)
                            .pressable { selectedCat = cat }
                            .padding(vertical = 10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(cat.color.copy(alpha = if (selected) 1f else 0.25f))
                                .then(if (selected) Modifier.popIn() else Modifier),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.label,
                                tint = if (selected) Color.White else cat.color,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = cat.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) t.textPrimary else t.textSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val cat = selectedCat ?: return@Button
                    onSave(amount, cat, note, type)
                    onDismiss()
                },
                enabled = valid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = t.accent,
                    contentColor = if (t.isDark) t.heroText else t.card,
                ),
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun TypeTab(label: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val t = Arta.tokens
    val bg by animateColorAsState(
        targetValue = if (selected) t.accent else Color.Transparent,
        animationSpec = tween(220),
        label = "tabBg",
    )
    val fg by animateColorAsState(
        targetValue = if (selected) (if (t.isDark) t.heroText else t.card) else t.textSecondary,
        animationSpec = tween(220),
        label = "tabFg",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(bg)
            .pressable(onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = fg)
    }
}
