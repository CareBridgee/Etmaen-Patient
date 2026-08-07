package com.carenest.domain.repository

import com.carenest.domain.model.home.User
import com.carenest.domain.model.user.UserUpdate
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun refreshCurrentUser(): Result<User>
    suspend fun uploadProfileImage(fileName: String, contentType: String, bytes: ByteArray): Result<String>
    suspend fun updateCurrentUser(update: UserUpdate): Result<User>
    suspend fun clearCurrentUser()
}
