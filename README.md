# Arta — Money Manager

Aplikasi pencatat keuangan Android premium dengan desain **luxe obsidian & champagne gold**.
Dibangun full custom dengan **Kotlin + Jetpack Compose + Material 3** — tanpa template.

## ✨ Fitur

- **Dashboard** — kartu saldo gradien gold, ringkasan pemasukan/pengeluaran bulan berjalan
- **Donut chart** pengeluaran per kategori (custom Canvas, animasi sweep)
- **Bar chart arus kas 7 hari** (masuk vs keluar, custom Canvas)
- **Rasio bulanan** — progress pengeluaran vs total arus kas
- **Catat transaksi** via bottom sheet: nominal, catatan, 12 kategori berwarna dengan ikon
- **Riwayat** — filter Semua/Masuk/Keluar, hapus transaksi dengan snackbar feedback
- **Offline-first** — data persisten di perangkat via DataStore + kotlinx-serialization
- Format Rupiah lokal (id-ID), dark theme elegan

## 🛠 Tech Stack

| Layer | Teknologi |
|---|---|
| UI | Jetpack Compose, Material 3, custom Canvas charts |
| State | ViewModel + StateFlow |
| Persistensi | DataStore Preferences + kotlinx-serialization |
| Bahasa | Kotlin 2.2 |
| Build | AGP 8.10, R8 minify + resource shrinking (release) |

## 📦 Build

```bash
./gradlew assembleRelease
# output: app/build/outputs/apk/release/app-release.apk (~1.8 MB)
```

Requirement: JDK 17, Android SDK 36.

## 📁 Struktur

```
app/src/main/java/com/agon/app/
├── MainActivity.kt              # Scaffold, bottom nav, FAB, snackbar
├── data/
│   ├── Models.kt                # Transaction, Category (12 kategori), formatter Rupiah
│   └── TransactionStore.kt      # DataStore + JSON ledger
├── viewmodel/
│   └── LedgerViewModel.kt       # agregasi saldo, per-kategori, 7 hari
└── ui/
    ├── components/Charts.kt     # CategoryDonut, WeeklyBars (custom Canvas)
    ├── screens/
    │   ├── DashboardScreen.kt
    │   ├── HistoryScreen.kt
    │   └── AddTransactionSheet.kt
    └── theme/                   # palet obsidian-gold + typography custom
```
