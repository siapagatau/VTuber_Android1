package com.dtxpads.vtuber.skin

/**
 * Batas ukuran skin. Angka ini adalah batas awal PROYEK VTuber yang disetujui pengguna
 * (8 MB per file, 24 MB per paket). Bukan klaim bahwa DTX Kendang memakai batas yang sama
 * (DTX: 4 MB). Wajib diterapkan di impor (CP2), validasi paket (CP3), dan unduhan (CP7).
 */
object SkinLimits {
    const val MAX_FILE_BYTES: Long = 8L * 1024 * 1024
    const val MAX_PACKAGE_BYTES: Long = 24L * 1024 * 1024

    /**
     * Batas dimensi gambar (piksel per sisi) — USULAN CP2, belum disetujui pengguna; ubah di sini bila perlu.
     * Alasan batas atas: PNG 8 MB bisa berisi gambar sangat besar ("bom dekompresi"); batas ini ditolak
     * SEBELUM decode. 4096x4096 ARGB = 64 MB RAM bila didecode penuh, jadi preview selalu di-downsample.
     */
    const val MIN_DIMENSION: Int = 64
    const val MAX_DIMENSION: Int = 4096

    /** Sisi terpanjang bitmap hasil decode untuk preview (downsample bila lebih besar). */
    const val DECODE_MAX_SIDE: Int = 2048

    /** Pesan error bila dimensi tidak sah, null bila lolos. */
    fun dimensionError(name: String, width: Int, height: Int): String? = when {
        width < MIN_DIMENSION || height < MIN_DIMENSION ->
            "Gambar $name terlalu kecil (${width}x$height px; minimum ${MIN_DIMENSION}x$MIN_DIMENSION px)"
        width > MAX_DIMENSION || height > MAX_DIMENSION ->
            "Gambar $name terlalu besar (${width}x$height px; maksimum ${MAX_DIMENSION}x$MAX_DIMENSION px)"
        else -> null
    }

    /** Pesan error bila ukuran file tidak sah, null bila lolos. */
    fun fileSizeError(name: String, sizeBytes: Long): String? = when {
        sizeBytes <= 0L -> "File $name kosong"
        sizeBytes > MAX_FILE_BYTES ->
            "File $name terlalu besar (${sizeBytes / 1024} KB; maksimum ${MAX_FILE_BYTES / 1024} KB)"
        else -> null
    }

    /** Pesan error bila total ukuran paket melebihi batas, null bila lolos. */
    fun packageSizeError(totalBytes: Long): String? =
        if (totalBytes > MAX_PACKAGE_BYTES) {
            "Paket skin terlalu besar (${totalBytes / 1024} KB; maksimum ${MAX_PACKAGE_BYTES / 1024} KB)"
        } else null
}
