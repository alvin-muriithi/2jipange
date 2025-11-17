package com.strathmore.groupworkmanager.di

import android.content.Context
import androidx.room.Room
import com.strathmore.groupworkmanager.data.database.GroupWorkDatabase
import com.strathmore.groupworkmanager.data.repository.CommentRepository
import com.strathmore.groupworkmanager.data.repository.ContributionRepository
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
    private val database: GroupWorkDatabase = Room.databaseBuilder(
        context.applicationContext,
        GroupWorkDatabase::class.java,
        "groupwork_database"
    ).build()

    val userRepository: UserRepository by lazy { UserRepository(database.userDao()) }
    val groupRepository: GroupRepository by lazy { GroupRepository(database.groupDao(), database.memberDao()) }
    val memberRepository: MemberRepository by lazy { MemberRepository(database.memberDao()) }
    val taskRepository: TaskRepository by lazy { TaskRepository(database.taskDao()) }
    val commentRepository: CommentRepository by lazy { CommentRepository(database.commentDao()) }
    val contributionRepository: ContributionRepository by lazy { ContributionRepository(database.contributionDao()) }
}