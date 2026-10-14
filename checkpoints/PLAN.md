# PLAN.md — Rencana Checkpoint CP0–CP8

Status yang dipakai: NOT_STARTED, IN_PROGRESS, BLOCKED, PASSED, FAILED.
PASSED hanya bila kriteria diverifikasi (lihat `docs/TEST_PLAN.md`). Tes yang tak bisa dijalankan = "belum terverifikasi".
Setiap checkpoint, saat dikerjakan, mendapat file `checkpoints/CPxx.md` berisi 12 butir (ID/nama, tujuan, pekerjaan, hasil diharapkan, file/modul, kriteria, tes, hasil aktual, bug/batasan, keputusan teknis, status, instruksi lanjut). Ringkasan rencananya di bawah.

| CP | Nama | Tujuan | Pekerjaan utama | File/modul | Kriteria lulus | Tes | Status |
|---|---|---|---|---|---|---|---|
| 0 | Audit & perencanaan | Dokumen analisis + rencana | Audit DTX, PROJECT_MASTER, ARCHITECTURE, TEST_PLAN, PLAN | `docs/`, `checkpoints/` | Dokumen lengkap, konsisten, ditinjau | T-0.1 | **PASSED** (lihat CP00.md) |
| 1 | Fondasi & UI | Proyek bisa dibuild; UI utama | Gradle + workflow CI, tema ala DTX, HomeScreen 6 elemen, navigasi, pengaturan | `app/`, `.github/workflows/`, `ui/` | Build hijau di CI; UI utama berjalan di perangkat | T-1.1–1.4 | **PASSED** (lihat CP01.md) |
| 2 | Impor & preview skin | PNG transparan tampil | Picker SAF, validasi (PNG, alpha, ukuran, dimensi), decode aman, simpan pilihan, pesan error | `skin/`, `avatar/` | PNG valid tampil & bertahan restart; file buruk ditolak tanpa crash | T-2.1–2.4 | **IN_PROGRESS** (kode ada, build UNVERIFIED; lihat CP02.md) |
| 3 | Prompt & paket skin | Format + prompt terdokumentasi dan sesuai kode | `manifest.json` skema, validator, mode sederhana + lanjutan, `VTUBER_SKIN_PROMPT.md` (12 isi wajib), contoh paket | `skin/`, `prompts/`, `skins/` | Contoh paket lolos validator; prompt cocok dengan validator | T-3.1–3.3 | NOT_STARTED |
| 4 | Animasi avatar | Idle + animasi sesuai aset | AvatarAnimator, idle, pemetaan ekspresi/layer (jika layered), uji dengan input simulasi | `avatar/` | Yang dijanjikan terdemo & teruji | T-4.1–4.2 | NOT_STARTED |
| 5 | Face tracking | Avatar mengikuti wajah | CameraX, MediaPipe, mapper, smoothing, sensitivitas, clamp, NoFace→idle, cek 16 KB | `tracking/` | Respons stabil di perangkat uji; angka latensi/CPU tercatat | T-5.1–5.4 | NOT_STARTED |
| 6 | Floating overlay | Avatar di atas app lain | Izin overlay, foreground service, drag/resize/alpha/sembunyi, stop, lifecycle, dokumentasi batas rekaman | `overlay/` | Berfungsi di perangkat uji; hasil tangkapan per aplikasi dicatat | T-6.1–6.5 | NOT_STARTED |
| 7 | Integrasi GitHub | Skin dari repo | `SkinRepository` (index.json, https, sha256, atomik, cache; URL sumber dikonfigurasi, tidak di-hardcode), UI pilih/unduh, dokumentasi menyiapkan repo | `skin/`, `docs/` | Unduh, validasi, pakai ulang offline; error aman; batas 8/24 MB teruji | T-7.1–7.4 | NOT_STARTED — **menunggu repo skin ditentukan pengguna** |
| 8 | Pengujian & rilis | Status rilis berdasarkan bukti | Build dari ZIP terekstrak, regresi, perbaiki bug kritis, APK uji bila build nyata sukses | semua | Status rilis ditentukan dari hasil nyata | T-8.1–8.2 | NOT_STARTED |

Aturan transisi: CP yang FAILED diperbaiki sebelum tahap berikutnya dianggap lulus. Pekerjaan tak bergantung boleh lanjut hanya bila tidak merusak fondasi yang belum stabil.
Catatan urutan: CP4 (animasi) dapat diuji dengan input simulasi sebelum CP5; tetapi "avatar merespons wajah" baru sah di CP5.
