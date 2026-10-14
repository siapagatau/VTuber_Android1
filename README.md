# VTuber Android (Floating Avatar)

Aplikasi Android VTuber 2D yang mengikuti gerak wajah dan tampil sebagai overlay; standar kerja mengacu pada DTX Kendang.
Package: `com.dtxpads.vtuber`.

**Status: CP0 PASSED, CP1 PASSED (build + unit test hijau di CI, UI diverifikasi di Infinix X6725C). CP2 IN_PROGRESS — kode impor PNG sudah ditulis tetapi BELUM dikompilasi/diuji. Belum ada APK rilis.**

## Mulai baca (AI penerus)
`checkpoints/PROJECT_MASTER.md` → `CURRENT_STATUS.md` → `CP01.md` (checkpoint terakhir) → `TASKS.md` → `CHANGELOG.md`. Analisis referensi: `docs/DTX_REFERENCE_ANALYSIS.md`.

## Build (tanpa Android Studio, seperti DTX)
1. Buat repo GitHub, push isi folder ini.
2. Tab **Actions** → "Build Debug APK" (otomatis saat push, atau Run workflow).
3. Unduh artifact `apk-vtuber-debug` (APK) dan `unit-test-reports`.
4. Gagal? Salin log error kompilasi ke AI.
Perintah CI: `gradle testDebugUnitTest assembleDebug --stacktrace` (Gradle 9.3.1, JDK 17, SDK 36).
