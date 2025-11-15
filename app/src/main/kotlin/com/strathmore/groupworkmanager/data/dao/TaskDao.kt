package com.strathmore.groupworkmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.strathmore.groupworkmanager.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for task operations. Tasks belong to a group and optionally a member.
 */
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE groupId = :groupId ORDER BY CASE WHEN deadline IS NULL THEN 1 ELSE 0 END, deadline ASC, priority DESC")
    fun getTasksByGroupId(groupId: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE groupId = :groupId AND title LIKE '%' || :searchQuery || '%' ORDER BY CASE WHEN deadline IS NULL THEN 1 ELSE 0 END, deadline ASC, priority DESC")
    fun searchTasksByTitle(groupId: Int, searchQuery: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}