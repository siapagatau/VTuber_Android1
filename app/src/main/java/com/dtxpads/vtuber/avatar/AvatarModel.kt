package com.dtxpads.vtuber.avatar

/**
 * Model avatar yang dipakai renderer/animator. SEMENTARA (provisional): format file di disk
 * (manifest.json) baru final di CP3. Tujuan kelas ini: PNG tunggal (MVP) dan skin berlayer
 * memakai model yang SAMA sehingga renderer tidak perlu dirombak saat layer/ekspresi ditambah.
 *
 * PNG tunggal = AvatarModel dengan satu layer BODY.
 */
enum class SkinKind { SINGLE_PNG, LAYERED }

enum class LayerRole {
    BODY, HEAD, HAIR,
    EYES_CLOSED, MOUTH_OPEN,
    EXPRESSION, OTHER
}

/** pivotX/pivotY: titik putar relatif (0..1) terhadap ukuran gambar layer. */
data class AvatarLayer(
    val id: String,
    val file: String,
    val role: LayerRole,
    val pivotX: Float = 0.5f,
    val pivotY: Float = 0.5f
)

/** Fitur animasi yang BENAR-BENAR didukung aset (UI hanya boleh menjanjikan ini). */
data class AvatarCapabilities(
    val headTilt: Boolean,
    val idleBreathing: Boolean,
    val blink: Boolean,
    val mouth: Boolean,
    val expressions: Boolean
)

data class AvatarModel(
    val id: String,
    val name: String,
    val kind: SkinKind,
    val width: Int,
    val height: Int,
    val layers: List<AvatarLayer>
) {
    /** Aturan: kedip butuh layer mata tertutup; mulut butuh layer mulut terbuka; ekspresi butuh sprite ekspresi. */
    val capabilities: AvatarCapabilities
        get() = AvatarCapabilities(
            headTilt = true,
            idleBreathing = true,
            blink = layers.any { it.role == LayerRole.EYES_CLOSED },
            mouth = layers.any { it.role == LayerRole.MOUTH_OPEN },
            expressions = layers.any { it.role == LayerRole.EXPRESSION }
        )

    companion object {
        fun singlePng(id: String, name: String, file: String, width: Int, height: Int) = AvatarModel(
            id = id,
            name = name,
            kind = SkinKind.SINGLE_PNG,
            width = width,
            height = height,
            layers = listOf(AvatarLayer(id = "body", file = file, role = LayerRole.BODY))
        )
    }
}
