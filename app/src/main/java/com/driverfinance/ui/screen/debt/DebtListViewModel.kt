package com.driverfinance.ui.screen.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.Debt
import com.driverfinance.domain.model.DebtListScreenState
import com.driverfinance.domain.model.DebtStatus
import com.driverfinance.domain.model.DebtType
import com.driverfinance.domain.model.PenaltyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * ViewModel for F006 Debt List screen.
 *
 * TODO: Inject DebtRepository to load real data from Room.
 * Currently uses placeholder data for UI development.
 */
@HiltViewModel
class DebtListViewModel @Inject constructor() : ViewModel() {

    private val _screenState = MutableStateFlow(DebtListScreenState())
    val screenState: StateFlow<DebtListScreenState> = _screenState.asStateFlow()

    init {
        loadDebts()
    }

    private fun loadDebts() {
        viewModelScope.launch {
            try {
                _screenState.update { it.copy(isLoading = true) }
                delay(300) // Simulate DB query

                // TODO: Replace with debtRepository.getActiveDebts() + debtRepository.getPaidOffDebts()
                val activeDebts = getPlaceholderActiveDebts()
                val paidOffDebts = getPlaceholderPaidOffDebts()

                _screenState.update {
                    it.copy(
                        activeDebts = activeDebts,
                        paidOffDebts = paidOffDebts,
                        totalRemainingAmount = activeDebts.sumOf { d -> d.remainingAmount },
                        totalMonthlyInstallment = activeDebts.mapNotNull { d -> d.monthlyInstallment }.sum(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load debts")
                _screenState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal memuat data hutang")
                }
            }
        }
    }

    fun refresh() {
        loadDebts()
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }

    // ---- Placeholder Data ----

    private fun getPlaceholderActiveDebts(): List<Debt> = listOf(
        Debt(
            id = "debt-1",
            name = "Cicilan Motor Honda BeAT",
            debtType = DebtType.FIXED_INSTALLMENT,
            originalAmount = 17_000_000,
            remainingAmount = 12_350_000,
            monthlyInstallment = 650_000,
            dueDateDay = 15,
            penaltyType = PenaltyType.NONE,
            penaltyAmount = 0,
            note = null,
            status = DebtStatus.ACTIVE,
            paidOffAt = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ),
        Debt(
            id = "debt-2",
            name = "Pinjol Akulaku",
            debtType = DebtType.PINJOL_PAYLATER,
            originalAmount = 2_000_000,
            remainingAmount = 1_500_000,
            monthlyInstallment = 750_000,
            dueDateDay = 10,
            penaltyType = PenaltyType.DAILY,
            penaltyAmount = 15_000,
            note = null,
            status = DebtStatus.ACTIVE,
            paidOffAt = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ),
        Debt(
            id = "debt-3",
            name = "Hutang ke Budi",
            debtType = DebtType.PERSONAL,
            originalAmount = 1_000_000,
            remainingAmount = 700_000,
            monthlyInstallment = null,
            dueDateDay = 28,
            penaltyType = PenaltyType.NONE,
            penaltyAmount = 0,
            note = "Bayar kalau sudah ada",
            status = DebtStatus.ACTIVE,
            paidOffAt = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    )

    private fun getPlaceholderPaidOffDebts(): List<Debt> = listOf(
        Debt(
            id = "debt-4",
            name = "SPayLater",
            debtType = DebtType.PINJOL_PAYLATER,
            originalAmount = 500_000,
            remainingAmount = 0,
            monthlyInstallment = 500_000,
            dueDateDay = 5,
            penaltyType = PenaltyType.DAILY,
            penaltyAmount = 5_000,
            note = null,
            status = DebtStatus.PAID_OFF,
            paidOffAt = LocalDateTime.of(2026, 2, 3, 14, 30),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    )
}
