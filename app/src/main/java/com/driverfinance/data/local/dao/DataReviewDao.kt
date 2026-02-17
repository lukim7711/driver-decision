package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.DataReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DataReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: DataReviewEntity)

    @Update
    suspend fun update(review: DataReviewEntity)

    @Query("SELECT * FROM data_reviews WHERE id = :id")
    suspend fun getById(id: String): DataReviewEntity?

    @Query("SELECT * FROM data_reviews WHERE review_status = :status ORDER BY created_at DESC")
    fun getByStatus(status: String): Flow<List<DataReviewEntity>>

    @Query("SELECT * FROM data_reviews WHERE review_status = 'PENDING' ORDER BY created_at DESC")
    fun getPending(): Flow<List<DataReviewEntity>>

    @Query("SELECT COUNT(*) FROM data_reviews WHERE review_status = 'PENDING' AND created_at LIKE :date || '%'")
    suspend fun getTodayPendingCount(date: String): Int

    @Query("SELECT COUNT(*) FROM data_reviews WHERE review_status = 'PENDING'")
    fun getPendingCount(): Flow<Int>
}
