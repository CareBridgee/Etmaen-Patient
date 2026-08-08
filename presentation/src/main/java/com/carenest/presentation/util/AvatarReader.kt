package com.carenest.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream

data class SelectedAvatar(
    val fileName: String,
    val contentType: String,
    val bytes: ByteArray
)

fun Context.readAvatar(uri: Uri): SelectedAvatar {
    val resolver = contentResolver
    val rawFileName = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
        ?.takeIf(String::isNotBlank)
        ?: "profile.jpg"

    val bytes = resolver.openInputStream(uri)?.use { input ->
        val originalBitmap = BitmapFactory.decodeStream(input)
            ?: error("Unable to decode selected image")

        val maxDimension = maxOf(originalBitmap.width, originalBitmap.height)
        val scaledBitmap = if (maxDimension > 1024) {
            val scale = 1024f / maxDimension
            val newWidth = (originalBitmap.width * scale).toInt()
            val newHeight = (originalBitmap.height * scale).toInt()
            Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        } else {
            originalBitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        if (scaledBitmap != originalBitmap) {
            scaledBitmap.recycle()
        }
        originalBitmap.recycle()
        outputStream.toByteArray()
    } ?: error("Unable to read selected image")

    require(bytes.isNotEmpty()) { "Selected image is empty" }
    val cleanName = rawFileName.substringBeforeLast('.') + ".jpg"
    return SelectedAvatar(cleanName, "image/jpeg", bytes)
}
