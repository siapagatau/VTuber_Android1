package com.dtxpads.vtuber.skin

import java.io.DataInputStream
import java.io.EOFException
import java.io.InputStream

data class PngInfo(
    val width: Int,
    val height: Int,
    val bitDepth: Int,
    val colorType: Int,
    val hasAlpha: Boolean
)

sealed class PngInspection {
    data class Valid(val info: PngInfo) : PngInspection()
    data class Invalid(val message: String) : PngInspection()
}

/**
 * Pemeriksa header PNG murni (JVM, tanpa Android) — dijalankan SEBELUM decode:
 * tanda tangan PNG, IHDR, dimensi, kanal alpha (color type 4/6, atau tRNS untuk palette/gray/RGB).
 * Hanya membaca chunk sampai IDAT pertama, jadi murah dan tidak mengalokasikan gambar.
 * CRC tidak diverifikasi di sini; file rusak ditangkap oleh decode berikutnya.
 */
object PngInspector {
    private val SIGNATURE = intArrayOf(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
    private val IEND_ASCII = byteArrayOf(0x49, 0x45, 0x4E, 0x44)
    private val VALID_COLOR_TYPES = setOf(0, 2, 3, 4, 6)

    fun inspect(name: String, input: InputStream): PngInspection {
        val data = DataInputStream(input)
        try {
            val sig = ByteArray(8)
            data.readFully(sig)
            for (i in 0 until 8) {
                if ((sig[i].toInt() and 0xFF) != SIGNATURE[i]) {
                    return PngInspection.Invalid("File $name bukan PNG (tanda tangan file tidak cocok)")
                }
            }

            // Chunk pertama wajib IHDR sepanjang 13 byte.
            val firstLen = data.readInt()
            val firstType = readType(data)
            if (firstType != "IHDR" || firstLen != 13) {
                return PngInspection.Invalid("File $name rusak (header IHDR tidak ditemukan)")
            }
            val width = data.readInt()
            val height = data.readInt()
            val bitDepth = data.readUnsignedByte()
            val colorType = data.readUnsignedByte()
            data.readUnsignedByte() // compression
            data.readUnsignedByte() // filter
            data.readUnsignedByte() // interlace
            data.readInt() // CRC IHDR

            if (width <= 0 || height <= 0) {
                return PngInspection.Invalid("File $name rusak (dimensi tidak sah)")
            }
            if (colorType !in VALID_COLOR_TYPES) {
                return PngInspection.Invalid("File $name rusak (tipe warna PNG tidak dikenal)")
            }

            var hasTrns = false
            var sawIdat = false
            while (!sawIdat) {
                val len = data.readInt().toLong() and 0xFFFFFFFFL
                val type = readType(data)
                when (type) {
                    "IDAT" -> sawIdat = true
                    "IEND" -> return PngInspection.Invalid("File $name tidak berisi data gambar")
                    else -> {
                        if (len > Int.MAX_VALUE) {
                            return PngInspection.Invalid("File $name rusak (ukuran chunk tidak wajar)")
                        }
                        if (type == "tRNS") hasTrns = true
                        skipFully(data, len + 4) // data + CRC
                    }
                }
            }

            val hasAlpha = colorType == 4 || colorType == 6 || hasTrns
            if (!hasAlpha) {
                return PngInspection.Invalid(
                    "File $name tidak transparan (tidak punya kanal alpha). Gunakan PNG dengan latar transparan."
                )
            }
            SkinLimits.dimensionError(name, width, height)?.let { return PngInspection.Invalid(it) }

            return PngInspection.Valid(PngInfo(width, height, bitDepth, colorType, hasAlpha))
        } catch (e: EOFException) {
            return PngInspection.Invalid("File $name rusak atau terpotong (berakhir sebelum data gambar)")
        }
    }

    /** True bila [tail] (potongan akhir file) memuat chunk penutup "IEND" — indikator file tidak terpotong. */
    fun tailHasIend(tail: ByteArray): Boolean {
        if (tail.size < IEND_ASCII.size) return false
        for (i in 0..tail.size - IEND_ASCII.size) {
            var match = true
            for (j in IEND_ASCII.indices) {
                if (tail[i + j] != IEND_ASCII[j]) { match = false; break }
            }
            if (match) return true
        }
        return false
    }

    private fun readType(data: DataInputStream): String {
        val b = ByteArray(4)
        data.readFully(b)
        return String(b, Charsets.ISO_8859_1)
    }

    private fun skipFully(input: DataInputStream, count: Long) {
        var remaining = count
        while (remaining > 0) {
            val skipped = input.skip(remaining)
            if (skipped > 0) {
                remaining -= skipped
            } else {
                if (input.read() < 0) throw EOFException()
                remaining -= 1
            }
        }
    }
}
