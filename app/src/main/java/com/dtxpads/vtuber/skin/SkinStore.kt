package com.dtxpads.vtuber.skin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dtxpads.vtuber.avatar.AvatarModel
import java.io.File

/**
 * Penyimpanan skin terimpor (CP2): satu file PNG di penyimpanan internal aplikasi + metadata di
 * SharedPreferences. File disalin ke sini, jadi tidak bergantung pada izin akses URI asli
 * dan bertahan setelah aplikasi ditutup/restart.
 */
object SkinStore {
    const val AVATAR_FILE_NAME = "avatar.png"
    const val IMPORTED_ID = "imported"

    private const val PREFS = "vtuber_skin"
    private const val KEY_NAME = "name"
    private const val KEY_WIDTH = "width"
    private const val KEY_HEIGHT = "height"

    fun dir(context: Context): File = File(context.filesDir, "skins/imported")

    fun avatarFile(context: Context): File = File(dir(context), AVATAR_FILE_NAME)

    fun save(context: Context, name: String, width: Int, height: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_NAME, name)
            .putInt(KEY_WIDTH, width)
            .putInt(KEY_HEIGHT, height)
            .apply()
    }

    /** Avatar tersimpan, atau null bila belum ada / file hilang. */
    fun load(context: Context): AvatarModel? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val w = prefs.getInt(KEY_WIDTH, 0)
        val h = prefs.getInt(KEY_HEIGHT, 0)
        val file = avatarFile(context)
        if (w <= 0 || h <= 0 || !file.isFile || file.length() <= 0L) return null
        return AvatarModel.singlePng(IMPORTED_ID, name, AVATAR_FILE_NAME, w, h)
    }

    /**
     * Decode untuk preview dengan downsample (sisi terpanjang <= [SkinLimits.DECODE_MAX_SIDE]).
     * Null bila file tidak bisa didecode atau RAM tidak cukup. Panggil di thread latar.
     */
    fun decodePreview(context: Context, model: AvatarModel): Bitmap? =
        decodeDownsampled(avatarFile(context), model.width, model.height)

    internal fun decodeDownsampled(file: File, width: Int, height: Int): Bitmap? {
        val opts = BitmapFactory.Options().apply {
            inSampleSize = ImageSampling.inSampleSize(width, height, SkinLimits.DECODE_MAX_SIDE)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        return try {
            BitmapFactory.decodeFile(file.absolutePath, opts)
        } catch (_: OutOfMemoryError) {
            null
        }
    }
}
