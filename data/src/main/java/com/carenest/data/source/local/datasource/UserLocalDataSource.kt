package com.carenest.data.source.local.datasource

import com.carenest.data.source.local.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    fun observeCurrentUser(): Flow<UserEntity?>
    suspend fun upsertCurrentUser(user: UserEntity)
    suspend fun clearCurrentUser()
}
