package com.strathmore.groupworkmanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represents a single member of a group. Members belong to a group
 * and can be assigned to tasks. Roles are stored as free‑text (e.g. "Leader", "Developer").
 */
@Entity(
    tableName = "members",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupId: Int,
    val memberName: String,
    val role: String
)