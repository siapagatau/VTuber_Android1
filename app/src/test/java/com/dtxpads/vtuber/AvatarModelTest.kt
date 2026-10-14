package com.dtxpads.vtuber

import com.dtxpads.vtuber.avatar.AvatarLayer
import com.dtxpads.vtuber.avatar.AvatarModel
import com.dtxpads.vtuber.avatar.LayerRole
import com.dtxpads.vtuber.avatar.SkinKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AvatarModelTest {
    @Test fun singlePngHasNoBlinkMouthOrExpressions() {
        val m = AvatarModel.singlePng("a", "A", "avatar.png", 512, 512)
        assertEquals(SkinKind.SINGLE_PNG, m.kind)
        assertEquals(1, m.layers.size)
        val c = m.capabilities
        assertTrue(c.headTilt)
        assertTrue(c.idleBreathing)
        assertFalse(c.blink)
        assertFalse(c.mouth)
        assertFalse(c.expressions)
    }

    @Test fun layeredCapabilitiesFollowLayers() {
        val m = AvatarModel(
            id = "b", name = "B", kind = SkinKind.LAYERED, width = 512, height = 512,
            layers = listOf(
                AvatarLayer("body", "body.png", LayerRole.BODY),
                AvatarLayer("eyes_closed", "eyes_closed.png", LayerRole.EYES_CLOSED),
                AvatarLayer("mouth_open", "mouth_open.png", LayerRole.MOUTH_OPEN)
            )
        )
        val c = m.capabilities
        assertTrue(c.blink)
        assertTrue(c.mouth)
        assertFalse(c.expressions)
    }
}
