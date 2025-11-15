package com.strathmore.groupworkmanager.data.repository

import com.strathmore.groupworkmanager.data.dao.MemberDao
import com.strathmore.groupworkmanager.data.model.MemberEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for member operations.
 */
class MemberRepository(private val memberDao: MemberDao) {
    fun getMembersByGroupId(groupId: Int): Flow<List<MemberEntity>> = memberDao.getMembersByGroupId(groupId)

    fun getMemberById(memberId: Int): Flow<MemberEntity?> = memberDao.getMemberById(memberId)

    suspend fun insertMember(member: MemberEntity): Long = memberDao.insertMember(member)

    suspend fun updateMember(member: MemberEntity) = memberDao.updateMember(member)

    suspend fun deleteMember(member: MemberEntity) = memberDao.deleteMember(member)
}