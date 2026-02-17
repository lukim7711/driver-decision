package com.driverfinance.ui.screen.obligation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.FixedExpense
import com.driverfinance.domain.model.FixedExpenseListScreenState
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
 * ViewModel for F009 Fixed Expense List screen.
 *
 * Business Logic (F009 spec 6.3 #1-3):
 * - CRUD operations for fixed expenses
 * - Soft delete (is_active = 0)
 * - Total = SUM(amount) WHERE is_active = 1
 *
 * Template Logic (F009 spec 6.3 #2):
 * - Show template if no records exist (including inactive)
 * - After template used, never show again
 *
 * TODO: Inject FixedExpenseRepository for real data.
 */
@HiltViewModel
class FixedExpenseListViewModel @Inject constructor() : ViewModel() {

    private val _screenState = MutableStateFlow(FixedExpenseListScreenState())
    val screenState: StateFlow<FixedExpenseListScreenState> = _screenState.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            try {
                _screenState.update { it.copy(isLoading = true) }
                delay(300)

                // TODO: Replace with fixedExpenseRepository.getActiveExpenses()
                // TODO: Check if template should show: fixedExpenseRepository.getTotalCount() == 0
                val expenses = getPlaceholderExpenses()
                val showTemplate = false // TODO: expenses total count (incl inactive) == 0

                _screenState.update {
                    it.copy(
                        expenses = expenses,
                        totalAmount = expenses.sumOf { e -> e.amount },
                        showTemplateSetup = showTemplate,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load fixed expenses")
                _screenState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal memuat biaya tetap")
                }
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            // TODO: fixedExpenseRepository.softDelete(expenseId)
            Timber.d("Soft delete expense: $expenseId")
            loadExpenses()
        }
    }

    fun refresh() {
        loadExpenses()
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }

    // ---- Placeholder ----

    private fun getPlaceholderExpenses(): List<FixedExpense> = listOf(
        FixedExpense(
            id = "fe-1", emoji = "\uD83D\uDCF1", name = "Pulsa / Paket Data",
            amount = 50_000, note = "Telkomsel + paket data 15GB",
            isActive = true, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
        ),
        FixedExpense(
            id = "fe-2", emoji = "\u26A1", name = "Listrik",
            amount = 200_000, note = null,
            isActive = true, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
        )
    )
}
