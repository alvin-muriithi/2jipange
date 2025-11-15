package com.strathmore.groupworkmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.strathmore.groupworkmanager.data.model.CommentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for comments. Comments are simple messages tied to a group.
 */
@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE groupId = :groupId ORDER BY timestamp ASC")
    fun getCommentsByGroupId(groupId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Delete
    suspend fun deleteComment(comment: CommentEntity)
}