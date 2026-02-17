package com.driverfinance.ui.screen.obligation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.DEFAULT_FIXED_EXPENSE_TEMPLATES
import com.driverfinance.domain.model.TemplateSetupScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for F009 Fixed Expense Template Setup.
 *
 * F009 spec 6.3 #2:
 * - Show once when no records exist in fixed_expenses
 * - Driver checks relevant items → app INSERTs each selected template
 * - After use, never show again (check COUNT including inactive)
 *
 * TODO: Inject FixedExpenseRepository.
 */
@HiltViewModel
class TemplateSetupViewModel @Inject constructor() : ViewModel() {

    private val _screenState = MutableStateFlow(TemplateSetupScreenState())
    val screenState: StateFlow<TemplateSetupScreenState> = _screenState.asStateFlow()

    fun toggleTemplate(index: Int) {
        _screenState.update { current ->
            val updated = current.templates.toMutableList()
            updated[index] = updated[index].copy(isSelected = !updated[index].isSelected)
            current.copy(templates = updated)
        }
    }

    fun confirmSelection(onComplete: () -> Unit) {
        viewModelScope.launch {
            _screenState.update { it.copy(isSaving = true) }

            val selected = _screenState.value.templates.filter { it.isSelected }

            // TODO: For each selected template:
            //   fixedExpenseRepository.add(FixedExpense(
            //     emoji = template.emoji,
            //     name = template.name,
            //     amount = 0, // Driver fills in later
            //     isActive = true
            //   ))
            Timber.d("Template selection: ${selected.size} items selected")
            selected.forEach { Timber.d("  ${it.emoji} ${it.name}") }

            _screenState.update { it.copy(isSaving = false) }
            onComplete()
        }
    }
}
