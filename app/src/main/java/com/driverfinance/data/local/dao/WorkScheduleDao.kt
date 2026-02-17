package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.WorkScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: WorkScheduleEntity)

    @Update
    suspend fun update(schedule: WorkScheduleEntity)

    @Query("SELECT * FROM work_schedules WHERE date = :date")
    suspend fun getByDate(date: String): WorkScheduleEntity?

    @Query("SELECT * FROM work_schedules WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getByDateRange(startDate: String, endDate: String): Flow<List<WorkScheduleEntity>>

    @Query("SELECT COUNT(*) FROM work_schedules WHERE date BETWEEN :startDate AND :endDate AND is_working = 1")
    suspend fun countWorkingDays(startDate: String, endDate: String): Int

    @Query("SELECT COUNT(*) FROM work_schedules WHERE date BETWEEN :startDate AND :endDate AND is_working = 1")
    fun observeWorkingDaysCount(startDate: String, endDate: String): Flow<Int>
}
