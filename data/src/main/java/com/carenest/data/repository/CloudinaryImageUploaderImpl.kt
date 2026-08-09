package com.carenest.data.repository

import com.carenest.data.source.remote.service.CloudinaryApiService
import com.carenest.domain.repository.ImageUploader
import javax.inject.Inject

class CloudinaryImageUploaderImpl @Inject constructor(
    private val cloudinaryApiService: CloudinaryApiService
) : ImageUploader {
    override suspend fun uploadImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray
    ): Result<String> = cloudinaryApiService.uploadImage(fileName, contentType, bytes)
}
