package com.strathmore.groupworkmanager.data.repository

import com.strathmore.groupworkmanager.data.dao.ContributionDao
import com.strathmore.groupworkmanager.data.model.ContributionEntity
import kotlinx.coroutines.flow.Flow

class ContributionRepository(private val contributionDao: ContributionDao) {
    fun getContributionsByTask(taskId: Int): Flow<List<ContributionEntity>> =
        contributionDao.getContributionsByTask(taskId)

    fun getContributionsByMember(memberId: Int): Flow<List<ContributionEntity>> =
        contributionDao.getContributionsByMember(memberId)

    fun getGroupContributions(groupId: Int): Flow<List<ContributionEntity>> =
        contributionDao.getGroupContributions(groupId)

    suspend fun insertContribution(contribution: ContributionEntity): Long =
        contributionDao.insertContribution(contribution)

    suspend fun updateContribution(contribution: ContributionEntity) =
        contributionDao.updateContribution(contribution)

    suspend fun deleteContribution(contribution: ContributionEntity) =
        contributionDao.deleteContribution(contribution)
}