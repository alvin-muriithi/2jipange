package com.strathmore.groupworkmanager.data.repository

import com.strathmore.groupworkmanager.data.dao.CommentDao
import com.strathmore.groupworkmanager.data.model.CommentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for comment operations. Comments are tied to a group and
 * provide simple chat functionality.
 */
class CommentRepository(private val commentDao: CommentDao) {
    fun getCommentsByGroupId(groupId: Int): Flow<List<CommentEntity>> = commentDao.getCommentsByGroupId(groupId)

    suspend fun insertComment(comment: CommentEntity): Long = commentDao.insertComment(comment)

    suspend fun deleteComment(comment: CommentEntity) = commentDao.deleteComment(comment)
}