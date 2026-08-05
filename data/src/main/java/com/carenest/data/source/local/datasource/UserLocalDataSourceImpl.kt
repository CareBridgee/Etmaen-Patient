package com.carenest.data.source.local.datasource

import com.carenest.data.source.local.database.dao.UserDao
import com.carenest.data.source.local.database.entity.UserEntity
import javax.inject.Inject

class UserLocalDataSourceImpl @Inject constructor(
    private val userDao: UserDao
) : UserLocalDataSource {
    override fun observeCurrentUser() = userDao.observeCurrentUser()

    override suspend fun upsertCurrentUser(user: UserEntity) =
        userDao.replaceCurrentUser(user)

    override suspend fun clearCurrentUser() = userDao.clear()
}
