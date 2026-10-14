# ARCHITECTURE.md — Rancangan Arsitektur (Checkpoint 0, belum diimplementasikan)

Semua isi di sini adalah **rencana**. Tidak ada kode yang ditulis atau diuji pada Checkpoint 0.

## 1. Tiga proses yang harus dibedakan (Master Prompt §7)
```
Kamera depan ──► [1 DETEKSI WAJAH]  ──► landmark + blendshape mentah
                  MediaPipe Face Landmarker
                         │
                         ▼
                 [2 ESTIMASI EKSPRESI]  ──► TrackingState (yaw/pitch/roll, eyeL/R, mouthOpen, smile, ...)
                  normalisasi, kalibrasi, smoothing, clamp, sensitivitas
                         │
                         ▼
                 [3 ANIMASI KARAKTER]   ──► AvatarPose (offset/rotasi/skala/sprite aktif per layer)
                  dibatasi kemampuan skin (single_png vs layered)
                         │
                         ▼
                  AvatarRenderer ──► layar utama (preview) / overlay (floating)
```
Bila wajah hilang: `TrackingState = NoFace` → animasi idle (napas/goyang halus, kedip acak bila ada aset kedip).

## 2. Modul (paket usulan `com.dtxpads.vtuber`)
| Modul | Tugas |
|---|---|
| `ui/` | Compose: HomeScreen, SkinPicker, Settings, StatusBar; tema ala DTX |
| `skin/` | `SkinManifest`, `SkinParser`, `SkinValidator`, `SkinLoader`, `SkinManager` (state + fallback), `SkinRepository` (index.json, unduh, sha256, cache atomik) |
| `tracking/` | `CameraSource` (CameraX), `FaceTracker` (MediaPipe), `TrackingMapper` (blendshape→state), `Smoother` |
| `avatar/` | `AvatarPose`, `AvatarAnimator` (idle + pemetaan), `AvatarRenderer` |
| `overlay/` | `OverlayService` (foreground), `OverlayWindow` (WindowManager, drag/resize/alpha) |
| `data/` | Preferensi (SharedPreferences/DataStore), penyimpanan skin |

## 3. Kemampuan animasi per jenis skin (agar tidak berjanji berlebihan)
| Kemampuan | `single_png` | `layered` |
|---|---|---|
| Rotasi/kemiringan kepala (roll) | Ya (rotasi seluruh gambar) | Ya |
| Geser/yaw-pitch semu | Terbatas (translasi + skala halus) | Ya (parallax per layer) |
| Napas/idle | Ya | Ya |
| Kedip | **Tidak** (tidak ada aset mata) | Ya bila ada sprite mata terbuka/tutup |
| Gerak mulut | **Tidak** | Ya bila ada sprite mulut |
| Ekspresi | **Tidak** | Ya bila ada sprite ekspresi |
UI harus menampilkan kemampuan yang aktif untuk skin terpilih.

## 4. Format skin dan fallback
Rantai: skin terpilih (cache) → avatar bawaan (assets) → layar "tidak ada avatar" dengan pesan jelas. Detail `manifest.json` ditetapkan Tahap 3 dan harus sama dengan validator nyata.

## 5. Floating overlay
`OverlayService` (foreground service, tipe sesuai versi Android) memegang kamera + tracker + renderer overlay; `MainActivity` hanya UI kontrol. Izin: `SYSTEM_ALERT_WINDOW` (pengguna beri manual), `CAMERA`, `POST_NOTIFICATIONS`, izin tipe foreground service. Kontrol: mulai/stop, drag, resize, alpha, sembunyi/tampil, tutup dari notifikasi.

## 6. Risiko teknis (R1–R8)
| ID | Risiko | Mitigasi / yang harus dibuktikan |
|---|---|---|
| R1 | Android 11+ membatasi kamera di latar belakang; Android 14 mewajibkan tipe foreground service `camera` + izin terkait | Service dimulai saat Activity terlihat; uji nyata di perangkat target (CP6). Jika tidak bisa, dokumentasikan batas. |
| R2 | Overlay belum tentu tertangkap semua aplikasi rekam/stream; app ber-`FLAG_SECURE` memblok tangkapan | Jangan klaim; uji di perangkat target; dokumentasikan alternatif realistis. Kamera virtual di Android tidak tersedia sebagai API standar → tidak dijanjikan. |
| R3 | MediaPipe membawa library native; target SDK 36/Play mensyaratkan 16 KB page alignment (DTX sudah memeriksa ini di CI) | Pin versi MediaPipe, jalankan cek 16 KB di CI di CP5; bila gagal, cari versi/alternatif. **Belum diverifikasi.** |
| R4 | Beban CPU/baterai/panas dari kamera + inferensi + overlay | Batasi FPS/resolusi analisis, `STRATEGY_KEEP_ONLY_LATEST`, ukur di CP5/CP6 |
| R5 | Latensi & jitter tracking | Smoothing adaptif (mis. One-Euro/EMA) + ambang; ukur latensi nyata |
| R6 | Sandbox AI tanpa Android SDK/jaringan → tidak bisa kompilasi | Verifikasi lewat GitHub Actions + log pengguna; status UNVERIFIED sampai ada bukti |
| R7 | PNG besar → OOM/lambat | Batas dimensi & ukuran, downsample saat decode, cache bitmap |
| R8 | Skin dari internet: file berbahaya/rusak | Hanya https, sha256, batas ukuran, validasi, pasang atomik, id/path aman (pola DTX) |

## 6a. Penyiapan perluasan skin (keputusan pengguna: PNG dulu, layer nanti)
`AvatarModel` (kode: `avatar/AvatarModel.kt`, provisional) adalah satu-satunya yang dilihat renderer/animator: daftar `AvatarLayer(role, file, pivot)` + `AvatarCapabilities` turunan.
PNG tunggal → 1 layer `BODY`. Skin berlayer menambah layer (`EYES_CLOSED`, `MOUTH_OPEN`, `EXPRESSION`, ...) tanpa mengubah renderer. Parser manifest (CP3) bertugas mengubah file → `AvatarModel`.

## 6b. Sumber skin tidak di-hardcode
Daftar sumber (URL HTTPS `index.json`, mis. raw GitHub) dibaca dari konfigurasi + bisa ditambah pengguna; tidak ada repository wajib. Batas: `SkinLimits` (8 MB/file, 24 MB/paket).

## 6c. Implikasi perangkat uji awal (data pihak ketiga, belum diverifikasi)
Infinix Smart 10 Plus: Unisoc T7250 (2×A75 + 6×A55, 12 nm), GPU Mali-G57 MP1, RAM 4/8 GB, layar 720×1600, kamera depan 8 MP, Android 15 (XOS 15.1; ada listing "Go edition"). Kelas entry-level → R4 (CPU/panas/baterai) dinaikkan prioritasnya:
analisis kamera resolusi rendah, FPS tracking terbatas, delegate CPU sebagai default yang diuji dulu, pantau memori. XOS/Go berpotensi mematikan service latar agresif → R1/R2 harus diuji nyata.

## 7. Dependensi (usulan; versi dipin Tahap 1/5 setelah dicek)
androidx core/activity-compose, Compose BOM + Material3, CameraX (core/camera2/lifecycle/view), `com.google.mediapipe:tasks-vision` (versi TBD), model `face_landmarker.task` (disimpan di `assets/`; ukuran & lisensi TBD — **belum diperiksa**), OkHttp opsional.
