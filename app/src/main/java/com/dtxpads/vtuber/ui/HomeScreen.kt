package com.dtxpads.vtuber.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dtxpads.vtuber.BuildConfig
import com.dtxpads.vtuber.avatar.AvatarModel
import com.dtxpads.vtuber.device.OverlayPermission
import com.dtxpads.vtuber.skin.ImportResult
import com.dtxpads.vtuber.skin.SkinImporter
import com.dtxpads.vtuber.skin.SkinStore
import com.dtxpads.vtuber.ui.theme.AppTextSize
import com.dtxpads.vtuber.ui.theme.CtrlPanelColor
import com.dtxpads.vtuber.ui.theme.GroupItemSpacing
import com.dtxpads.vtuber.ui.theme.GroupOuterPaddingH
import com.dtxpads.vtuber.ui.theme.GroupOuterPaddingV
import com.dtxpads.vtuber.ui.theme.PadLineColor
import com.dtxpads.vtuber.ui.theme.TextDim
import com.dtxpads.vtuber.ui.theme.TextMain
import com.dtxpads.vtuber.ui.theme.ThemeState
import com.dtxpads.vtuber.ui.theme.WarnAmber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class StatusLevel { OK, WARN, OFF }

data class StatusItem(val label: String, val value: String, val level: StatusLevel)

/**
 * Layar utama. Prioritas (Master Prompt §3): preview, impor skin, kamera+tracking, floating,
 * status, pengaturan. CP2: IMPOR / GANTI SKIN berfungsi (PNG transparan tunggal); tombol lain
 * tetap nonaktif dan berlabel checkpoint tujuannya (tidak pura-pura berfungsi).
 */
@Composable
fun HomeScreen(resumeTick: Int, onOpenSettings: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val overlayGranted = remember(resumeTick) { OverlayPermission.isGranted(context) }

    var avatar by remember { mutableStateOf(SkinStore.load(context)) }
    var preview by remember { mutableStateOf<ImageBitmap?>(null) }
    var previewFailed by remember { mutableStateOf(false) }
    var previewVersion by remember { mutableIntStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    var importError by rememberSaveable { mutableStateOf<String?>(null) }

    // Decode preview di thread latar (ter-downsample); dijalankan ulang tiap avatar/versi berubah.
    LaunchedEffect(avatar, previewVersion) {
        val model = avatar
        previewFailed = false
        if (model == null) {
            preview = null
        } else {
            val bmp = withContext(Dispatchers.IO) { SkinStore.decodePreview(context, model) }
            preview = bmp?.asImageBitmap()
            previewFailed = bmp == null
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null && !busy) {
            busy = true
            importError = null
            scope.launch {
                val result = withContext(Dispatchers.IO) { SkinImporter.import(context, uri) }
                when (result) {
                    is ImportResult.Success -> {
                        avatar = result.model
                        previewVersion++
                    }
                    is ImportResult.Failure -> importError = result.message
                }
                busy = false
            }
        }
    }

    val current = avatar
    val statuses = listOf(
        if (current != null) {
            StatusItem(
                "Avatar",
                "${current.name.ellipsize(16)} · ${current.width}×${current.height}",
                if (previewFailed) StatusLevel.WARN else StatusLevel.OK
            )
        } else {
            StatusItem("Avatar", "Belum ada", StatusLevel.OFF)
        },
        StatusItem("Kamera", "Mati (CP5)", StatusLevel.OFF),
        StatusItem("Tracking", "Mati (CP5)", StatusLevel.OFF),
        StatusItem("Floating", "Mati (CP6)", StatusLevel.OFF),
        StatusItem(
            "Izin overlay",
            if (overlayGranted) "Diizinkan" else "Belum diizinkan",
            if (overlayGranted) StatusLevel.OK else StatusLevel.WARN
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = GroupOuterPaddingH, vertical = GroupOuterPaddingV),
        verticalArrangement = Arrangement.spacedBy(GroupItemSpacing)
    ) {
        HeaderBar()
        AvatarPreview(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            image = preview,
            hasAvatar = current != null,
            failed = previewFailed,
            busy = busy
        )
        importError?.let {
            Text(
                it,
                color = WarnAmber,
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
            )
        }
        StatusPanel(statuses)
        ActionPanel(
            importBusy = busy,
            hasAvatar = current != null,
            onImport = { picker.launch(arrayOf("image/png")) },
            onOpenSettings = onOpenSettings
        )
    }
}

private fun String.ellipsize(max: Int): String = if (length <= max) this else take(max - 1) + "…"

@Composable
private fun HeaderBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("VTUBER DTX", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Text("CP2 · v${BuildConfig.VERSION_NAME}", color = TextDim, fontSize = 10.sp)
    }
}

