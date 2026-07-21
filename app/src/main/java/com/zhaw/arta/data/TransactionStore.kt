package com.zhaw.arta.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.artaDataStore by preferencesDataStore(name = "arta_ledger")

class TransactionStore(private val context: Context) {

    private val ledgerKey = stringPreferencesKey("ledger_json")
    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = ListSerializer(Transaction.serializer())

    val transactions: Flow<List<Transaction>> = context.artaDataStore.data.map { prefs ->
        prefs[ledgerKey]?.let {
            runCatching { json.decodeFromString(serializer, it) }.getOrDefault(emptyList())
        } ?: emptyList()
    }

    suspend fun add(tx: Transaction) = mutate { it + tx }

    suspend fun delete(id: Long) = mutate { list -> list.filterNot { it.id == id } }

    private suspend fun mutate(block: (List<Transaction>) -> List<Transaction>) {
        context.artaDataStore.edit { prefs ->
            val current = prefs[ledgerKey]?.let {
                runCatching { json.decodeFromString(serializer, it) }.getOrDefault(emptyList())
            } ?: emptyList()
            prefs[ledgerKey] = json.encodeToString(serializer, block(current))
        }
    }
}
