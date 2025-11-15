package com.strathmore.groupworkmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a project group. Each group can have multiple members,
 * tasks and comments. Groups are associated with a course or unit.
 */
@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupName: String,
    val courseName: String,
    val lecturerName: String? = null
)