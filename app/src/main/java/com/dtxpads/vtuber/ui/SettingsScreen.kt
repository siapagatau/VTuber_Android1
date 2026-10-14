package com.dtxpads.vtuber.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dtxpads.vtuber.BuildConfig
import com.dtxpads.vtuber.device.DeviceInfo
import com.dtxpads.vtuber.device.OverlayPermission
import com.dtxpads.vtuber.ui.theme.AppTextSize
import com.dtxpads.vtuber.ui.theme.CtrlPanelColor
import com.dtxpads.vtuber.ui.theme.GroupItemSpacing
import com.dtxpads.vtuber.ui.theme.GroupOuterPaddingH
import com.dtxpads.vtuber.ui.theme.GroupOuterPaddingV
import com.dtxpads.vtuber.ui.theme.TextDim
import com.dtxpads.vtuber.ui.theme.TextMain
import com.dtxpads.vtuber.ui.theme.ThemeState
import com.dtxpads.vtuber.ui.theme.WarnAmber
import com.dtxpads.vtuber.ui.theme.argbToHex
import com.dtxpads.vtuber.ui.theme.parseHexRgb

private val AccentPresets: List<Int> = listOf(
    0xFFFFFFFFL, 0xFFC97A5FL, 0xFF4FC3F7L, 0xFFBA68C8L, 0xFFFFD54FL, 0xFFF06292L
).map { it.toInt() }

@Composable
fun SettingsScreen(resumeTick: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = GroupOuterPaddingH, vertical = GroupOuterPaddingV),
        verticalArrangement = Arrangement.spacedBy(GroupItemSpacing)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextMain)
            }
            Text("PENGATURAN", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(GroupItemSpacing)
        ) {
            AccentSection()
            OverlaySection(resumeTick)
            DeviceSection(resumeTick)
            AboutSection()
        }
    }
}

@Composable
private fun SectionPanel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(CtrlPanelColor)
            .padding(horizontal = GroupOuterPaddingH, vertical = GroupOuterPaddingV),
        verticalArrangement = Arrangement.spacedBy(GroupItemSpacing)
    ) {
        Text(title, color = TextDim, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        content()
    }
}

@Composable
private fun AccentSection() {
    val context = LocalContext.current
    var hex by remember { mutableStateOf(argbToHex(ThemeState.accentColor.toArgb())) }
    var error by remember { mutableStateOf<String?>(null) }

    SectionPanel("WARNA AKSEN") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AccentPresets.forEach { argb ->
                val selected = ThemeState.accentColor.toArgb() == argb
                Box32(
                    color = Color(argb),
                    selected = selected,
                    onClick = {
                        ThemeState.set(context, Color(argb))
                        hex = argbToHex(argb)
                        error = null
                    }
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(GroupItemSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = hex,
                onValueChange = { hex = it.take(7) },
                singleLine = true,
                label = { Text("Hex RRGGBB") },
                isError = error != null,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                label = "TERAPKAN",
                icon = Icons.Filled.Settings,
                onClick = {
                    val v = parseHexRgb(hex)
                    if (v == null) {
                        error = "Format hex tidak valid (contoh: C97A5F)"
                    } else {
                        ThemeState.set(context, Color(v))
                        error = null
                    }
                }
            )
        }
        if (error != null) Text(error ?: "", color = WarnAmber, fontSize = 11.sp)
    }
}

@Composable
private fun Box32(color: Color, selected: Boolean, onClick: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .border(if (selected) 2.dp else 1.dp, if (selected) TextMain else TextDim, CircleShape)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun OverlaySection(resumeTick: Int) {
    val context = LocalContext.current
    val granted = remember(resumeTick) { OverlayPermission.isGranted(context) }
    SectionPanel("IZIN OVERLAY") {
        Text(
            if (granted) "Diizinkan" else "Belum diizinkan (dibutuhkan untuk mode floating di CP6)",
            color = if (granted) TextMain else WarnAmber,
            fontSize = AppTextSize
        )
        ActionButton(
            label = "BUKA PENGATURAN IZIN",
            icon = Icons.Filled.Settings,
            onClick = {
                if (!OverlayPermission.openSettings(context)) {
                    Toast.makeText(context, "Halaman izin overlay tidak bisa dibuka di perangkat ini", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DeviceSection(resumeTick: Int) {
    val context = LocalContext.current
    val items = remember(resumeTick) { DeviceInfo.collect(context) }
    SectionPanel("INFO PERANGKAT (dibaca langsung dari perangkat)") {
        items.forEach { (k, v) ->
            Row {
                Text(k, color = TextDim, fontSize = AppTextSize, modifier = Modifier.weight(0.45f))
                Text(v, color = TextMain, fontSize = AppTextSize, modifier = Modifier.weight(0.55f))
            }
        }
        ActionButton(
            label = "SALIN INFO",
            icon = Icons.Filled.Settings,
            onClick = {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("info-perangkat", DeviceInfo.asText(items)))
                Toast.makeText(context, "Info perangkat disalin", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AboutSection() {
    SectionPanel("TENTANG") {
        Text("Versi: ${BuildConfig.VERSION_NAME}", color = TextMain, fontSize = AppTextSize)
        Text("Package: ${BuildConfig.APPLICATION_ID}", color = TextMain, fontSize = AppTextSize)
        Text(
            "Checkpoint 1 — fondasi & UI. Fitur impor skin, tracking, dan floating belum tersedia.",
            color = TextDim,
            fontSize = 11.sp
        )
    }
}
