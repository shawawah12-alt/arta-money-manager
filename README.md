# Arta

Aplikasi catatan keuangan Android. Kubuat karena males sama aplikasi budgeting
yang ada — kebanyakan fitur, banyak iklan, dan nyuruh login segala macam.
Arta cuma butuh satu hal: catat uang masuk-keluar, terus lihat ringkasannya.
Semua data disimpan di HP, nggak ada server, nggak ada akun.

Ditulis pakai Kotlin + Jetpack Compose. Chart-nya (donut sama bar mingguan)
digambar manual pakai Canvas, bukan library chart, soalnya library chart
kebanyakan overkill buat kebutuhan sesimpel ini.

## Fitur

- Catat pemasukan/pengeluaran, pilih kategori, kasih catatan
- Ringkasan bulan berjalan: saldo, pengeluaran per kategori, arus kas 7 hari terakhir
- Riwayat transaksi, bisa difilter, hapus dengan swipe ke kiri
- Tema terang/gelap/ikut sistem
- Nominal ditulis format Rupiah (id-ID)

Data disimpan pakai DataStore, di-serialize ke JSON pakai kotlinx-serialization.
Sengaja nggak pakai Room karena datanya cuma satu list transaksi — Room
kerasa berlebihan.

## Build

Butuh JDK 17 dan Android SDK 36.

```
./gradlew assembleRelease
```

APK-nya keluar di `app/build/outputs/apk/release/`, ukurannya sekitar 2 MB
(sudah diminify R8). Kalau mau langsung pakai, ambil aja dari halaman Releases.

Catatan: release build di-sign pakai debug keystore yang ikut ke-commit di repo
ini. Buat dipakai sendiri nggak masalah, tapi kalau mau publish ke Play Store
ya ganti keystore sendiri.

## Struktur singkat

Kode utamanya di `app/src/main/java/com/zhaw/arta/`:

- `data/` — model transaksi, kategori, dan penyimpanan
- `viewmodel/` — agregasi (saldo, per kategori, per hari)
- `ui/components/` — chart custom dan modifier animasi (entrance, press-scale, count-up)
- `ui/screens/` — dashboard, riwayat, sheet tambah transaksi

Minimal Android 7.0 (API 24).
