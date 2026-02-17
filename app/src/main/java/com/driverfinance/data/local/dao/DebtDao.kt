package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: DebtEntity)

    @Update
    suspend fun update(debt: DebtEntity)

    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getById(id: String): DebtEntity?

    @Query("SELECT * FROM debts WHERE status = 'ACTIVE' ORDER BY due_date_day ASC")
    fun getActiveDebts(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE status = 'ACTIVE' ORDER BY due_date_day ASC")
    suspend fun getActiveDebtsSync(): List<DebtEntity>

    @Query("SELECT * FROM debts WHERE status = :status ORDER BY created_at DESC")
    fun getByStatus(status: String): Flow<List<DebtEntity>>

    @Query("SELECT COUNT(*) FROM debts WHERE status = :status")
    suspend fun getCountByStatus(status: String): Int

    @Query("SELECT SUM(remaining_amount) FROM debts WHERE status = :status")
    suspend fun getTotalRemaining(status: String): Int?

    @Query("UPDATE debts SET status = 'DELETED', updated_at = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: String)

    @Query("UPDATE debts SET status = 'PAID_OFF', paid_off_at = :paidOffAt, updated_at = :updatedAt WHERE id = :id")
    suspend fun markPaidOff(id: String, paidOffAt: String, updatedAt: String)

    @Query("SELECT SUM(monthly_installment) FROM debts WHERE status = 'ACTIVE' AND monthly_installment IS NOT NULL")
    suspend fun getTotalMonthlyInstallment(): Int?

    @Query("SELECT COUNT(*) FROM debts WHERE status = 'ACTIVE'")
    fun getActiveCount(): Flow<Int>
}
