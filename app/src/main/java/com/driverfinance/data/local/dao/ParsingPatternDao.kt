package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.ParsingPatternEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParsingPatternDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pattern: ParsingPatternEntity)

    @Update
    suspend fun update(pattern: ParsingPatternEntity)

    @Query("SELECT * FROM parsing_patterns WHERE id = :id")
    suspend fun getById(id: String): ParsingPatternEntity?

    @Query("SELECT * FROM parsing_patterns WHERE is_active = 1 AND screen_type = :screenType ORDER BY accuracy DESC")
    suspend fun getActiveByScreenType(screenType: String): List<ParsingPatternEntity>

    @Query("SELECT * FROM parsing_patterns WHERE is_active = 1 ORDER BY accuracy DESC")
    fun getActivePatterns(): Flow<List<ParsingPatternEntity>>
}
