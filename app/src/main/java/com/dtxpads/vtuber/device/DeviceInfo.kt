package com.dtxpads.vtuber.device

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.system.Os
import android.system.OsConstants

/** Izin overlay (dipakai penuh di CP6; di CP1 hanya dibaca + membuka halaman izin sistem). */
object OverlayPermission {
    fun isGranted(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun openSettings(context: Context): Boolean = try {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + context.packageName)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Info perangkat NYATA yang dibaca saat aplikasi berjalan. Dipakai pengguna untuk memverifikasi
 * perangkat uji (mis. Infinix Smart 10 Plus) dan disalin balik ke AI sebagai bukti.
 */
object DeviceInfo {
    fun collect(context: Context): List<Pair<String, String>> {
        val dm = context.resources.displayMetrics
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mem = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mem)
        val pageSize = try {
            Os.sysconf(OsConstants._SC_PAGESIZE)
        } catch (e: Exception) {
            null
        }
        val frontCam = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
        return listOf(
            "Merek" to Build.MANUFACTURER,
            "Model" to Build.MODEL,
            "Android" to "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            "Patch keamanan" to (Build.VERSION.SECURITY_PATCH ?: "-"),
            "ABI" to Build.SUPPORTED_ABIS.joinToString(),
            "Core CPU" to Runtime.getRuntime().availableProcessors().toString(),
            "RAM total" to "${mem.totalMem / (1024L * 1024L)} MB",
            "Layar" to "${dm.widthPixels}x${dm.heightPixels} px, ${dm.densityDpi} dpi",
            "Page size memori" to (if (pageSize != null) "$pageSize byte" else "tidak terbaca"),
            "Kamera depan" to (if (frontCam) "terdeteksi" else "tidak terdeteksi"),
            "Izin overlay" to (if (OverlayPermission.isGranted(context)) "diizinkan" else "belum diizinkan")
        )
    }

    fun asText(items: List<Pair<String, String>>): String =
        items.joinToString("\n") { (k, v) -> "$k: $v" }
}
