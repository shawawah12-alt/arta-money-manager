package com.zhaw.arta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zhaw.arta.data.Category
import com.zhaw.arta.data.TxType
import com.zhaw.arta.ui.theme.ChampagneGold
import com.zhaw.arta.ui.theme.Charcoal
import com.zhaw.arta.ui.theme.Mist
import com.zhaw.arta.ui.theme.Obsidian
import com.zhaw.arta.ui.theme.SlateHigh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    onDismiss: () -> Unit,
    onSave: (amount: Long, category: Category, note: String, type: TxType) -> Unit,
) {
    var type by remember { mutableStateOf(TxType.Expense) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf<Category?>(null) }

    val cats = Category.of(type)
    val amount = amountText.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val valid = amount > 0 && selectedCat != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Charcoal,
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
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = type == TxType.Expense,
                    onClick = { type = TxType.Expense; selectedCat = null },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = ChampagneGold,
                        activeContentColor = Obsidian,
                    ),
                ) { Text("Pengeluaran") }
                SegmentedButton(
                    selected = type == TxType.Income,
                    onClick = { type = TxType.Income; selectedCat = null },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = ChampagneGold,
                        activeContentColor = Obsidian,
                    ),
                ) { Text("Pemasukan") }
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
                    focusedBorderColor = ChampagneGold,
                    focusedLabelColor = ChampagneGold,
                    cursorColor = ChampagneGold,
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
                    focusedBorderColor = ChampagneGold,
                    focusedLabelColor = ChampagneGold,
                    cursorColor = ChampagneGold,
                ),
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Kategori",
                style = MaterialTheme.typography.labelLarge,
                color = Mist,
            )
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) cat.color.copy(alpha = 0.22f) else SlateHigh.copy(alpha = 0.4f))
                            .clickable { selectedCat = cat }
                            .padding(vertical = 10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(cat.color.copy(alpha = if (selected) 1f else 0.25f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.label,
                                tint = if (selected) Obsidian else cat.color,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = cat.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) MaterialTheme.colorScheme.onSurface else Mist,
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
                    containerColor = ChampagneGold,
                    contentColor = Obsidian,
                ),
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
