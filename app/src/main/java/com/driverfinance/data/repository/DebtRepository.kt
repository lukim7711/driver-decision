package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.DebtDao
import com.driverfinance.data.local.dao.DebtPaymentDao
import com.driverfinance.data.local.entity.DebtEntity
import com.driverfinance.data.local.entity.DebtPaymentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtRepository @Inject constructor(
    private val debtDao: DebtDao,
    private val paymentDao: DebtPaymentDao
) {

    // --- Debts ---

    fun getActiveDebts(): Flow<List<DebtEntity>> = debtDao.getByStatus("ACTIVE")

    suspend fun getActiveDebtsSync(): List<DebtEntity> = debtDao.getActiveDebtsSync()

    fun getPaidOffDebts(): Flow<List<DebtEntity>> = debtDao.getByStatus("PAID_OFF")

    suspend fun getDebtById(id: String): DebtEntity? = debtDao.getById(id)

    suspend fun insertDebt(debt: DebtEntity) = debtDao.insert(debt)

    suspend fun updateDebt(debt: DebtEntity) = debtDao.update(debt)

    suspend fun getActiveDebtCount(): Int = debtDao.getCountByStatus("ACTIVE")

    suspend fun getTotalRemainingAmount(): Int = debtDao.getTotalRemaining("ACTIVE") ?: 0

    suspend fun getTotalMonthlyInstallment(): Int = debtDao.getTotalMonthlyInstallment() ?: 0

    // --- Payments ---

    fun getPaymentsByDebtId(debtId: String): Flow<List<DebtPaymentEntity>> =
        paymentDao.getByDebtId(debtId)

    suspend fun insertPayment(payment: DebtPaymentEntity) = paymentDao.insert(payment)

    suspend fun getLastPenaltyPaymentDate(debtId: String): String? =
        paymentDao.getLastPaymentDateByType(debtId, "PENALTY")

    suspend fun getNonExpensePaymentsForMonth(yearMonth: String): Int =
        paymentDao.getSumNonExpenseByMonth(yearMonth) ?: 0
}
