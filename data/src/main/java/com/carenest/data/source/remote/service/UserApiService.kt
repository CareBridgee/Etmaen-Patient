package com.carenest.data.source.remote.service

import com.carenest.data.source.remote.dto.user.UpdateUserRequestDto
import com.carenest.data.source.remote.dto.user.UserResponseDto

interface UserApiService {
    suspend fun getCurrentUser(): Result<UserResponseDto>
    suspend fun updateCurrentUser(request: UpdateUserRequestDto): Result<UserResponseDto>
}
