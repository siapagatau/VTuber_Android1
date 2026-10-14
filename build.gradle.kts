plugins {
    // Versi toolchain DIAMBIL dari proyek DTX Kendang (build CI-nya sudah dipakai pengguna):
    // AGP 9.1.0 (compileSdk 36), Kotlin 2.2.10 + plugin Compose terpisah.
    // STATUS: belum dikompilasi untuk proyek ini (CP1) - lihat checkpoints/CURRENT_STATUS.md.
    id("com.android.application") version "9.1.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
}
