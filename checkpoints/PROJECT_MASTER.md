# PROJECT_MASTER.md — Sumber Kebenaran Proyek VTuber Android

Versi dokumen: 0.2 (CP0→CP1). Bahasa dokumen: Indonesia.
Spesifikasi asal: `Master_Prompt_VTuber_DTX.txt` (diserahkan pengguna). Bila dokumen ini bertentangan dengan Master Prompt, Master Prompt menang; perbaiki dokumen ini.

## 1. Tujuan
Aplikasi Android yang menampilkan karakter VTuber 2D (PNG transparan / paket skin) yang mengikuti gerak wajah pengguna secara real-time lewat kamera depan, dapat tampil sebagai **floating overlay** di atas aplikasi lain untuk rekaman/streaming, dengan sistem skin + prompt AI pembuat skin, integrasi GitHub, dan dokumentasi yang bisa dilanjutkan AI lain.

## 2. Fitur wajib (dari Master Prompt §2)
1. Impor PNG transparan lokal. 2. Impor/muat skin dari repo GitHub. 3. Preview. 4. Animasi dari gerak wajah. 5. Idle + ekspresi sesuai aset. 6. Floating overlay. 7. Drag & resize avatar. 8. Kontrol tracking/kamera/floating sederhana. 9. Dukungan rekam/streaming sesuai kemampuan Android. 10. `prompts/VTUBER_SKIN_PROMPT.md`. 11. Dokumentasi + checkpoint.
Aturan: fitur harus benar-benar berfungsi, bukan mockup.

## 3. Referensi DTX Kendang
Ringkasan + keputusan: `docs/DTX_REFERENCE_ANALYSIS.md`. Prinsip: bila ragu, ikuti pola DTX yang relevan & aman; jangan menyalin kode membabi buta.

## 4. Standar desain (turunan DTX; UNVERIFIED secara visual — belum ada screenshot)
- Tema gelap netral: latar `#111113`, panel `#1C1C1E`/`#3C3C3F`, teks redup `#8C8C90`, aksen custom (default putih), status rekam/aktif merah `#D32F2F`.
- Satu tangan, portrait. Halaman utama: (1) preview karakter, (2) impor/ganti skin, (3) kamera + face tracking, (4) floating mode, (5) indikator status, (6) akses pengaturan. Pengaturan lanjutan di panel terpisah. Tanpa dekorasi tanpa fungsi.
- Teks UI: Indonesia.

## 5. Teknologi (keputusan awal; versi dipin di Tahap 1)
| Area | Pilihan | Status |
|---|---|---|
| Bahasa/UI | Kotlin + Jetpack Compose + Material3 (seperti DTX) | Keputusan |
| Toolchain | AGP/Kotlin/Gradle/JDK 17/SDK 36 mengikuti DTX sebagai titik awal | Perlu diverifikasi saat build |
| Build | GitHub Actions (seperti DTX); sandbox AI tidak punya Android SDK | Keputusan |
| Kamera | CameraX (ImageAnalysis, kamera depan) | Rencana, belum diuji |
| Face tracking | MediaPipe Face Landmarker (Tasks Vision), blendshapes + matriks transformasi wajah | Rencana; versi/ukuran/16 KB **belum diverifikasi** (R3) |
| Render avatar | Compose Canvas / View kustom untuk PNG + transformasi (rotasi, translasi, skala, swap sprite) | Rencana |
| Overlay | `WindowManager` + `TYPE_APPLICATION_OVERLAY`, foreground service | Rencana |
| Penyimpanan skin | `filesDir/skin-cache/` + SharedPreferences (pola DTX) | Rencana |
| Jaringan | `HttpURLConnection`/OkHttp, hanya https, sha256 | Rencana |
| Package ID | `com.dtxpads.vtuber` (applicationId = namespace) | **Disetujui pengguna**; diperiksa tidak bentrok dengan konfigurasi DTX yang tersedia (`com.dtxpads.kendang`, `com.kendang.realpads`). Bentrok dengan aplikasi lain di perangkat/Play belum bisa diperiksa. |

Perubahan teknologi inti/format skin/arsitektur wajib disertai alasan + pembaruan dokumen.

