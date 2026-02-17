package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.driverfinance.data.local.entity.DebtPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtPaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: DebtPaymentEntity)

    @Query("SELECT * FROM debt_payments WHERE id = :id")
    suspend fun getById(id: String): DebtPaymentEntity?

    @Query("SELECT * FROM debt_payments WHERE debt_id = :debtId ORDER BY payment_date DESC")
    fun getByDebtId(debtId: String): Flow<List<DebtPaymentEntity>>

    @Query("SELECT SUM(amount) FROM debt_payments WHERE debt_id = :debtId")
    suspend fun getTotalPaidByDebtId(debtId: String): Int?

    @Query("SELECT SUM(amount) FROM debt_payments WHERE payment_date = :date")
    suspend fun getTotalPaidByDate(date: String): Int?
}
