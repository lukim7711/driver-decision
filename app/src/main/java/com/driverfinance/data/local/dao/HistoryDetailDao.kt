package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.HistoryDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detail: HistoryDetailEntity)

    @Update
    suspend fun update(detail: HistoryDetailEntity)

    @Query("SELECT * FROM history_details WHERE id = :id")
    suspend fun getById(id: String): HistoryDetailEntity?

    @Query("SELECT * FROM history_details WHERE history_trip_id = :historyTripId")
    fun getByHistoryTripId(historyTripId: String): Flow<List<HistoryDetailEntity>>

    @Query("SELECT * FROM history_details WHERE order_sn = :orderSn")
    suspend fun getByOrderSn(orderSn: String): HistoryDetailEntity?

    @Query("SELECT * FROM history_details WHERE linked_order_id IS NULL")
    suspend fun getUnlinked(): List<HistoryDetailEntity>

    @Query("SELECT SUM(hd.total_earning) FROM history_details hd INNER JOIN history_trips ht ON hd.history_trip_id = ht.id WHERE ht.trip_date = :date")
    suspend fun getTotalEarningByDate(date: String): Int?
}
