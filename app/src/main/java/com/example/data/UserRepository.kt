package com.example.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val allUsers: Flow<List<User>> = userDao.getAllUsersFlow()
    val adminConfigFlow: Flow<AdminConfig?> = userDao.getAdminConfigFlow()

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUserById(userId: Long) {
        userDao.deleteUserById(userId)
    }

    suspend fun getAdminConfig(): AdminConfig? {
        return userDao.getAdminConfig()
    }

    suspend fun saveAdminConfig(config: AdminConfig) {
        userDao.insertAdminConfig(config)
    }
}
