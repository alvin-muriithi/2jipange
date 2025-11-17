package com.strathmore.groupworkmanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "contributions",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ContributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val memberId: Int,
    val contributionType: String, // e.g., "COMPLETED_TASK", "RESEARCH"
    val description: String,
    val hoursSpent: Double? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val verifiedByLeader: Boolean = false
)