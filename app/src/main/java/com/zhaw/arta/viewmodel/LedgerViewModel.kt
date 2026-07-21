package com.zhaw.arta.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zhaw.arta.data.Category
import com.zhaw.arta.data.Transaction
import com.zhaw.arta.data.TransactionStore
import com.zhaw.arta.data.TxType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DaySpend(val label: String, val expense: Long, val income: Long)

data class LedgerUiState(
    val transactions: List<Transaction> = emptyList(),
    val balance: Long = 0,
    val monthIncome: Long = 0,
    val monthExpense: Long = 0,
    val byCategory: List<Pair<Category, Long>> = emptyList(),
    val last7Days: List<DaySpend> = emptyList(),
)

class LedgerViewModel(app: Application) : AndroidViewModel(app) {

    private val store = TransactionStore(app)

    val state: StateFlow<LedgerUiState> = store.transactions
        .map { list -> buildState(list.sortedByDescending { it.epochMillis }) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LedgerUiState())

    fun add(amount: Long, category: Category, note: String, type: TxType) {
        viewModelScope.launch {
            store.add(
                Transaction(
                    id = System.currentTimeMillis(),
                    amount = amount,
                    category = category.name,
                    note = note.trim(),
                    epochMillis = System.currentTimeMillis(),
                    type = type.name,
                )
            )
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch { store.delete(id) }
    }

    private fun buildState(list: List<Transaction>): LedgerUiState {
        val now = Calendar.getInstance()
        val thisMonth = now.get(Calendar.MONTH) to now.get(Calendar.YEAR)

        fun Transaction.isThisMonth(): Boolean {
            val c = Calendar.getInstance().apply { timeInMillis = epochMillis }
            return (c.get(Calendar.MONTH) to c.get(Calendar.YEAR)) == thisMonth
        }

        val income = list.filter { it.txType == TxType.Income }.sumOf { it.amount }
        val expense = list.filter { it.txType == TxType.Expense }.sumOf { it.amount }
        val monthIn = list.filter { it.txType == TxType.Income && it.isThisMonth() }.sumOf { it.amount }
        val monthOut = list.filter { it.txType == TxType.Expense && it.isThisMonth() }.sumOf { it.amount }

        val byCat = list
            .filter { it.txType == TxType.Expense && it.isThisMonth() }
            .groupBy { it.cat }
            .map { (cat, txs) -> cat to txs.sumOf { it.amount } }
            .sortedByDescending { it.second }

        val dayLabels = arrayOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
        val days = (6 downTo 0).map { back ->
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -back) }
            val dayStart = (c.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val dayEnd = dayStart + 24L * 60 * 60 * 1000
            val inDay = list.filter { it.epochMillis in dayStart until dayEnd }
            DaySpend(
                label = dayLabels[c.get(Calendar.DAY_OF_WEEK) - 1],
                expense = inDay.filter { it.txType == TxType.Expense }.sumOf { it.amount },
                income = inDay.filter { it.txType == TxType.Income }.sumOf { it.amount },
            )
        }

        return LedgerUiState(
            transactions = list,
            balance = income - expense,
            monthIncome = monthIn,
            monthExpense = monthOut,
            byCategory = byCat,
            last7Days = days,
        )
    }
}
