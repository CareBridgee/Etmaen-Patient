package com.carenest.data.source.remote.service

import com.carenest.data.BuildConfig
import com.carenest.data.source.remote.dto.cloudinary.CloudinaryResponseDto
import com.carenest.data.utils.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import javax.inject.Inject

import com.carenest.data.di.AuthHttpClient

interface CloudinaryApiService {
    suspend fun uploadImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray
    ): Result<String>
}

class CloudinaryApiServiceImpl @Inject constructor(
    @AuthHttpClient private val httpClient: HttpClient,
    private val json: Json
) : CloudinaryApiService {
    override suspend fun uploadImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray
    ): Result<String> = httpClient.executeRequest<CloudinaryResponseDto>(json) {
        method = HttpMethod.Post
        url(BuildConfig.cloudinary_upload_url)
        setBody(
            MultiPartFormDataContent(
                formData {
                    append(
                        key = "upload_preset",
                        value = BuildConfig.cloudinary_upload_preset,
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"upload_preset\"")
                        }
                    )
                    append(
                        key = "folder",
                        value = BuildConfig.cloudinary_asset_folder,
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"folder\"")
                        }
                    )
                    append(
                        key = "file",
                        value = bytes,
                        headers = Headers.build {
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"file\"; filename=\"${fileName.replace("\"", "")}\""
                            )
                            append(HttpHeaders.ContentType, contentType)
                        }
                    )
                }
            )
        )
    }.mapCatching { response ->
        response.secureUrl
            ?: response.url
            ?: error("Cloudinary upload response did not contain a secure_url")
    }
}
