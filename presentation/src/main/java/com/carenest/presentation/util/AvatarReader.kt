package com.carenest.presentation.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream

data class SelectedAvatar(
    val fileName: String,
    val contentType: String,
    val bytes: ByteArray
)

private const val MAX_AVATAR_BYTES = 5 * 1024 * 1024

fun Context.readAvatar(uri: Uri): SelectedAvatar {
    val resolver = contentResolver
    val fileName = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        }
        ?.takeIf(String::isNotBlank)
        ?: "profile.jpg"
    val contentType = resolver.getType(uri)?.takeIf { it.startsWith("image/") }
        ?: "image/jpeg"
    val bytes = resolver.openInputStream(uri)?.use { input ->
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var total = 0
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            total += count
            require(total <= MAX_AVATAR_BYTES) { "Selected image is too large" }
            output.write(buffer, 0, count)
        }
        output.toByteArray()
    } ?: error("Unable to read selected image")
    require(bytes.isNotEmpty()) { "Selected image is empty" }
    return SelectedAvatar(fileName, contentType, bytes)
}
