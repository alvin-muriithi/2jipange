package com.strathmore.groupworkmanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents an individual task within a group project.
 * A task can be assigned to a member, has a priority and status, and a deadline.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignedToMemberId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupId: Int,
    val title: String,
    val description: String,
    val assignedToMemberId: Int?,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val deadline: Long? = null,
    val status: TaskStatus = TaskStatus.PENDING
)

/**
 * Enum class representing task priority. Defaults to MEDIUM.
 */
enum class TaskPriority { LOW, MEDIUM, HIGH }

/**
 * Enum class representing task status. Defaults to PENDING.
 */
enum class TaskStatus { PENDING, IN_PROGRESS, COMPLETED }