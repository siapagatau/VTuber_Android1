package com.dtxpads.vtuber.skin

/** Logika murni (JVM) untuk memilih downsample saat decode agar RAM aman di perangkat entry-level. */
object ImageSampling {
    /**
     * Pangkat 2 terkecil [s] sehingga sisi terpanjang setelah dibagi [s] (dibulatkan ke atas)
     * tidak melebihi [maxSide]. Sesuai kontrak BitmapFactory.Options.inSampleSize.
     */
    fun inSampleSize(width: Int, height: Int, maxSide: Int): Int {
        require(maxSide > 0) { "maxSide harus > 0" }
        val longest = maxOf(width, height)
        var s = 1
        while ((longest + s - 1) / s > maxSide) s *= 2
        return s
    }
}
