package com.zhaw.arta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhaw.arta.data.SettingsStore
import com.zhaw.arta.data.formatRupiah
import com.zhaw.arta.ui.components.GradientCanvas
import com.zhaw.arta.ui.screens.AddTransactionSheet
import com.zhaw.arta.ui.screens.DashboardScreen
import com.zhaw.arta.ui.screens.HistoryScreen
import com.zhaw.arta.ui.theme.Arta
import com.zhaw.arta.ui.theme.ArtaTheme
import com.zhaw.arta.ui.theme.ThemeMode
import com.zhaw.arta.viewmodel.LedgerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val settings = remember { SettingsStore(context) }
            val scope = rememberCoroutineScope()
            var mode by remember { mutableStateOf(ThemeMode.System) }
            LaunchedEffect(Unit) { settings.themeMode.collect { mode = it } }

            ArtaTheme(mode = mode) {
                ArtaApp(
                    themeMode = mode,
                    onCycleTheme = {
                        val next = when (mode) {
                            ThemeMode.System -> ThemeMode.Light
                            ThemeMode.Light -> ThemeMode.Dark
                            ThemeMode.Dark -> ThemeMode.System
                        }
                        scope.launch { settings.setThemeMode(next) }
                    },
                )
            }
        }
    }
}

private enum class Tab { Dashboard, History }

@Composable
fun ArtaApp(
    themeMode: ThemeMode,
    onCycleTheme: () -> Unit,
    vm: LedgerViewModel = viewModel(),
) {
    val t = Arta.tokens
    val state by vm.state.collectAsState()
    var tab by remember { mutableStateOf(Tab.Dashboard) }
    var showSheet by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar(containerColor = t.navBar) {
                NavigationBarItem(
                    selected = tab == Tab.Dashboard,
                    onClick = { tab = Tab.Dashboard },
                    icon = { Icon(Icons.Rounded.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    colors = navColors(),
                )
                NavigationBarItem(
                    selected = tab == Tab.History,
                    onClick = { tab = Tab.History },
                    icon = { Icon(Icons.Rounded.ReceiptLong, contentDescription = "Riwayat") },
                    label = { Text("Riwayat") },
                    colors = navColors(),
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = t.accent,
                contentColor = if (t.isDark) t.heroText else t.card,
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

        GradientCanvas {
            // Transisi antar-tab: slide + fade kaya page transition di React Router
            AnimatedContent(
                targetState = tab,
                transitionSpec = {
                    val dir = if (targetState.ordinal > initialState.ordinal) 1 else -1
                    (slideInHorizontally { it / 6 * dir } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it / 6 * dir } + fadeOut())
                },
                label = "tabTransition",
            ) { current ->
                when (current) {
                    Tab.Dashboard -> DashboardScreen(
                        state = state,
                        themeMode = themeMode,
                        onCycleTheme = onCycleTheme,
                        contentPadding = padding,
                    )
                    Tab.History -> HistoryScreen(
                        transactions = state.transactions,
                        onDelete = { id ->
                            vm.delete(id)
                            scope.launch { snackbar.showSnackbar("Transaksi dihapus") }
                        },
                        contentPadding = padding,
                    )
                }
            }
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

@Composable
private fun navColors(): androidx.compose.material3.NavigationBarItemColors {
    val t = Arta.tokens
    return NavigationBarItemDefaults.colors(
        selectedIconColor = if (t.isDark) t.heroText else t.card,
        indicatorColor = t.accent,
        selectedTextColor = t.accent,
        unselectedIconColor = t.textSecondary,
        unselectedTextColor = t.textSecondary,
    )
}
