package com.strathmore.groupworkmanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.strathmore.groupworkmanager.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for interacting with the user table. Since there is only ever one user
 * profile stored locally, the table will contain at most one entry.
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}