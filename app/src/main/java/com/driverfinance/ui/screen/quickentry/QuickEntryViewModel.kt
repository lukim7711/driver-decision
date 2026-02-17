package com.driverfinance.ui.screen.quickentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.local.seed.QuickEntrySeedData
import com.driverfinance.domain.model.CategoryUiModel
import com.driverfinance.domain.model.EntryType
import com.driverfinance.domain.model.QuickEntryScreenState
import com.driverfinance.domain.model.SavedEntryInfo
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
 * ViewModel for F004 Quick Entry.
 *
 * State machine: CATEGORY_GRID → AMOUNT_PICKER → (auto-save or CUSTOM_NUMPAD → save)
 * After save: brief confirmation → back to CATEGORY_GRID (beruntun flow).
 *
 * TODO: Inject QuickEntryRepository when Room data layer is connected.
 */
@HiltViewModel
class QuickEntryViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val CONFIRM_DISPLAY_MS = 2000L
        private const val SAVE_DEBOUNCE_MS = 500L
        private const val MAX_CUSTOM_DIGITS = 9
    }

    private val _screenState = MutableStateFlow(QuickEntryScreenState())
    val screenState: StateFlow<QuickEntryScreenState> = _screenState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val categories = QuickEntrySeedData.getDefaultCategories()
        _screenState.update { it.copy(categories = categories) }
    }

    // ---- Tab ----

    fun switchTab(type: EntryType) {
        _screenState.update {
            it.copy(
                activeTab = type,
                selectedCategory = null,
                showNumpad = false,
                customAmountDigits = "",
                note = ""
            )
        }
    }

    // ---- Category ----

    fun selectCategory(category: CategoryUiModel) {
        _screenState.update {
            it.copy(
                selectedCategory = category,
                showNumpad = false,
                customAmountDigits = "",
                note = ""
            )
        }
    }

    // ---- Amount ----

    fun selectPresetAmount(amount: Int) {
        saveEntry(amount)
    }

    fun openCustomNumpad() {
        _screenState.update { it.copy(showNumpad = true, customAmountDigits = "") }
    }

    fun appendDigit(digit: String) {
        _screenState.update { state ->
            if (state.customAmountDigits.length + digit.length > MAX_CUSTOM_DIGITS) return@update state
            state.copy(customAmountDigits = state.customAmountDigits + digit)
        }
    }

    fun deleteLastDigit() {
        _screenState.update { it.copy(customAmountDigits = it.customAmountDigits.dropLast(1)) }
    }

    fun saveCustomAmount() {
        val amount = _screenState.value.customAmountValue
        if (amount <= 0) return
        saveEntry(amount)
    }

    // ---- Note ----

    fun updateNote(note: String) {
        _screenState.update { it.copy(note = note) }
    }

    // ---- Navigation ----

    fun goBackToGrid() {
        _screenState.update {
            it.copy(selectedCategory = null, showNumpad = false, customAmountDigits = "", note = "")
        }
    }

    fun goBackToPresets() {
        _screenState.update { it.copy(showNumpad = false, customAmountDigits = "") }
    }

    // ---- Save ----

    private fun saveEntry(amount: Int) {
        val state = _screenState.value
        if (state.isSaving) return
        val category = state.selectedCategory ?: return

        _screenState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                // TODO: repository.saveEntry(type, categoryId, amount, note, date, time)
                delay(SAVE_DEBOUNCE_MS)

                val saved = SavedEntryInfo(
                    categoryEmoji = category.emoji,
                    categoryName = category.name,
                    amount = amount,
                    note = state.note.takeIf { it.isNotBlank() }
                )

                _screenState.update { current ->
                    current.copy(
                        selectedCategory = null,
                        showNumpad = false,
                        customAmountDigits = "",
                        note = "",
                        isSaving = false,
                        lastSavedEntry = saved,
                        todayExpenseCount = if (current.activeTab == EntryType.EXPENSE) current.todayExpenseCount + 1 else current.todayExpenseCount,
                        todayExpenseTotal = if (current.activeTab == EntryType.EXPENSE) current.todayExpenseTotal + amount else current.todayExpenseTotal,
                        todayIncomeCount = if (current.activeTab == EntryType.INCOME) current.todayIncomeCount + 1 else current.todayIncomeCount,
                        todayIncomeTotal = if (current.activeTab == EntryType.INCOME) current.todayIncomeTotal + amount else current.todayIncomeTotal
                    )
                }

                delay(CONFIRM_DISPLAY_MS)
                _screenState.update { it.copy(lastSavedEntry = null) }

            } catch (e: Exception) {
                Timber.e(e, "Failed to save quick entry")
                _screenState.update { it.copy(isSaving = false) }
            }
        }
    }
}
