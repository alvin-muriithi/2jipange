package com.strathmore.groupworkmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single user of the app.
 * The app stores only one user profile locally, since there is no
 * back‑end or authentication service in this project.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val university: String = "Strathmore University"
)