package com.dtxpads.vtuber.skin

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import com.dtxpads.vtuber.avatar.AvatarModel
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.StandardCopyOption

sealed class ImportResult {
    data class Success(val model: AvatarModel) : ImportResult()
    data class Failure(val message: String) : ImportResult()
}

/**
 * Impor satu PNG transparan dari URI pilihan pengguna (SAF). Urutan (CP2):
 * 1) cek ukuran terdeklarasi (bila ada) -> 2) salin ke file sementara dengan batas keras 8 MB
 * -> 3) cek header PNG/alpha/dimensi (tanpa decode) -> 4) cek tidak terpotong -> 5) decode uji
 * ter-downsample -> 6) ganti file lama secara atomik + simpan metadata.
 * Skin lama TIDAK disentuh bila ada langkah yang gagal. Panggil di thread latar.
 */
object SkinImporter {
    private const val TEMP_NAME = "incoming.tmp"
    private const val TAIL_BYTES = 64

    fun import(context: Context, uri: Uri): ImportResult {
        val name = displayName(context, uri) ?: "avatar.png"
        val dir = SkinStore.dir(context)
        if (!dir.isDirectory && !dir.mkdirs()) {
            return ImportResult.Failure("Tidak bisa membuat folder penyimpanan skin")
        }
        val temp = File(dir, TEMP_NAME)
        try {
            declaredSize(context, uri)?.let { declared ->
                SkinLimits.fileSizeError(name, declared)?.let { return ImportResult.Failure(it) }
            }

            val copied = copyLimited(context, uri, temp)
                ?: return ImportResult.Failure("Tidak bisa membuka file $name")
            SkinLimits.fileSizeError(name, copied)?.let { return ImportResult.Failure(it) }

            val info = when (val r = temp.inputStream().buffered().use { PngInspector.inspect(name, it) }) {
                is PngInspection.Invalid -> return ImportResult.Failure(r.message)
                is PngInspection.Valid -> r.info
            }

            if (!PngInspector.tailHasIend(readTail(temp))) {
                return ImportResult.Failure("File $name terpotong (penutup PNG tidak ditemukan)")
            }

            val bmp = SkinStore.decodeDownsampled(temp, info.width, info.height)
                ?: return ImportResult.Failure(
                    "File $name tidak bisa didecode (rusak, atau memori tidak cukup)"
                )
            val decodedHasAlpha = bmp.hasAlpha()
            bmp.recycle()
            if (!decodedHasAlpha) {
                return ImportResult.Failure("File $name tidak transparan setelah didecode")
            }

            Files.move(
                temp.toPath(),
                SkinStore.avatarFile(context).toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
            val shownName = name.removeSuffix(".png").removeSuffix(".PNG").ifBlank { "avatar" }
            SkinStore.save(context, shownName, info.width, info.height)
            return ImportResult.Success(
                AvatarModel.singlePng(
                    SkinStore.IMPORTED_ID, shownName, SkinStore.AVATAR_FILE_NAME, info.width, info.height
                )
            )
        } catch (e: SecurityException) {
            return ImportResult.Failure("Akses ke file $name ditolak oleh sistem")
        } catch (e: IOException) {
            return ImportResult.Failure("Gagal membaca/menyimpan file $name: ${e.message ?: "kesalahan I/O"}")
        } finally {
            temp.delete()
        }
    }

    /** Salin ke [dest] maksimal MAX_FILE_BYTES + 1 byte; null bila stream tidak bisa dibuka. */
    private fun copyLimited(context: Context, uri: Uri, dest: File): Long? {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        var total = 0L
        input.use { src ->
            dest.outputStream().buffered().use { out ->
                val buf = ByteArray(16 * 1024)
                while (true) {
                    val n = src.read(buf)
                    if (n < 0) break
                    total += n
                    if (total > SkinLimits.MAX_FILE_BYTES) break // cukup untuk menolak; jangan salin lagi
                    out.write(buf, 0, n)
                }
            }
        }
        return total
    }

    private fun readTail(file: File): ByteArray {
        RandomAccessFile(file, "r").use { raf ->
            val len = raf.length()
            val n = minOf(len, TAIL_BYTES.toLong()).toInt()
            val tail = ByteArray(n)
            raf.seek(len - n)
            raf.readFully(tail)
            return tail
        }
    }

    private fun declaredSize(context: Context, uri: Uri): Long? = queryColumn(context, uri, OpenableColumns.SIZE) {
        if (!it.isNull(0)) it.getLong(0) else null
    }

    private fun displayName(context: Context, uri: Uri): String? =
        queryColumn(context, uri, OpenableColumns.DISPLAY_NAME) {
            if (!it.isNull(0)) it.getString(0) else null
        }?.takeIf { it.isNotBlank() }

    private fun <T> queryColumn(
        context: Context,
        uri: Uri,
        column: String,
        read: (android.database.Cursor) -> T?
    ): T? = try {
        context.contentResolver.query(uri, arrayOf(column), null, null, null)?.use { c ->
            if (c.moveToFirst()) read(c) else null
        }
    } catch (_: Exception) {
        null
    }
}
