package com.carenest.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.carenest.data.source.local.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM current_user LIMIT 1")
    fun observeCurrentUser(): Flow<UserEntity?>

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("DELETE FROM current_user")
    suspend fun clear()

    @Transaction
    suspend fun replaceCurrentUser(user: UserEntity) {
        clear()
        upsert(user)
    }
}
