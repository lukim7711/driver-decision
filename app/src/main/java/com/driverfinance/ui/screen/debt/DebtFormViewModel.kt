package com.driverfinance.ui.screen.debt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.DebtFormScreenState
import com.driverfinance.domain.model.DebtType
import com.driverfinance.domain.model.PenaltyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for F006 Add/Edit Debt form.
 *
 * F006 spec: Form fields vary by debt type.
 * - Cicilan Tetap: name, amounts, installment, due date
 * - Pinjol/Paylater: + penalty type & amount
 * - Personal: name, amounts, due date (optional), note
 *
 * TODO: Inject DebtRepository for save/update operations.
 */
@HiltViewModel
class DebtFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val editDebtId: String? = savedStateHandle.get<String>("debtId")

    private val _screenState = MutableStateFlow(
        DebtFormScreenState(isEditMode = editDebtId != null, debtId = editDebtId)
    )
    val screenState: StateFlow<DebtFormScreenState> = _screenState.asStateFlow()

    init {
        if (editDebtId != null) {
            loadDebtForEdit(editDebtId)
        }
    }

    private fun loadDebtForEdit(debtId: String) {
        viewModelScope.launch {
            // TODO: Load debt from repository and populate form
            Timber.d("Loading debt for edit: $debtId")
        }
    }

    // ---- Form Updates ----

    fun updateName(name: String) {
        _screenState.update { it.copy(name = name) }
    }

    fun updateDebtType(type: DebtType) {
        _screenState.update { current ->
            current.copy(
                debtType = type,
                // Reset fields not relevant to new type
                penaltyType = if (type == DebtType.PINJOL_PAYLATER) current.penaltyType else PenaltyType.NONE,
                penaltyAmount = if (type == DebtType.PINJOL_PAYLATER) current.penaltyAmount else "",
                monthlyInstallment = if (type == DebtType.PERSONAL) "" else current.monthlyInstallment
            )
        }
    }

    fun updateOriginalAmount(amount: String) {
        _screenState.update { it.copy(originalAmount = amount.filter { c -> c.isDigit() }) }
    }

    fun updateRemainingAmount(amount: String) {
        _screenState.update { it.copy(remainingAmount = amount.filter { c -> c.isDigit() }) }
    }

    fun updateMonthlyInstallment(amount: String) {
        _screenState.update { it.copy(monthlyInstallment = amount.filter { c -> c.isDigit() }) }
    }

    fun updateDueDateDay(day: String) {
        val filtered = day.filter { it.isDigit() }
        val dayInt = filtered.toIntOrNull()
        if (dayInt == null || dayInt in 1..31) {
            _screenState.update { it.copy(dueDateDay = filtered) }
        }
    }

    fun updatePenaltyType(type: PenaltyType) {
        _screenState.update { it.copy(penaltyType = type) }
    }

    fun updatePenaltyAmount(amount: String) {
        _screenState.update { it.copy(penaltyAmount = amount.filter { c -> c.isDigit() }) }
    }

    fun updateNote(note: String) {
        _screenState.update { it.copy(note = note) }
    }

    // ---- Save ----

    fun save() {
        val state = _screenState.value
        if (!state.canSave) return

        viewModelScope.launch {
            _screenState.update { it.copy(isSaving = true) }

            try {
                // TODO: Replace with debtRepository.addDebt() or debtRepository.updateDebt()
                Timber.d(
                    "Saving debt: name=${state.name}, type=${state.debtType}, " +
                            "original=${state.originalAmount}, remaining=${state.remainingAmount}"
                )

                _screenState.update {
                    it.copy(isSaving = false, savedSuccessfully = true)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save debt")
                _screenState.update {
                    it.copy(isSaving = false, errorMessage = "Gagal menyimpan. Coba lagi.")
                }
            }
        }
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }
}
