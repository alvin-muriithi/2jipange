package com.strathmore.groupworkmanager.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strathmore.groupworkmanager.data.model.CommentEntity
import com.strathmore.groupworkmanager.data.model.GroupEntity
import com.strathmore.groupworkmanager.data.model.MemberEntity
import com.strathmore.groupworkmanager.data.model.TaskEntity
import com.strathmore.groupworkmanager.data.model.TaskPriority
import com.strathmore.groupworkmanager.data.model.TaskStatus
import com.strathmore.groupworkmanager.data.repository.CommentRepository
import com.strathmore.groupworkmanager.data.repository.GroupRepository
import com.strathmore.groupworkmanager.data.repository.MemberRepository
import com.strathmore.groupworkmanager.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel for a specific group detail screen. Provides flows
 * representing the group itself, its members, tasks and comments. Also
 * exposes operations to create tasks and comments and update task status.
 */
class GroupDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
    private val taskRepository: TaskRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    // groupId is passed through navigation arguments
    private val groupId: Int = checkNotNull(savedStateHandle["groupId"]) {
        "groupId is required"
    }

    val group: Flow<GroupEntity?> = groupRepository.getGroupById(groupId)
    val members: Flow<List<MemberEntity>> = memberRepository.getMembersByGroupId(groupId)
    val tasks: Flow<List<TaskEntity>> = taskRepository.getTasksByGroupId(groupId)
    val comments: Flow<List<CommentEntity>> = commentRepository.getCommentsByGroupId(groupId)

    fun addTask(
        title: String,
        description: String,
        assignedToMemberId: Int?,
        priority: TaskPriority,
        deadline: Long?
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                groupId = groupId,
                title = title,
                description = description,
                assignedToMemberId = assignedToMemberId,
                priority = priority,
                deadline = deadline,
                status = TaskStatus.PENDING
            )
            taskRepository.insertTask(task)
        }
    }

    fun markTaskStatus(task: TaskEntity, status: TaskStatus) {
        viewModelScope.launch { taskRepository.markTaskStatus(task, status) }
    }

    fun addComment(message: String) {
        viewModelScope.launch {
            val comment = CommentEntity(groupId = groupId, message = message)
            commentRepository.insertComment(comment)
        }
    }
}