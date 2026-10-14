package com.dtxpads.vtuber

import com.dtxpads.vtuber.skin.SkinLimits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SkinLimitsTest {
    @Test fun limitsMatchAgreedValues() {
        assertEquals(8L * 1024 * 1024, SkinLimits.MAX_FILE_BYTES)
        assertEquals(24L * 1024 * 1024, SkinLimits.MAX_PACKAGE_BYTES)
    }

    @Test fun fileBoundary() {
        assertNotNull(SkinLimits.fileSizeError("a.png", 0))
        assertNotNull(SkinLimits.fileSizeError("a.png", -1))
        assertNull(SkinLimits.fileSizeError("a.png", 1))
        assertNull(SkinLimits.fileSizeError("a.png", SkinLimits.MAX_FILE_BYTES))
        assertNotNull(SkinLimits.fileSizeError("a.png", SkinLimits.MAX_FILE_BYTES + 1))
    }

    @Test fun packageBoundary() {
        assertNull(SkinLimits.packageSizeError(SkinLimits.MAX_PACKAGE_BYTES))
        assertNotNull(SkinLimits.packageSizeError(SkinLimits.MAX_PACKAGE_BYTES + 1))
    }
}
