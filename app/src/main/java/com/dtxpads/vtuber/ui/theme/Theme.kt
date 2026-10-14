package com.dtxpads.vtuber.ui.theme

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Palet diambil dari konstanta di MainActivity.kt DTX Kendang (diperiksa di CP0):
// "flat, no green" menurut komentar DTX -> status OK tidak memakai hijau.
val BgColor = Color(0xFF111113)
val PanelColor = Color(0xFF3C3C3F)
val PadLineColor = Color(0xFF232325)
val PadTextColor = Color(0xFFA9A9AD)
val CtrlPanelColor = Color(0xFF1C1C1E)
val TextDim = Color(0xFF8C8C90)
val TextMain = Color(0xFFF2EDE9)
val RecordRed = Color(0xFFD32F2F)   // DTX: merah untuk status aktif/rekam
val WarnAmber = Color(0xFFFFB74D)   // BARU (bukan dari DTX): peringatan izin

// Ukuran & jarak seragam ala DTX (AppTextSize 12sp, padding grup 8dp, jarak item 6dp, ikon 24dp).
val AppTextSize = 12.sp
val GroupOuterPaddingV = 8.dp
val GroupOuterPaddingH = 8.dp
val GroupItemSpacing = 6.dp
val ControlIconSize = 24.dp

val DefaultAccentColor = Color(0xFFFFFFFF)

private const val THEME_PREFS = "vtuber_theme"
private const val KEY_ACCENT_ARGB = "accent_argb"

/** Warna aksen custom, disimpan di SharedPreferences (pola ThemeState DTX). */
object ThemeState {
    var accentColor by mutableStateOf(DefaultAccentColor)
        private set

    val accentTextColor: Color
        get() = if (isLightColor(accentColor.toArgb())) Color(0xFF201512) else TextMain

    fun load(context: Context) {
        val prefs = context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
        if (prefs.contains(KEY_ACCENT_ARGB)) {
            accentColor = Color(prefs.getInt(KEY_ACCENT_ARGB, DefaultAccentColor.toArgb()))
        }
    }

    fun set(context: Context, color: Color) {
        accentColor = color
        context.getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_ACCENT_ARGB, color.toArgb())
            .apply()
    }
}

@Composable
fun VTuberTheme(content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        primary = ThemeState.accentColor,
        onPrimary = ThemeState.accentTextColor,
        background = BgColor,
        onBackground = TextMain,
        surface = CtrlPanelColor,
        onSurface = TextMain,
        surfaceVariant = PanelColor,
        onSurfaceVariant = PadTextColor,
        outline = TextDim
    )
    MaterialTheme(colorScheme = scheme, content = content)
}
