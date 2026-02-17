package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.driverfinance.data.local.entity.AmbitiousModeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AmbitiousModeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mode: AmbitiousModeEntity)

    @Query("SELECT * FROM ambitious_mode LIMIT 1")
    fun observe(): Flow<AmbitiousModeEntity?>

    @Query("SELECT * FROM ambitious_mode LIMIT 1")
    suspend fun get(): AmbitiousModeEntity?

    @Query("UPDATE ambitious_mode SET is_active = :isActive, deactivated_reason = :reason, updated_at = :updatedAt")
    suspend fun setActive(isActive: Int, reason: String?, updatedAt: String)
}
