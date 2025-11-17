package com.strathmore.groupworkmanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.strathmore.groupworkmanager.data.dao.CommentDao
import com.strathmore.groupworkmanager.data.dao.GroupDao
import com.strathmore.groupworkmanager.data.dao.MemberDao
import com.strathmore.groupworkmanager.data.dao.TaskDao
import com.strathmore.groupworkmanager.data.dao.UserDao
import com.strathmore.groupworkmanager.data.model.CommentEntity
import com.strathmore.groupworkmanager.data.model.GroupEntity
import com.strathmore.groupworkmanager.data.model.MemberEntity
import com.strathmore.groupworkmanager.data.model.TaskEntity
import com.strathmore.groupworkmanager.data.model.UserEntity

/**
 * Main Room database for the app. This database stores all entities
 * representing the user profile, groups, members, tasks and comments.
 */
@Database(
    entities = [UserEntity::class,
                GroupEntity::class,
                MemberEntity::class,
                TaskEntity::class,
                CommentEntity::class
               ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GroupWorkDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun memberDao(): MemberDao
    abstract fun taskDao(): TaskDao
    abstract fun commentDao(): CommentDao
    abstract fun contributionDao(): ContributionDao
}