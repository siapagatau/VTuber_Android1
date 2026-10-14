package com.dtxpads.vtuber

import com.dtxpads.vtuber.skin.PngInspection
import com.dtxpads.vtuber.skin.PngInspector
import com.dtxpads.vtuber.skin.SkinLimits
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** PNG uji dibuat oleh ImageIO (JDK) saat tes berjalan; tidak ada file biner di repo. */
class PngInspectorTest {
    private fun png(img: BufferedImage): ByteArray {
        val out = ByteArrayOutputStream()
        check(ImageIO.write(img, "png", out)) { "ImageIO tidak bisa menulis PNG" }
        return out.toByteArray()
    }

    private fun argb(w: Int, h: Int) = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)

    private fun inspect(bytes: ByteArray) = PngInspector.inspect("t.png", ByteArrayInputStream(bytes))

    private fun assertInvalid(r: PngInspection, contains: String) {
        assertTrue("diharapkan Invalid, dapat $r", r is PngInspection.Invalid)
        val msg = (r as PngInspection.Invalid).message
        assertTrue("pesan '$msg' tidak memuat '$contains'", msg.contains(contains))
    }

    @Test fun validArgbPng() {
        val r = inspect(png(argb(128, 96)))
        assertTrue(r is PngInspection.Valid)
        val info = (r as PngInspection.Valid).info
        assertEquals(128, info.width)
        assertEquals(96, info.height)
        assertTrue(info.hasAlpha)
    }

    @Test fun rgbWithoutAlphaIsRejected() {
        assertInvalid(inspect(png(BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB))), "tidak transparan")
    }

    @Test fun paletteWithTransparencyIsAccepted() {
        val r = byteArrayOf(0, 120.toByte()); val g = byteArrayOf(0, 60.toByte()); val b = byteArrayOf(0, 30.toByte())
        val a = byteArrayOf(0, 255.toByte())
        val icm = IndexColorModel(8, 2, r, g, b, a)
        val img = BufferedImage(64, 64, BufferedImage.TYPE_BYTE_INDEXED, icm)
        val res = inspect(png(img))
        assertTrue("dapat $res", res is PngInspection.Valid)
    }

    @Test fun notAPngIsRejected() {
        assertInvalid(inspect("GIF89a-bukan-png".toByteArray()), "bukan PNG")
        assertInvalid(inspect(ByteArray(0)), "rusak")
    }

    @Test fun truncatedBeforeImageDataIsRejected() {
        val full = png(argb(128, 128))
        assertInvalid(inspect(full.copyOf(20)), "rusak")
    }

    @Test fun dimensionBoundaries() {
        assertTrue(inspect(png(argb(SkinLimits.MIN_DIMENSION, SkinLimits.MIN_DIMENSION))) is PngInspection.Valid)
        assertInvalid(inspect(png(argb(SkinLimits.MIN_DIMENSION - 1, 100))), "terlalu kecil")
        assertTrue(inspect(png(argb(SkinLimits.MAX_DIMENSION, 64))) is PngInspection.Valid)
        assertInvalid(inspect(png(argb(SkinLimits.MAX_DIMENSION + 1, 64))), "terlalu besar")
    }

    @Test fun tailDetection() {
        val full = png(argb(64, 64))
        assertTrue(PngInspector.tailHasIend(full.copyOfRange(full.size - 64.coerceAtMost(full.size), full.size)))
        assertFalse(PngInspector.tailHasIend(full.copyOf(full.size - 12)))
        assertFalse(PngInspector.tailHasIend(ByteArray(2)))
    }

    @Test fun dimensionErrorMessagesAreNullWhenValid() {
        assertEquals(null, SkinLimits.dimensionError("a.png", 512, 512))
    }
}
