package com.driverfinance.ui.screen.obligation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.AmbitiousModeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for F009 Ambitious Mode.
 *
 * F009 spec 6.3 #6-8:
 * - Activation guard: can't activate if no active debts
 * - Calculation: totalDebt ÷ targetMonths = ambitious installment
 * - MAX(ambitiousInstallment, normalInstallment) to prevent lower target
 * - Auto-off via checkAndDeactivateAmbitiousMode() called by F006/F007
 *
 * This is rendered as a section in F007 Detail Target screen,
 * but can also be standalone for the "Lain" tab.
 *
 * TODO: Inject AmbitiousModeRepository + DebtRepository.
 */
@HiltViewModel
class AmbitiousModeViewModel @Inject constructor() : ViewModel() {

    private val _screenState = MutableStateFlow(AmbitiousModeScreenState())
    val screenState: StateFlow<AmbitiousModeScreenState> = _screenState.asStateFlow()

    init {
        loadState()
    }

    private fun loadState() {
        viewModelScope.launch {
            try {
                _screenState.update { it.copy(isLoading = true) }
                delay(300)

                // TODO: Load from repositories:
                //   val ambitiousMode = ambitiousModeRepository.get()
                //   val activeDebts = debtRepository.getActiveDebts()
                //   val totalDebt = activeDebts.sumOf { it.remainingAmount }
                //   val normalInstallment = activeDebts.mapNotNull { it.monthlyInstallment }.sum()

                val totalDebt = 13_850_000L // Placeholder
                val normalInstallment = 1_400_000L
                val targetMonths = 6
                val ambitiousInstallment = totalDebt / targetMonths
                val effectiveInstallment = maxOf(ambitiousInstallment, normalInstallment)

                _screenState.update {
                    it.copy(
                        isActive = false,
                        targetMonths = targetMonths,
                        totalDebtRemaining = totalDebt,
                        normalMonthlyInstallment = normalInstallment,
                        ambitiousMonthlyInstallment = effectiveInstallment,
                        additionalPerMonth = effectiveInstallment - normalInstallment,
                        normalDailyTarget = 75_000, // Placeholder — F007 calculates
                        ambitiousDailyTarget = 116_288, // Placeholder — F007 calculates
                        hasActiveDebts = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load ambitious mode")
                _screenState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal memuat data")
                }
            }
        }
    }

    /**
     * Toggle ambitious mode on/off.
     * F009 spec 6.3 #6.
     */
    fun toggleAmbitiousMode() {
        val state = _screenState.value

        if (!state.isActive && !state.canActivate) {
            _screenState.update {
                it.copy(errorMessage = "Tidak ada hutang aktif untuk dipercepat")
            }
            return
        }

        val newActive = !state.isActive

        // TODO:
        //   if (newActive) ambitiousModeRepository.activate(targetMonths)
        //   else ambitiousModeRepository.deactivate(DeactivatedReason.MANUAL)
        Timber.d("Ambitious mode toggled: active=$newActive")

        _screenState.update { it.copy(isActive = newActive) }
        if (newActive) recalculate()
    }

    /**
     * Select preset month target.
     * F009 spec mockup E: [3] [6] [9] [12] bulan.
     */
    fun selectPresetMonths(months: Int) {
        _screenState.update { it.copy(targetMonths = months, customMonths = "") }
        recalculate()
    }

    /**
     * Set custom month target via text input.
     */
    fun updateCustomMonths(input: String) {
        val filtered = input.filter { it.isDigit() }
        val months = filtered.toIntOrNull() ?: return
        if (months in 1..120) {
            _screenState.update { it.copy(targetMonths = months, customMonths = filtered) }
            recalculate()
        } else {
            _screenState.update { it.copy(customMonths = filtered) }
        }
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }

    /**
     * Recalculate ambitious installment and daily target.
     * F009 spec 6.3 #7.
     */
    private fun recalculate() {
        _screenState.update { state ->
            val targetMonths = state.targetMonths.coerceAtLeast(1)
            val ambitiousInstallment = state.totalDebtRemaining / targetMonths
            val effectiveInstallment = maxOf(ambitiousInstallment, state.normalMonthlyInstallment)

            // TODO: Get real daily target from F007 calculation
            //   val dailyTarget = targetCalculator.calculate(
            //     monthlyObligation = effectiveInstallment + totalFixedExpenses,
            //     remainingWorkDays = workScheduleRepo.getRemainingWorkDays()
            //   )

            state.copy(
                ambitiousMonthlyInstallment = effectiveInstallment,
                additionalPerMonth = effectiveInstallment - state.normalMonthlyInstallment
            )
        }
    }

    /**
     * F009 spec 6.3 #8 — Function exposed for F006 and F007.
     *
     * checkAndDeactivateAmbitiousMode():
     * - Checks if any active debts remain
     * - If none → deactivate ambitious mode with AUTO_ALL_PAID_OFF
     * - Returns true if deactivated, false if debts remain
     *
     * TODO: Move to a shared service/use-case, not in ViewModel.
     * This is placed here for reference during UI phase.
     */
    fun checkAndDeactivateAmbitiousMode(): Boolean {
        // TODO: Implement with real repository:
        //   val activeDebtCount = debtRepository.countActive()
        //   if (activeDebtCount == 0 && ambitiousModeRepository.isActive()) {
        //     ambitiousModeRepository.deactivate(DeactivatedReason.AUTO_ALL_PAID_OFF)
        //     return true
        //   }
        //   return false
        Timber.d("checkAndDeactivateAmbitiousMode called")
        return false
    }
}
