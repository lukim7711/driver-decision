package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.QuickEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: QuickEntryEntity)

    @Update
    suspend fun update(entry: QuickEntryEntity)

    @Delete
    suspend fun delete(entry: QuickEntryEntity)

    @Query("SELECT * FROM quick_entries WHERE id = :id")
    suspend fun getById(id: String): QuickEntryEntity?

    @Query("SELECT * FROM quick_entries WHERE entry_date = :date ORDER BY entry_time DESC")
    fun getByDate(date: String): Flow<List<QuickEntryEntity>>

    @Query("SELECT * FROM quick_entries WHERE entry_date = :date AND type = :type ORDER BY entry_time DESC")
    fun getByDateAndType(date: String, type: String): Flow<List<QuickEntryEntity>>

    @Query("SELECT SUM(amount) FROM quick_entries WHERE entry_date = :date AND type = :type")
    suspend fun getTotalByDateAndType(date: String, type: String): Int?

    @Query("SELECT SUM(amount) FROM quick_entries WHERE entry_date LIKE :dateLike AND type = :type")
    suspend fun getSumByDateAndType(dateLike: String, type: String): Int?

    @Query("SELECT COUNT(*) FROM quick_entries WHERE entry_date = :date AND type = :type")
    suspend fun getCountByDateAndType(date: String, type: String): Int

    @Query("SELECT SUM(amount) FROM quick_entries WHERE entry_date BETWEEN :startDate AND :endDate AND type = :type")
    suspend fun getTotalByDateRangeAndType(startDate: String, endDate: String, type: String): Int?
}
