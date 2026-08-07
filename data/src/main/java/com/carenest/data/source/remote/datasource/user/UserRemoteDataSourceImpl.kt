package com.carenest.data.source.remote.datasource.user

import com.carenest.data.source.remote.dto.user.UpdateUserRequestDto
import com.carenest.data.source.remote.service.UserApiService
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val api: UserApiService
) : UserRemoteDataSource {
    override suspend fun getCurrentUser() = api.getCurrentUser()

    override suspend fun uploadProfileImage(
        fileName: String,
        contentType: String,
        bytes: ByteArray
    ) = api.uploadProfileImage(fileName, contentType, bytes)

    override suspend fun updateCurrentUser(request: UpdateUserRequestDto) =
        api.updateCurrentUser(request)
}
