package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.driverfinance.data.local.entity.AiCorrectionLogEntity

@Dao
interface AiCorrectionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AiCorrectionLogEntity)

    @Query("SELECT * FROM ai_corrections_log WHERE data_review_id = :reviewId")
    suspend fun getByReviewId(reviewId: String): List<AiCorrectionLogEntity>

    @Query("SELECT * FROM ai_corrections_log WHERE applied_to_pattern = 0")
    suspend fun getUnapplied(): List<AiCorrectionLogEntity>
}
