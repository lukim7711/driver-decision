package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.FixedExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FixedExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: FixedExpenseEntity)

    @Update
    suspend fun update(expense: FixedExpenseEntity)

    @Query("SELECT * FROM fixed_expenses WHERE id = :id")
    suspend fun getById(id: String): FixedExpenseEntity?

    @Query("SELECT * FROM fixed_expenses WHERE is_active = 1 ORDER BY created_at ASC")
    fun getActive(): Flow<List<FixedExpenseEntity>>

    @Query("UPDATE fixed_expenses SET is_active = 0, updated_at = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: String)

    @Query("SELECT SUM(amount) FROM fixed_expenses WHERE is_active = 1")
    suspend fun getTotalMonthlyFixedExpenses(): Int?

    @Query("SELECT SUM(amount) FROM fixed_expenses WHERE is_active = 1")
    fun observeTotalMonthlyFixedExpenses(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM fixed_expenses WHERE is_active = 1")
    suspend fun getCount(): Int
}
