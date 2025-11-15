package com.strathmore.groupworkmanager.data.repository

import com.strathmore.groupworkmanager.data.dao.GroupDao
import com.strathmore.groupworkmanager.data.dao.MemberDao
import com.strathmore.groupworkmanager.data.model.GroupEntity
import com.strathmore.groupworkmanager.data.model.MemberEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Repository for managing groups and their associated members.
 */
class GroupRepository(
    private val groupDao: GroupDao,
    private val memberDao: MemberDao
) {
    fun getAllGroups(): Flow<List<GroupEntity>> = groupDao.getAllGroups()

    fun getGroupById(groupId: Int): Flow<GroupEntity?> = groupDao.getGroupById(groupId)

    fun getMembersForGroup(groupId: Int): Flow<List<MemberEntity>> = memberDao.getMembersByGroupId(groupId)

    suspend fun createGroup(
        groupName: String,
        courseName: String,
        lecturerName: String?,
        members: List<Pair<String, String>> // list of name to role
    ): Long {
        val group = GroupEntity(groupName = groupName, courseName = courseName, lecturerName = lecturerName)
        val groupId = groupDao.insertGroup(group).toInt()
        // Insert members
        members.forEach { (name, role) ->
            memberDao.insertMember(MemberEntity(groupId = groupId, memberName = name, role = role))
        }
        return groupId.toLong()
    }

    suspend fun updateGroup(group: GroupEntity) = groupDao.updateGroup(group)

    suspend fun deleteGroup(group: GroupEntity) = groupDao.deleteGroup(group)
}