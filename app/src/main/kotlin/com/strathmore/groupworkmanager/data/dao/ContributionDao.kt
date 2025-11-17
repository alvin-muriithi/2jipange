package com.strathmore.groupworkmanager.data.dao

import androidx.room.*
import com.strathmore.groupworkmanager.data.model.ContributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributionDao {
    @Query("SELECT * FROM contributions WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getContributionsByTask(taskId: Int): Flow<List<ContributionEntity>>

    @Query("SELECT * FROM contributions WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getContributionsByMember(memberId: Int): Flow<List<ContributionEntity>>

    @Query("""
        SELECT * FROM contributions 
        WHERE taskId IN (SELECT id FROM tasks WHERE groupId = :groupId)
        ORDER BY timestamp DESC
    """)
    fun getGroupContributions(groupId: Int): Flow<List<ContributionEntity>>

    @Insert
    suspend fun insertContribution(contribution: ContributionEntity): Long

    @Update
    suspend fun updateContribution(contribution: ContributionEntity)

    @Delete
    suspend fun deleteContribution(contribution: ContributionEntity)
}