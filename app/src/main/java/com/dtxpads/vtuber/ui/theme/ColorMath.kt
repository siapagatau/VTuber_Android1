package com.dtxpads.vtuber.ui.theme

/** Logika warna murni (tanpa Android/Compose) agar bisa diuji sebagai unit test JVM. */

private fun isHexDigit(c: Char) = c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F'

/** "C97A5F" atau "#C97A5F" -> ARGB opaque (0xFFRRGGBB). null bila format tidak valid. */
fun parseHexRgb(input: String): Int? {
    val clean = input.trim().removePrefix("#")
    if (clean.length != 6 || !clean.all(::isHexDigit)) return null
    val rgb = clean.toLong(16)
    return (0xFF000000L or rgb).toInt()
}

/** ARGB -> "RRGGBB" huruf besar (tanpa alpha, tanpa '#'). */
fun argbToHex(argb: Int): String = String.format("%06X", argb and 0xFFFFFF)

/** Luminance sederhana 0..1 (rumus sama dengan DTX ThemeState.bestTextColorFor). */
fun luminanceOf(argb: Int): Float {
    val r = ((argb shr 16) and 0xFF) / 255f
    val g = ((argb shr 8) and 0xFF) / 255f
    val b = (argb and 0xFF) / 255f
    return 0.299f * r + 0.587f * g + 0.114f * b
}

/** true bila warna terang -> teks di atasnya harus gelap. Ambang 0.6 seperti DTX. */
fun isLightColor(argb: Int): Boolean = luminanceOf(argb) > 0.6f
