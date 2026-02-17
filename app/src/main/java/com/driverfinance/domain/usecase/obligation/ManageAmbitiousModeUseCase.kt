package com.driverfinance.domain.usecase.obligation

import com.driverfinance.data.local.entity.AmbitiousModeEntity
import com.driverfinance.data.repository.AmbitiousModeRepository
import com.driverfinance.data.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ManageAmbitiousModeUseCase @Inject constructor(
    private val ambitiousModeRepository: AmbitiousModeRepository,
    private val debtRepository: DebtRepository
) {

    fun getAmbitiousModeDisplay(): Flow<AmbitiousModeDisplay> {
        return combine(
            ambitiousModeRepository.getAmbitiousMode(),
            debtRepository.getActiveDebts()
        ) { mode, activeDebts ->
            val totalRemaining = activeDebts.sumOf { it.remainingAmount }
            val totalNormalInstallment = activeDebts.sumOf { it.monthlyInstallment ?: 0 }

            val ambitiousInstallment = if (mode != null && mode.isActive == 1 && mode.targetMonths > 0) {
                (totalRemaining + mode.targetMonths - 1) / mode.targetMonths
            } else null

            val effectiveInstallment = if (ambitiousInstallment != null) {
                maxOf(ambitiousInstallment, totalNormalInstallment)
            } else totalNormalInstallment

            AmbitiousModeDisplay(
                isActive = mode?.isActive == 1,
                targetMonths = mode?.targetMonths ?: 6,
                totalRemainingDebt = totalRemaining,
                normalInstallment = totalNormalInstallment,
                ambitiousInstallment = effectiveInstallment,
                additionalPerMonth = (effectiveInstallment - totalNormalInstallment).coerceAtLeast(0),
                hasActiveDebts = activeDebts.isNotEmpty()
            )
        }
    }

    suspend fun activate(targetMonths: Int): AmbitiousModeRepository.ActivateResult {
        return ambitiousModeRepository.activate(targetMonths)
    }

    suspend fun deactivate() {
        ambitiousModeRepository.deactivateManual()
    }

    /**
     * Exposed for F006 (event-driven) and F007 (reactive fallback).
     */
    suspend fun checkAndDeactivateAmbitiousMode(): Boolean {
        return ambitiousModeRepository.checkAndDeactivateAmbitiousMode()
    }
}

data class AmbitiousModeDisplay(
    val isActive: Boolean,
    val targetMonths: Int,
    val totalRemainingDebt: Int,
    val normalInstallment: Int,
    val ambitiousInstallment: Int,
    val additionalPerMonth: Int,
    val hasActiveDebts: Boolean
)
