package com.dtxpads.vtuber

import com.dtxpads.vtuber.skin.ImageSampling
import org.junit.Assert.assertEquals
import org.junit.Test

class ImageSamplingTest {
    @Test fun smallImagesAreNotDownsampled() {
        assertEquals(1, ImageSampling.inSampleSize(512, 512, 2048))
        assertEquals(1, ImageSampling.inSampleSize(2048, 100, 2048))
    }

    @Test fun usesLongestSideAndPowersOfTwo() {
        assertEquals(2, ImageSampling.inSampleSize(2049, 64, 2048))
        assertEquals(2, ImageSampling.inSampleSize(64, 4096, 2048))
        assertEquals(4, ImageSampling.inSampleSize(4097, 4097, 2048)) // ceil(4097/2)=2049 > 2048
        assertEquals(8, ImageSampling.inSampleSize(16000, 16000, 2048))
    }
}