/** Area preview: kotak-kotak (indikator transparansi untuk PNG alpha) + avatar atau pesan. */
@Composable
private fun AvatarPreview(
    modifier: Modifier,
    image: ImageBitmap?,
    hasAvatar: Boolean,
    failed: Boolean,
    busy: Boolean
) {
    val shape = RoundedCornerShape(5.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(CtrlPanelColor)
            .border(1.dp, PadLineColor, shape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val s = 16.dp.toPx()
            val cols = (size.width / s).toInt() + 1
            val rows = (size.height / s).toInt() + 1
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if ((r + c) % 2 == 0) {
                        drawRect(
                            color = Color(0xFF232325),
                            topLeft = Offset(c * s, r * s),
                            size = Size(s, s)
                        )
                    }
                }
            }
        }
        when {
            image != null -> Image(
                bitmap = image,
                contentDescription = "Preview avatar",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(8.dp)
            )
            busy -> Text("Memproses PNG…", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            failed -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Avatar tidak bisa dimuat", color = WarnAmber, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Impor ulang PNG lewat tombol GANTI SKIN", color = TextDim, fontSize = 11.sp)
            }
            hasAvatar -> Text("Memuat avatar…", color = TextDim, fontSize = 12.sp)
            else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Belum ada avatar", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Tekan IMPOR SKIN untuk memilih PNG transparan", color = TextDim, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun StatusPanel(items: List<StatusItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(CtrlPanelColor)
            .padding(horizontal = GroupOuterPaddingH, vertical = GroupOuterPaddingV),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            val dot = when (item.level) {
                StatusLevel.OK -> ThemeState.accentColor
                StatusLevel.WARN -> WarnAmber
                StatusLevel.OFF -> TextDim
            }
            val valueColor = when (item.level) {
                StatusLevel.OK -> TextMain
                StatusLevel.WARN -> WarnAmber
                StatusLevel.OFF -> TextDim
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(dot))
                Spacer(Modifier.size(8.dp))
                Text(item.label, color = TextDim, fontSize = AppTextSize, modifier = Modifier.weight(1f))
                Text(item.value, color = valueColor, fontSize = AppTextSize, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ActionPanel(
    importBusy: Boolean,
    hasAvatar: Boolean,
    onImport: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(CtrlPanelColor)
            .padding(horizontal = GroupOuterPaddingH, vertical = GroupOuterPaddingV),
        verticalArrangement = Arrangement.spacedBy(GroupItemSpacing)
    ) {
        ActionButton(
            label = if (hasAvatar) "GANTI SKIN" else "IMPOR SKIN",
            icon = Icons.Filled.Image,
            onClick = onImport,
            enabled = !importBusy,
            caption = if (importBusy) "Memproses…" else "PNG transparan, maks 8 MB",
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(GroupItemSpacing)) {
            ActionButton(
                label = "KAMERA",
                icon = Icons.Filled.Videocam,
                onClick = {},
                enabled = false,
                caption = "CP5",
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                label = "TRACKING",
                icon = Icons.Filled.Face,
                onClick = {},
                enabled = false,
                caption = "CP5",
                modifier = Modifier.weight(1f)
            )
        }
        ActionButton(
            label = "FLOATING",
            icon = Icons.Filled.Layers,
            onClick = {},
            enabled = false,
            caption = "Tersedia di CP6",
            modifier = Modifier.fillMaxWidth()
        )
        ActionButton(
            label = "PENGATURAN",
            icon = Icons.Filled.Settings,
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
