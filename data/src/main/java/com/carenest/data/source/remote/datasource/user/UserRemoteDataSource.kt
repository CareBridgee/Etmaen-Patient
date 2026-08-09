package com.carenest.data.source.remote.datasource.user

import com.carenest.data.source.remote.dto.user.UpdateUserRequestDto
import com.carenest.data.source.remote.dto.user.UserResponseDto

interface UserRemoteDataSource {
    suspend fun getCurrentUser(): Result<UserResponseDto>
    suspend fun updateCurrentUser(request: UpdateUserRequestDto): Result<UserResponseDto>
}
