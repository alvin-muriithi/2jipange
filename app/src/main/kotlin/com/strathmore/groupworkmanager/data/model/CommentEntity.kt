package com.strathmore.groupworkmanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents a simple comment within a group. Comments contain a message
 * and timestamp. Each comment belongs to a group.
 */
@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupId: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)