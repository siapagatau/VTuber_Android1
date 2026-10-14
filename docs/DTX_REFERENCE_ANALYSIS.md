# DTX_REFERENCE_ANALYSIS.md — Hasil Audit Referensi DTX Kendang

Status: Tahap 0 / Checkpoint 0. Bagian "Fakta" berasal dari file yang benar-benar diperiksa.
Bagian "Adaptasi" dan "Tidak berlaku" adalah penilaian/keputusan, bukan fakta DTX.

## 1. Sumber yang diperiksa
| Berkas | Isi yang dibaca |
|---|---|
| `DTX-SUPORT-LED-FIXUPDATE.zip` | `CHECKPOINT.md`, `README.md`, `app/build.gradle.kts`, `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `AndroidManifest.xml`, `.github/workflows/build-apk.yml`, `docs/architecture.md`, `docs/skin-repo.md`, daftar file `skin/*.kt`, `ThemeState.kt`, konstanta warna `MainActivity.kt` |
| `SKIN-DTX-PREMIUM-ADDUNGU.zip` | `CHECKPOINT.md`, `PROMPT_SKIN.md` (bagian awal), `index.json`, `skins/skin-01/skin.json` (bagian awal), daftar file |
| `Master_Prompt_VTuber_DTX.txt` | dibaca penuh |

Pembaruan audit (CP0 lanjutan, 2026-10-14): `PROMPT_SKIN.md` sampai bagian 7, `SkinValidator.kt` (bagian awal),
`SkinRepository.kt` (`download`, `httpGetBytes`), dan konstanta/tombol aksi `MainActivity.kt` baris 85–128 & 1780–1830 sudah dibaca.
**Masih belum dibaca penuh** (tidak menghalangi CP0/CP1): `MainActivity.kt` (3162 baris; sisanya), `SkinHost/SkinRenderer/HitTester`,
`docs/skin-v3*.md`, `docs/validation.md`, `docs/skin-picker.md`.
**Keterbatasan referensi:** tidak ada screenshot UI DTX di paket (belum diberikan pengguna). Analisis UI hanya dari kode/konstanta,
bukan dari tampilan nyata; kemiripan visual BELUM dapat dinilai. Pengguna akan memberi screenshot/source bila diperlukan.

## 2. Fakta: struktur & teknologi DTX
- Android native: Kotlin + Jetpack Compose (Material3), audio C++ (Oboe). Package `com.kendang.realpads`, satu modul `:app`.
- Gradle: AGP 9.1.0, Kotlin 2.2.10 (+ plugin compose), compileSdk/targetSdk 36, minSdk 26, JDK 17, NDK 28.2 (16 KB page size), CMake 3.22.1.
  `gradle.properties` mematikan `android.builtInKotlin` dan `android.newDsl` agar tidak perlu migrasi DSL.
- Product flavor `free`/`premium` dari satu source tree; flag `BuildConfig.IS_PREMIUM`.
- Build lewat **GitHub Actions** (`build-apk.yml`: JDK 17, Gradle 9.3.1, install SDK 36 + NDK + CMake, verifikasi alignment 16 KB, upload APK sebagai artifact). Pengguna tidak memakai Android Studio.
- Dependensi UI: compose-bom 2024.06.00, material3, material-icons-extended; SVG: `com.caverock:androidsvg-aar:1.4`.
- Release: R8 minify + shrinkResources, debug symbols native.
- Palet tema gelap (MainActivity): `BgColor #111113`, `PanelColor #3C3C3F`, `PadColor #4D4D50`, `PadLineColor #232325`,
  `CtrlPanelColor #1C1C1E`, `TextDim #8C8C90`, aksen custom user (default putih) via `ThemeState` + SharedPreferences,
  warna teks kontras otomatis dari luminance. Merah `#D32F2F` untuk status rekam.
- `MainActivity` dikunci landscape (khas aplikasi pad).
- Foreground service media projection + izin `FOREGROUND_SERVICE_MEDIA_PROJECTION` dan `POST_NOTIFICATIONS` dipakai untuk rekam layar — contoh pola izin/service Android 13/14.

## 3. Fakta: Skin Engine & repository skin
- Alur: gambar referensi → `skin.svg` + `skin.json` → repo GitHub / assets default → `SkinRepository` (unduh+cache) → `SkinLoader` → `SkinParser` → `SkinValidator` → `SkinManager.state` → render → aksi.
- Pemisahan tegas: SVG = tampilan/posisi/ID/hit area; JSON = daftar kontrol + mapping; APK = fungsi. SVG tanpa logika.
- Repo skin terpisah dari APK. `index.json`: `schemaVersion`, `repo`, `updated`, `skins[]` (id, name, version, author, path, `files{nama:{sha256,size}}`).
- `SkinRepository`: hanya `https`, batas 4 MB, cek ukuran + sha256, validasi di folder sementara lalu pasang **atomik** ke `filesDir/skin-cache/skins/<id>/`; kegagalan tidak merusak cache lama. `id` hanya `A-Z a-z 0-9 _ -` (maks 64), path tanpa `..`.
- Rantai fallback: skin terpilih dari cache → skin default di `assets/` → UI klasik. Skin terpilih disimpan di SharedPreferences.
- URL repo di `assets/config/skin-repo.json` (`indexUrl`); placeholder `RAW_GITHUB_INDEX_URL` = repo dimatikan.
- `skin.json` punya `schemaVersion` dan `version`; kebutuhan versi APK dicatat di checkpoint ("Butuh APK Skin v3.3").
- **Prompt skin**: `PROMPT_SKIN.md` mandiri (AI cukup menerima dokumen + gambar), satu gambar = satu skin, alur analisis → desain → file → validasi → daftar di index, tiap tahap menulis `CHECKPOINT.md`, `analysis/skin-XX.md`, dan `preview.svg`.

## 4. Fakta: kebiasaan checkpoint DTX
- `CHECKPOINT.md` berupa log kronologis "Update Stage N / vX.Y: ... BELUM dikompilasi."
- Status jujur: sandbox AI tanpa Android SDK → kode ditulis tapi belum dikompilasi; pengguna membangun lewat GitHub Actions dan menyalin log error ke AI berikutnya.
- Dokumen per topik di `docs/`.
- Kekurangan yang terlihat: tidak ada status baku (PASSED/FAILED), tidak ada TASKS terpisah, tidak ada tabel tes. Master Prompt VTuber meminta format lebih ketat; itu yang dipakai di sini.

## 5. Pola yang DIADAPTASI untuk VTuber
| Pola DTX | Adaptasi VTuber |
|---|---|
| Kotlin + Compose + Material3, satu modul `:app` | Sama. |
| Toolchain Gradle/AGP/Kotlin/SDK 36/JDK 17 + opt-out di `gradle.properties` | Titik awal; versi dipin & diverifikasi ulang di Tahap 1 (tidak disalin buta). |
| Build via GitHub Actions + cek 16 KB | Sama; **kritis** karena MediaPipe membawa library native (risiko R3). |
| Paket skin + JSON + validator + `index.json` sha256/size | `manifest.json` + PNG (+ layer). Unduh https, sha256, atomik, cache offline, fallback dipakai ulang konsepnya. |
| Rantai fallback skin | skin terpilih → avatar bawaan → layar kosong + pesan jelas (tanpa crash). |
| `PROMPT_SKIN.md` mandiri + alur bertahap | `prompts/VTUBER_SKIN_PROMPT.md` (Tahap 3), harus selaras dengan validator nyata. |
| Dokumen per topik di `docs/` | `ARCHITECTURE.md`, `TEST_PLAN.md`, + dokumen format skin & overlay nanti. |
| Tema gelap netral + aksen custom + teks kontras otomatis | Palet & `ThemeState` serupa. |
| "SVG tanpa logika" | `manifest.json` deklaratif, tanpa script. |
| Catatan jujur "BELUM dikompilasi" | Wajib: tandai `UNVERIFIED` pada apa pun yang belum dibuild/diuji. |

## 6. Yang TIDAK berlaku langsung
- Audio engine (Oboe/C++), pad, MIDI, preset, musik, `PadEngine`.
- Render SVG vektor untuk tampilan controller: avatar VTuber berbasis **PNG raster**.
- Hit-testing multi-touch per kontrol: avatar memakai drag/resize overlay.
- Landscape terkunci: VTuber portrait (utama) + overlay lintas orientasi.
- Skema `skin.json` DTX (controls/hit/states/indicators): diganti `manifest.json`.
- Flavor free/premium & kunci fitur: bukan persyaratan Master Prompt; tidak diadopsi kecuali diminta.
- Batas 4 MB per file: terlalu kecil untuk PNG layer beresolusi tinggi → batas baru diusulkan di PROJECT_MASTER (keputusan terbuka D-04).

## 7. Yang belum diketahui / diminta dari pengguna
1. Screenshot UI DTX asli (kerapian/proporsi nyata). Tanpa itu, kesesuaian visual hanya perkiraan dari palet kode.
2. Perangkat uji target (merek, versi Android) — menentukan perilaku overlay/kamera di latar belakang.
3. Nama/URL repo GitHub skin VTuber (dibutuhkan Tahap 7).
4. Apakah layer/sprite diprioritaskan sejak awal, atau cukup PNG tunggal dulu (Tahap 4).

## 8. Temuan tambahan dari audit lanjutan (fakta, diperiksa di file)
- **UI DTX (MainActivity.kt):** tombol aksi = Material3 `Button`, `containerColor = PanelColor`, `contentColor = PadTextColor`, status aktif/rekam `#D32F2F` (konten putih);
  sudut `RoundedCornerShape` 3–5 dp; ikon 24 dp + label berdampingan, 12 sp SemiBold huruf besar (`AppTextSize = 12.sp`, `ControlIconSize = 24.dp`);
  padding grup 8 dp, jarak antar-item 6 dp (`GroupOuterPaddingV/H`, `GroupItemSpacing`). Komentar kode: palet "flat, no green".
  Layout DTX: pad di tengah, panel preset kiri, panel musik kanan, strip tombol aksi bawah (landscape).
- **Tema:** `Theme.RealPads` memakai warna jendela = `BgColor` agar tidak ada kilatan saat start; aksen custom disimpan di SharedPreferences.
- **Dokumentasi DTX tidak selalu sinkron dengan kode:** `README.md` menyebut applicationId `com.kendang.realpads.gler.free/premium`,
  sedangkan `app/build.gradle.kts` memakai `com.dtxpads.kendang` (free) dan `…premium`; namespace kode `com.kendang.realpads`.
  Pelajaran: bila dokumen ≠ kode, kode adalah sumber kebenaran (sesuai aturan Master Prompt §11).
- **PROMPT_SKIN.md:** skin ditolak bila memuat `<script> <foreignObject> <image> <use> <audio> <video> <iframe>`, atribut `on*`, `href`, URL http(s) di atribut,
  >20.000 elemen, >4 MB, viewBox tidak wajar. Katalog fungsi RESMI tertutup (fungsi di luar katalog → skin ditolak). Konvensi ID baku (`pad-N`, `hit-…`).
  "Prinsip Skin v2": tidak ada menu tambahan di luar skin. Bila atribut SVG ≠ skin.json, atribut SVG menang. Wajib `unsupportedInReference` untuk elemen tanpa fungsi.
- **Validator:** tidak pernah melempar exception; `errors` = skin ditolak, `warnings` = tetap dipakai; dipanggil juga sebelum skin unduhan masuk cache.
- **SkinRepository.download:** unduh ke `<id>.tmp` → cek size + sha256 → parse+validasi → rename lama→`<id>.old`, tmp→`<id>` → hapus `.old`; gagal mengembalikan lama.
  `HttpURLConnection`, timeout, redirect diikuti, hanya `https://`, batas total `MAX_BYTES = 4 MB`.
  **Catatan:** batas ukuran DTX dicek terhadap total yang dibaca per respons; untuk VTuber dipakai batas berbeda (8 MB/file, 24 MB/paket) — lihat `SkinLimits.kt`.
- **Prompt skin DTX bergaya "satu gambar = satu skin"; VTuber akan "satu karakter = satu paket"** (keputusan, bukan fakta DTX).
