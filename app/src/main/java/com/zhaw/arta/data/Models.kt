package com.zhaw.arta.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.MoreHoriz
import kotlinx.serialization.Serializable
import java.text.NumberFormat
import java.util.Locale

enum class TxType { Income, Expense }

enum class Category(
    val label: String,
    val color: Color,
    val icon: ImageVector,
    val type: TxType,
) {
    // Palet kategori v2 — muted/desaturated, satu keluarga tonal (bukan pelangi neon).
    Makanan("Makanan", Color(0xFFC77D4F), Icons.Rounded.Fastfood, TxType.Expense),
    Transportasi("Transportasi", Color(0xFF5E7FA3), Icons.Rounded.DirectionsBus, TxType.Expense),
    Belanja("Belanja", Color(0xFF8B6FA8), Icons.Rounded.ShoppingBag, TxType.Expense),
    Tagihan("Tagihan", Color(0xFF5A9490), Icons.Rounded.Bolt, TxType.Expense),
    Hiburan("Hiburan", Color(0xFFB5686C), Icons.Rounded.Movie, TxType.Expense),
    Kesehatan("Kesehatan", Color(0xFF6D9B7B), Icons.Rounded.MedicalServices, TxType.Expense),
    Pendidikan("Pendidikan", Color(0xFFA98F55), Icons.Rounded.School, TxType.Expense),
    LainnyaKeluar("Lainnya", Color(0xFF8A8D95), Icons.Rounded.MoreHoriz, TxType.Expense),
    Gaji("Gaji", Color(0xFF4E8C6F), Icons.Rounded.Payments, TxType.Income),
    Investasi("Investasi", Color(0xFFA98F55), Icons.Rounded.TrendingUp, TxType.Income),
    Hadiah("Hadiah", Color(0xFF8B6FA8), Icons.Rounded.CardGiftcard, TxType.Income),
    LainnyaMasuk("Lainnya", Color(0xFF5E7FA3), Icons.Rounded.MoreHoriz, TxType.Income);

    companion object {
        fun of(type: TxType) = entries.filter { it.type == type }
    }
}

@Serializable
data class Transaction(
    val id: Long,
    val amount: Long,          // dalam Rupiah utuh
    val category: String,      // Category.name
    val note: String,
    val epochMillis: Long,
    val type: String,          // TxType.name
) {
    val cat: Category get() = runCatching { Category.valueOf(category) }.getOrDefault(Category.LainnyaKeluar)
    val txType: TxType get() = runCatching { TxType.valueOf(type) }.getOrDefault(TxType.Expense)
}

private val idLocale = Locale.forLanguageTag("id-ID")

fun formatRupiah(amount: Long): String {
    val nf = NumberFormat.getNumberInstance(idLocale)
    return "Rp" + nf.format(amount)
}

fun formatRupiahSigned(t: Transaction): String =
    (if (t.txType == TxType.Income) "+" else "\u2212") + formatRupiah(t.amount)
