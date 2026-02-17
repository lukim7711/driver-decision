package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.driverfinance.data.local.entity.DailyTargetCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyTargetCacheDao {

    @Query("SELECT * FROM daily_target_cache WHERE target_date = :date LIMIT 1")
    fun getByDate(date: String): Flow<DailyTargetCacheEntity?>

    @Query("SELECT * FROM daily_target_cache WHERE target_date = :date LIMIT 1")
    suspend fun getByDateSync(date: String): DailyTargetCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyTargetCacheEntity)

    @Query("DELETE FROM daily_target_cache WHERE target_date = :date")
    suspend fun deleteByDate(date: String)
}
