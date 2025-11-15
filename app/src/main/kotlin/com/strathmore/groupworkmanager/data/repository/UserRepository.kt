package com.strathmore.groupworkmanager.data.repository

import com.strathmore.groupworkmanager.data.dao.UserDao
import com.strathmore.groupworkmanager.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for user profile operations. Abstracts the [UserDao] and
 * provides a simple API for reading and writing the local user profile.
 */
class UserRepository(private val userDao: UserDao) {
    fun getUser(): Flow<UserEntity?> = userDao.getUser()

    suspend fun saveUser(name: String) {
        val user = UserEntity(fullName = name)
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
}