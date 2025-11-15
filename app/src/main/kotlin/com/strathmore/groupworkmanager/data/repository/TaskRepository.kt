package com.strathmore.groupworkmanager.data.repository

import com.strathmore.groupworkmanager.data.dao.TaskDao
import com.strathmore.groupworkmanager.data.model.TaskEntity
import com.strathmore.groupworkmanager.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository for task operations. Provides methods to observe, search and
 * update tasks within a group.
 */
class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksByGroupId(groupId: Int): Flow<List<TaskEntity>> = taskDao.getTasksByGroupId(groupId)

    fun searchTasks(groupId: Int, query: String): Flow<List<TaskEntity>> = taskDao.searchTasksByTitle(groupId, query)

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun markTaskStatus(task: TaskEntity, status: TaskStatus) {
        val updated = task.copy(status = status)
        taskDao.updateTask(updated)
    }

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
}