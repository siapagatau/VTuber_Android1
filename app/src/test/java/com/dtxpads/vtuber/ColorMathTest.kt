package com.dtxpads.vtuber

import com.dtxpads.vtuber.ui.theme.argbToHex
import com.dtxpads.vtuber.ui.theme.isLightColor
import com.dtxpads.vtuber.ui.theme.parseHexRgb
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorMathTest {
    @Test fun parsesWithAndWithoutHash() {
        assertEquals(0xFFC97A5F.toInt(), parseHexRgb("C97A5F"))
        assertEquals(0xFFC97A5F.toInt(), parseHexRgb("#c97a5f"))
        assertEquals(0xFFFFFFFF.toInt(), parseHexRgb("  #FFFFFF "))
    }

    @Test fun rejectsInvalid() {
        assertNull(parseHexRgb(""))
        assertNull(parseHexRgb("12345"))
        assertNull(parseHexRgb("1234567"))
        assertNull(parseHexRgb("GGGGGG"))
        assertNull(parseHexRgb("+12345"))
        assertNull(parseHexRgb("-12345"))
    }

    @Test fun hexRoundTrip() {
        assertEquals("C97A5F", argbToHex(0xFFC97A5F.toInt()))
        assertEquals("000000", argbToHex(0xFF000000.toInt()))
    }

    @Test fun lightness() {
        assertTrue(isLightColor(0xFFFFFFFF.toInt()))
        assertFalse(isLightColor(0xFF111113.toInt()))
    }
}
