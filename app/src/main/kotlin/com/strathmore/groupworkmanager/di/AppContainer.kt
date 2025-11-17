package com.strathmore.groupworkmanager.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.strathmore.groupworkmanager.data.database.GroupWorkDatabase
import com.strathmore.groupworkmanager.data.repository.CommentRepository
import com.strathmore.groupworkmanager.data.repository.GroupRepository
import com.strathmore.groupworkmanager.data.repository.MemberRepository
import com.strathmore.groupworkmanager.data.repository.TaskRepository
import com.strathmore.groupworkmanager.data.repository.UserRepository

/**
 * A simple dependency container that provides instances of the database and
 * repositories. This is a lightweight alternative to a full dependency
 * injection framework. It should be created in the [android.app.Application]
 * class or Activity and passed down to Composables as needed.
 */
class AppContainer(context: Context) {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS contributions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    taskId INTEGER NOT NULL,
                    memberId INTEGER NOT NULL,
                    contributionType TEXT NOT NULL,
                    description TEXT NOT NULL,
                    hoursSpent REAL,
                    timestamp INTEGER NOT NULL,
                    verifiedByLeader INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(taskId) REFERENCES tasks(id) ON DELETE CASCADE,
                    FOREIGN KEY(memberId) REFERENCES members(id) ON DELETE CASCADE
                )
            """)
        }
    }
    private val database: GroupWorkDatabase = Room.databaseBuilder(
        context.applicationContext,
        GroupWorkDatabase::class.java,
        "groupwork_database"
    ).build()

        .addMigrations(MIGRATION_1_2)  // Add this line
        .build()

    val userRepository: UserRepository by lazy { UserRepository(database.userDao()) }
    val groupRepository: GroupRepository by lazy { GroupRepository(database.groupDao(), database.memberDao()) }
    val memberRepository: MemberRepository by lazy { MemberRepository(database.memberDao()) }
    val taskRepository: TaskRepository by lazy { TaskRepository(database.taskDao()) }
    val commentRepository: CommentRepository by lazy { CommentRepository(database.commentDao()) }

    val contributionRepository: ContributionRepository by lazy {
        ContributionRepository(database.contributionDao())
    }
}