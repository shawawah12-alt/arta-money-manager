package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.agon.app.data.formatRupiah
import com.agon.app.ui.screens.AddTransactionSheet
import com.agon.app.ui.screens.DashboardScreen
import com.agon.app.ui.screens.HistoryScreen
import com.agon.app.ui.theme.AgonAppTheme
import com.agon.app.ui.theme.ChampagneGold
import com.agon.app.ui.theme.Charcoal
import com.agon.app.ui.theme.Mist
import com.agon.app.ui.theme.Obsidian
import com.agon.app.viewmodel.LedgerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AgonAppTheme {
                ArtaApp()
            }
        }
    }
}

private enum class Tab { Dashboard, History }

@Composable
fun ArtaApp(vm: LedgerViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var tab by remember { mutableStateOf(Tab.Dashboard) }
    var showSheet by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar(containerColor = Charcoal) {
                NavigationBarItem(
                    selected = tab == Tab.Dashboard,
                    onClick = { tab = Tab.Dashboard },
                    icon = { Icon(Icons.Rounded.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Obsidian,
                        indicatorColor = ChampagneGold,
                        selectedTextColor = ChampagneGold,
                        unselectedIconColor = Mist,
                        unselectedTextColor = Mist,
                    ),
                )
                NavigationBarItem(
                    selected = tab == Tab.History,
                    onClick = { tab = Tab.History },
                    icon = { Icon(Icons.Rounded.ReceiptLong, contentDescription = "Riwayat") },
                    label = { Text("Riwayat") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Obsidian,
                        indicatorColor = ChampagneGold,
                        selectedTextColor = ChampagneGold,
                        unselectedIconColor = Mist,
                        unselectedTextColor = Mist,
                    ),
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = ChampagneGold,
                contentColor = Obsidian,
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Tambah transaksi")
            }
        },
    ) { inner ->
        val ld = LocalLayoutDirection.current
        val padding = PaddingValues(
            start = inner.calculateStartPadding(ld),
            end = inner.calculateEndPadding(ld),
            top = inner.calculateTopPadding() + 12.dp,
            bottom = inner.calculateBottomPadding() + 12.dp,
        )

        when (tab) {
            Tab.Dashboard -> DashboardScreen(state = state, contentPadding = padding)
            Tab.History -> HistoryScreen(
                transactions = state.transactions,
                onDelete = { id ->
                    vm.delete(id)
                    scope.launch { snackbar.showSnackbar("Transaksi dihapus") }
                },
                contentPadding = padding,
            )
        }

        if (showSheet) {
            AddTransactionSheet(
                onDismiss = { showSheet = false },
                onSave = { amount, cat, note, type ->
                    vm.add(amount, cat, note, type)
                    scope.launch { snackbar.showSnackbar("Tersimpan: ${formatRupiah(amount)}") }
                },
            )
        }
    }
}
