package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.FixedExpenseDao
import com.driverfinance.data.local.entity.FixedExpenseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FixedExpenseRepository @Inject constructor(
    private val dao: FixedExpenseDao
) {

    fun getActiveExpenses(): Flow<List<FixedExpenseEntity>> =
        dao.getActive()

    suspend fun getById(id: String): FixedExpenseEntity? = dao.getById(id)

    suspend fun insert(expense: FixedExpenseEntity) = dao.insert(expense)

    suspend fun update(expense: FixedExpenseEntity) = dao.update(expense)

    suspend fun getTotalActive(): Int = dao.getTotalMonthlyFixedExpenses() ?: 0

    suspend fun hasAnyRecords(): Boolean = dao.getCount() > 0
}
