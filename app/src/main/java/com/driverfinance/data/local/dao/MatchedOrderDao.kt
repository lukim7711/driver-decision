package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.MatchedOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchedOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(match: MatchedOrderEntity)

    @Update
    suspend fun update(match: MatchedOrderEntity)

    @Query("SELECT * FROM matched_orders WHERE id = :id")
    suspend fun getById(id: String): MatchedOrderEntity?

    @Query("SELECT * FROM matched_orders WHERE match_status = :status")
    fun getByStatus(status: String): Flow<List<MatchedOrderEntity>>

    @Query("SELECT * FROM matched_orders WHERE match_status = :status")
    suspend fun getByStatusSync(status: String): List<MatchedOrderEntity>

    @Query("SELECT * FROM matched_orders WHERE captured_order_id = :id")
    suspend fun getByCapturedOrderId(id: String): MatchedOrderEntity?

    @Query("SELECT * FROM matched_orders WHERE history_detail_id = :id")
    suspend fun getByHistoryDetailId(id: String): MatchedOrderEntity?

    @Query("SELECT * FROM matched_orders WHERE captured_order_id = :orderId OR history_detail_id = :orderId")
    suspend fun getByOrderId(orderId: String): MatchedOrderEntity?
}
