package com.carenest.domain.repository

interface ImageUploader {
    suspend fun uploadImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray
    ): Result<String>
}
