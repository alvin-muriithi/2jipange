package com.strathmore.groupworkmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.strathmore.groupworkmanager.data.model.GroupEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for group operations. Provides methods to create, update, delete and
 * query project groups.
 */
@Dao
interface GroupDao {
    @Query("SELECT * FROM groups ORDER BY id DESC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE id = :groupId LIMIT 1")
    fun getGroupById(groupId: Int): Flow<GroupEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity): Long

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Delete
    suspend fun deleteGroup(group: GroupEntity)
}