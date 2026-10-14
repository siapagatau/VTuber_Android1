# CURRENT_STATUS.md

Terakhir diperbarui: 2026-10-14 (CP0 PASSED; CP1 PASSED; CP2 IN_PROGRESS)

- **Tahap aktif**: 2 — Impor & preview skin (PNG tunggal)
- **Checkpoint terakhir lulus**: CP1 (PASSED, bukti di CP01.md)
- **Selesai dan diuji**: CP0 (dokumen); CP1 (build + 3 kelas unit test hijau di CI [laporan pengguna]; UI utama & Pengaturan berjalan di Infinix X6725C; info perangkat nyata tercatat)
- **Selesai tetapi belum diverifikasi**: seluruh kode CP2 (hanya pemeriksaan statis + verifikasi logika parser pada PNG nyata; belum dikompilasi, unit test baru belum dijalankan, belum diuji di perangkat)
- **Belum selesai**: CP3–CP8
- **Bug diketahui**: tidak ada yang teramati; error kompilasi CP2 mungkin ada karena belum pernah dibuild
- **File penting berubah (CP2)**: `skin/{PngInspector,ImageSampling,SkinStore,SkinImporter,SkinLimits}.kt`, `ui/HomeScreen.kt`, `app/build.gradle.kts`, tes `PngInspectorTest`, `ImageSamplingTest`
- **Perintah & hasil build terakhir**: CP1 = `gradle testDebugUnitTest assembleDebug --stacktrace` hijau (laporan pengguna). CP2 = belum dibuild.
- **Tes terakhir**: T-1.1/1.2/1.3/1.4/1.5 = PASS; T-2.x = NOT_RUN
- **APK**: tidak ada di `release/` (disengaja; APK debug CP1 hanya artifact CI)

## Perangkat uji (terverifikasi dari SALIN INFO)
Infinix X6725C, Android 15/API 35, 8 core, RAM 7762 MB, 720x1600 @280 dpi (~411x914 dp), page size 4096 B, kamera depan ada, overlay diizinkan. Kaitan model X6725C ↔ "Smart 10 Plus" belum dikonfirmasi.

## Keterbatasan terbuka
1. Belum ada screenshot UI DTX → kemiripan visual tak bisa dinilai.
2. Versi MediaPipe/CameraX belum diperiksa (CP5); risiko 16 KB (R3) tidak muncul di perangkat uji ini (page 4096 B) tetapi guard CI tetap perlu.
3. URL repo skin belum ditentukan → CP7 menunggu.
4. Batas dimensi 64–4096 px adalah usulan AI, belum disetujui pengguna.

## Yang dibutuhkan dari pengguna untuk menutup CP2
1. Push ke GitHub dan jalankan **Build Debug APK**; kirim hijau/merah + log error kompilasi bila merah.
2. Install APK, uji skenario di CP02.md butir 12, kirim hasil/screenshot.
3. Setujui atau ubah batas dimensi 64–4096 px.

## Langkah berikutnya
Setelah CP2 PASSED: CP3 — skema `manifest.json` + validator (batas 24 MB/paket) + `prompts/VTUBER_SKIN_PROMPT.md` yang cocok dengan validator.
