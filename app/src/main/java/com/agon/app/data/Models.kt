package com.agon.app.data

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
import com.agon.app.ui.theme.Amethyst
import com.agon.app.ui.theme.Azure
import com.agon.app.ui.theme.ChampagneGold
import com.agon.app.ui.theme.Coral
import com.agon.app.ui.theme.EmeraldIn
import com.agon.app.ui.theme.Mist
import com.agon.app.ui.theme.RoseOut
import com.agon.app.ui.theme.Teal
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
    Makanan("Makanan", Coral, Icons.Rounded.Fastfood, TxType.Expense),
    Transportasi("Transportasi", Azure, Icons.Rounded.DirectionsBus, TxType.Expense),
    Belanja("Belanja", Amethyst, Icons.Rounded.ShoppingBag, TxType.Expense),
    Tagihan("Tagihan", Teal, Icons.Rounded.Bolt, TxType.Expense),
    Hiburan("Hiburan", RoseOut, Icons.Rounded.Movie, TxType.Expense),
    Kesehatan("Kesehatan", EmeraldIn, Icons.Rounded.MedicalServices, TxType.Expense),
    Pendidikan("Pendidikan", ChampagneGold, Icons.Rounded.School, TxType.Expense),
    LainnyaKeluar("Lainnya", Mist, Icons.Rounded.MoreHoriz, TxType.Expense),
    Gaji("Gaji", EmeraldIn, Icons.Rounded.Payments, TxType.Income),
    Investasi("Investasi", ChampagneGold, Icons.Rounded.TrendingUp, TxType.Income),
    Hadiah("Hadiah", Amethyst, Icons.Rounded.CardGiftcard, TxType.Income),
    LainnyaMasuk("Lainnya", Azure, Icons.Rounded.MoreHoriz, TxType.Income);

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
