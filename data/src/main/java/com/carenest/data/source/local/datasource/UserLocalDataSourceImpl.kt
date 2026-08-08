package com.carenest.data.source.local.datasource

import com.carenest.data.source.local.database.CareNestDatabase
import com.carenest.data.source.local.database.dao.UserDao
import com.carenest.data.source.local.database.entity.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao,
    private val database: CareNestDatabase
) : UserLocalDataSource {
    override fun observeCurrentUser() = userDao.observeCurrentUser()

    override suspend fun upsertCurrentUser(user: UserEntity) =
        userDao.replaceCurrentUser(user)

    override suspend fun clearCurrentUser() {
        userDao.clear()
        runCatching { database.clearAllTables() }
    }
}
