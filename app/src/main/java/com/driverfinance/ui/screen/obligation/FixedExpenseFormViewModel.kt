package com.driverfinance.ui.screen.obligation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.FixedExpenseFormScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for F009 Add/Edit Fixed Expense form.
 *
 * F009 spec mockup B: emoji, name, amount, note.
 * TODO: Inject FixedExpenseRepository.
 */
@HiltViewModel
class FixedExpenseFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val editExpenseId: String? = savedStateHandle.get<String>("expenseId")

    private val _screenState = MutableStateFlow(
        FixedExpenseFormScreenState(isEditMode = editExpenseId != null, expenseId = editExpenseId)
    )
    val screenState: StateFlow<FixedExpenseFormScreenState> = _screenState.asStateFlow()

    init {
        if (editExpenseId != null) {
            loadExpenseForEdit(editExpenseId)
        }
    }

    private fun loadExpenseForEdit(id: String) {
        viewModelScope.launch {
            // TODO: fixedExpenseRepository.getById(id) -> populate form
            Timber.d("Loading fixed expense for edit: $id")
        }
    }

    fun updateEmoji(emoji: String) {
        _screenState.update { it.copy(emoji = emoji) }
    }

    fun updateName(name: String) {
        _screenState.update { it.copy(name = name) }
    }

    fun updateAmount(amount: String) {
        _screenState.update { it.copy(amount = amount.filter { c -> c.isDigit() }) }
    }

    fun updateNote(note: String) {
        _screenState.update { it.copy(note = note) }
    }

    fun save() {
        val state = _screenState.value
        if (!state.canSave) return

        viewModelScope.launch {
            _screenState.update { it.copy(isSaving = true) }
            try {
                // TODO: fixedExpenseRepository.add() or .update()
                Timber.d("Saving fixed expense: ${state.emoji} ${state.name} Rp${state.amount}")
                _screenState.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save fixed expense")
                _screenState.update { it.copy(isSaving = false, errorMessage = "Gagal menyimpan") }
            }
        }
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }
}
