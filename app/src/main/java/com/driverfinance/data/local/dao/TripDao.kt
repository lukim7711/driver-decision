package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trip: TripEntity)

    @Update
    suspend fun update(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getById(id: String): TripEntity?

    @Query("SELECT * FROM trips WHERE trip_date = :date ORDER BY captured_at DESC")
    fun getByDate(date: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE trip_date = :date AND captured_at >= :cutoffTime ORDER BY captured_at DESC LIMIT 1")
    suspend fun getActiveTripWithinMinutes(date: String, cutoffTime: String): TripEntity?

    @Query("SELECT COUNT(*) FROM trips WHERE trip_date = :date")
    suspend fun countByDate(date: String): Int
}