## 6. Struktur repository (target)
```
VTuber_Android/
  app/                       (Tahap 1+)
  prompts/VTUBER_SKIN_PROMPT.md   (Tahap 3)
  skins/                     skin bawaan & contoh
  docs/{DTX_REFERENCE_ANALYSIS,ARCHITECTURE,TEST_PLAN}.md
  checkpoints/{PROJECT_MASTER,CURRENT_STATUS,CHANGELOG,TASKS,PLAN,CP00}.md
  .github/workflows/         (Tahap 1)
  release/                   (APK hanya bila build nyata berhasil)
```

## 7. Format skin (rancangan — final ditetapkan Tahap 3)
Mode sederhana: 1 PNG transparan. Mode lanjutan: paket `manifest.json` + `avatar.png` + `expressions/` + `layers/` + `preview.png` + `README.md`.
`manifest.json` memuat: id, name, formatVersion, resolusi, jenis model (`single_png` | `layered`), ekspresi, layer+pivot, fitur animasi yang didukung.
Validasi paket, pesan error jelas, kompatibilitas versi (`formatVersion`), pengguna tidak wajib mengedit JSON.
Repo skin: `index.json` ala DTX (sha256 + size per file), hanya https, atomik, offline cache.
Keputusan D-04 (DISETUJUI pengguna): batas awal **8 MB per file, 24 MB per paket** (`skin/SkinLimits.kt`). Ini batas proyek VTuber, bukan klaim DTX (DTX 4 MB).
Wajib diterapkan DAN diuji pada: impor (CP2), validasi paket (CP3), unduhan (CP7). Konstanta + unit test batas sudah ada di CP1; penerapan di alur nyata belum.

## 7a. Keputusan pengguna (CP0→CP1)
- **MVP = PNG transparan tunggal** (CP2/CP4 dulu). Arsitektur disiapkan untuk layer/sprite/ekspresi tanpa rombak: `avatar/AvatarModel.kt` (provisional, PNG tunggal = model dengan 1 layer BODY; kemampuan animasi diturunkan dari layer yang ada).
- **Repo skin tidak diwajibkan satu repository.** URL HTTPS/GitHub dikonfigurasi (pola `skin-repo.json` DTX), bisa lebih dari satu sumber. **CP7 menunggu** sampai repo ditentukan; tidak memblokir CP1–CP6.
- **Perangkat uji awal: Infinix Smart 10 Plus** — spesifikasi dari situs pihak ketiga (belum terverifikasi di perangkat); lihat `docs/DEVICE_NOTES.md`. Info nyata dibaca layar Pengaturan → "Info perangkat".
- DTX Kendang tetap referensi utama (UI, skin, prompt AI, checkpoint). Tidak boleh mengarang tampilan DTX; bila screenshot belum ada, catat sebagai keterbatasan.
- CP0 hanya PASSED setelah verifikasi (lihat `checkpoints/CP00.md`).

## 8. Tahapan & checkpoint
Lihat `checkpoints/PLAN.md` (CP0–CP8 lengkap dengan kriteria lulus). Status aktual: `checkpoints/CURRENT_STATUS.md`.

## 9. Definisi "fitur selesai"
Selesai = kode ada + terhubung ke fitur nyata + build berhasil + diuji sesuai `docs/TEST_PLAN.md` dengan hasil tercatat. Tanpa bukti tes → status "selesai tetapi belum diverifikasi". Dilarang menandai PASSED tanpa verifikasi; dilarang mengarang hasil tes atau APK.

## 10. Aturan AI penerus
1. Baca urut: PROJECT_MASTER → CURRENT_STATUS → checkpoint terakhir (`CPxx.md`) → TASKS → CHANGELOG.
2. Periksa kode aktual sebelum mengubah; bila dokumen ≠ kode, tentukan mana benar & perbaiki eksplisit.
3. Jangan mengulang dari awal; lanjut dari kondisi aktual.
4. Jangan hapus riwayat penting.
5. Setelah kerja: uji, catat hasil nyata, perbarui TASKS/CHANGELOG/CURRENT_STATUS/checkpoint.
6. Satu ZIP lengkap per tahap: `VTuber_Android_Checkpoint_NN.zip`, satu folder induk, tanpa cache build. Uji ekstrak sebelum menyerahkan.
7. Jangan menyimpan token/API key di repo.
8. Jangan mengklaim overlay selalu terekam, atau PNG tunggal punya ekspresi/lip-sync.
9. Lingkungan AI tanpa Android SDK/jaringan: tandai build/tes `UNVERIFIED`; jalur verifikasi = GitHub Actions + log dari pengguna.

## 11. Risiko teknis utama
Lihat `docs/ARCHITECTURE.md` §Risiko (R1–R8).
