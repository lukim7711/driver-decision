package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.CapturedOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CapturedOrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: CapturedOrderEntity)

    @Update
    suspend fun update(order: CapturedOrderEntity)

    @Query("SELECT * FROM captured_orders WHERE id = :id")
    suspend fun getById(id: String): CapturedOrderEntity?

    @Query("SELECT * FROM captured_orders WHERE trip_id = :tripId")
    fun getByTripId(tripId: String): Flow<List<CapturedOrderEntity>>

    @Query("SELECT * FROM captured_orders WHERE order_sn = :orderSn")
    suspend fun getByOrderSn(orderSn: String): CapturedOrderEntity?
}
