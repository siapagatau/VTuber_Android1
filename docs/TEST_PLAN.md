# TEST_PLAN.md — Rencana Pengujian

Aturan: kolom "Hasil aktual" hanya diisi dari tes yang benar-benar dijalankan. Default `NOT_RUN`. Dilarang mengarang hasil.
Jalur verifikasi: (A) unit test lokal/CI, (B) build GitHub Actions, (C) uji manual di perangkat oleh pengguna dengan log/foto/catatan yang disalin balik.

| ID | CP | Tes | Cara | Kriteria lulus | Hasil aktual |
|---|---|---|---|---|---|
| T-0.1 | 0 | Dokumen Tahap 0 lengkap & konsisten | Tinjau file | Semua file wajib ada, tidak saling bertentangan | Ditinjau manual oleh penulis (lihat CP00) |
| T-1.1 | 1 | Build debug sukses | B | `assembleDebug` hijau | PASS (CI hijau, laporan pengguna 2026-10-14) |
| T-1.2 | 1 | UI utama tampil portrait | C | Layar utama menampilkan 6 elemen prioritas | PASS (screenshot Infinix X6725C: preview, status, impor, kamera, tracking, floating, pengaturan) |
| T-1.3 | 1 | Guard native lib di CI | B | Langkah guard lolos (APK tanpa .so) atau gagal dengan pesan jelas | PASS implisit (job CI hijau; log langkah tidak dilihat) |
| T-1.4 | 1 | Unit test JVM (ColorMath, SkinLimits, AvatarModel) | B | `testDebugUnitTest` hijau, 3 kelas tes | PASS (laporan pengguna; laporan tes tidak dilihat AI) |
| T-1.5 | 1 | Info perangkat nyata (Pengaturan → Info perangkat → SALIN) | C | Pengguna menyalin teks info; model/Android/page size tercatat | PASS (X6725C, Android 15, page 4096 B; lihat DEVICE_NOTES) |
| T-2.1 | 2 | Impor PNG alpha valid | C | Avatar tampil, transparan benar | NOT_RUN |
| T-2.2 | 2 | PNG tanpa alpha / bukan PNG / korup / terlalu besar | A+C | Ditolak + pesan jelas, tidak crash | NOT_RUN |
| T-2.3 | 2 | Skin terpilih bertahan setelah restart | C | Avatar sama muncul | NOT_RUN |
| T-2.4 | 2 | Impor file tepat 8 MB (lolos) dan > 8 MB (ditolak) | A+C | Sesuai `SkinLimits` | NOT_RUN |
| T-3.1 | 3 | Validator manifest: valid, field hilang, versi tak dikenal, path berbahaya | A | Sesuai ekspektasi per kasus | NOT_RUN |
| T-3.2 | 3 | Contoh paket di prompt lolos validator | A | Lulus tanpa edit manual | NOT_RUN |
| T-3.3 | 3 | Paket total > 24 MB ditolak | A | Pesan error jelas | NOT_RUN |
| T-4.1 | 4 | Idle berjalan | C | Gerakan halus saat tanpa wajah | NOT_RUN |
| T-4.2 | 4 | Layer/ekspresi (bila skin layered) | C | Sprite berganti sesuai input uji | NOT_RUN |
| T-5.1 | 5 | Deteksi wajah + 3 sumbu kepala | C | Avatar mengikuti yaw/pitch/roll | NOT_RUN |
| T-5.2 | 5 | Kedip & mulut (layered) | C | Sprite berganti saat kedip/buka mulut | NOT_RUN |
| T-5.3 | 5 | Smoothing/latensi/CPU/panas 10 menit | C | Dicatat angka nyata; UI tidak tersendat | NOT_RUN |
| T-5.4 | 5 | Wajah hilang → idle | C | Transisi tanpa glitch | NOT_RUN |
| T-6.1 | 6 | Izin overlay + start/stop | C | Overlay muncul/hilang benar | NOT_RUN |
| T-6.2 | 6 | Drag, resize, alpha, sembunyi | C | Berfungsi, posisi tersimpan | NOT_RUN |
| T-6.3 | 6 | Tracking lanjut saat pindah app | C | Avatar tetap mengikuti wajah | NOT_RUN |
| T-6.4 | 6 | Tangkapan di perekam layar bawaan + 1 app streaming | C | Dicatat tertangkap/tidak per aplikasi | NOT_RUN |
| T-6.5 | 6 | Lifecycle: kunci layar, rotasi, matikan service, app dibunuh | C | Tidak crash/leak kamera | NOT_RUN |
| T-7.1 | 7 | Unduh skin dari repo uji | C | Terpasang, sha256 cocok | NOT_RUN |
| T-7.2 | 7 | URL salah, http, sha256 salah, putus jaringan, file oversize | A+C | Ditolak aman, cache lama utuh | NOT_RUN |
| T-7.3 | 7 | Pakai ulang offline | C | Avatar tampil mode pesawat | NOT_RUN |
| T-7.4 | 7 | Unduhan > 8 MB per file / > 24 MB per paket ditolak | A+C | Sesuai `SkinLimits`, cache lama utuh | NOT_RUN |
| T-8.1 | 8 | Build release/debug dari hasil ekstraksi ZIP | B | Hijau | NOT_RUN |
| T-8.2 | 8 | Regresi semua tes sebelumnya | A+C | Semua lulus atau dicatat sebagai bug | NOT_RUN |

Perangkat uji: Infinix X6725C (spesifikasi dasar terverifikasi dari SALIN INFO; lihat `docs/DEVICE_NOTES.md`). Pengujian di perangkat nyata baru mencakup CP1 (T-1.2, T-1.5). Tes CP2 dan seterusnya: NOT_RUN.
