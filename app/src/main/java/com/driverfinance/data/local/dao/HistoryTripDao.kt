package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.HistoryTripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryTripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: HistoryTripEntity)

    @Update
    suspend fun update(trip: HistoryTripEntity)

    @Query("SELECT * FROM history_trips WHERE id = :id")
    suspend fun getById(id: String): HistoryTripEntity?

    @Query("SELECT * FROM history_trips WHERE trip_date = :date ORDER BY trip_time DESC")
    fun getByDate(date: String): Flow<List<HistoryTripEntity>>

    @Query("SELECT * FROM history_trips WHERE linked_trip_id IS NULL AND trip_date = :date")
    suspend fun getUnlinkedByDate(date: String): List<HistoryTripEntity>

    @Query("SELECT SUM(total_earning) FROM history_trips WHERE trip_date = :date")
    suspend fun getTotalEarningByDate(date: String): Int?
}
