package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.user.UpdateUserRequestDto
import com.carenest.data.source.remote.dto.user.UserResponseDto

interface UserApiService {
    suspend fun getCurrentUser(): Result<UserResponseDto>
    suspend fun uploadProfileImage(fileName: String, contentType: String, bytes: ByteArray): Result<String>
    suspend fun updateCurrentUser(request: UpdateUserRequestDto): Result<UserResponseDto>
}
