package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.AmbitiousModeDao
import com.driverfinance.data.local.entity.AmbitiousModeEntity
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmbitiousModeRepository @Inject constructor(
    private val dao: AmbitiousModeDao,
    private val debtRepository: DebtRepository
) {

    fun getAmbitiousMode(): Flow<AmbitiousModeEntity?> = dao.observe()

    suspend fun getAmbitiousModeSync(): AmbitiousModeEntity? = dao.get()

    suspend fun activate(targetMonths: Int): ActivateResult {
        val activeDebtCount = debtRepository.getActiveDebtCount()
        if (activeDebtCount == 0) return ActivateResult.NoActiveDebts

        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val existing = dao.get()

        if (existing != null) {
            dao.upsert(
                existing.copy(
                    isActive = 1,
                    targetMonths = targetMonths,
                    activatedAt = now,
                    deactivatedReason = null,
                    updatedAt = now
                )
            )
        } else {
            dao.upsert(
                AmbitiousModeEntity(
                    id = UUID.randomUUID().toString(),
                    isActive = 1,
                    targetMonths = targetMonths,
                    activatedAt = now,
                    deactivatedReason = null,
                    updatedAt = now
                )
            )
        }
        return ActivateResult.Success
    }

    suspend fun deactivateManual() {
        val existing = dao.get() ?: return
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        dao.upsert(
            existing.copy(
                isActive = 0,
                deactivatedReason = "MANUAL",
                updatedAt = now
            )
        )
    }

    /**
     * Exposed function for F006 (event-driven) and F007 (reactive fallback).
     * Returns true if ambitious mode was deactivated.
     */
    suspend fun checkAndDeactivateAmbitiousMode(): Boolean {
        val existing = dao.get() ?: return false
        if (existing.isActive != 1) return false

        val activeDebtCount = debtRepository.getActiveDebtCount()
        if (activeDebtCount == 0) {
            val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            dao.upsert(
                existing.copy(
                    isActive = 0,
                    deactivatedReason = "AUTO_ALL_PAID_OFF",
                    updatedAt = now
                )
            )
            return true
        }
        return false
    }

    sealed interface ActivateResult {
        data object Success : ActivateResult
        data object NoActiveDebts : ActivateResult
    }
}
