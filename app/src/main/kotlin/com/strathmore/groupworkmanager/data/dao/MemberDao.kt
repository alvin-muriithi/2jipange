package com.strathmore.groupworkmanager.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.strathmore.groupworkmanager.data.model.MemberEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for member operations. Members are associated with a group via
 * groupId foreign key.
 */
@Dao
interface MemberDao {
    @Query("SELECT * FROM members WHERE groupId = :groupId ORDER BY id ASC")
    fun getMembersByGroupId(groupId: Int): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE id = :memberId LIMIT 1")
    fun getMemberById(memberId: Int): Flow<MemberEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)
}