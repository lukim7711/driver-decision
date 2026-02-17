package com.driverfinance.domain.model

/** Entry type — maps to quick_entries.type column. */
enum class EntryType(val value: String) {
    EXPENSE("EXPENSE"),
    INCOME("INCOME")
}

/** UI model for a category in the grid. */
data class CategoryUiModel(
    val id: String,
    val name: String,
    val emoji: String,
    val type: EntryType,
    val presetAmounts: List<Int>
)

/** Confirmation info after saving an entry. */
data class SavedEntryInfo(
    val categoryEmoji: String,
    val categoryName: String,
    val amount: Int,
    val note: String?
)

/** Input phases for the Quick Entry screen. */
enum class InputPhase {
    CATEGORY_GRID,
    AMOUNT_PICKER,
    CUSTOM_NUMPAD
}

/**
 * Single state object for F004 Quick Entry screen.
 * Phase is derived from selectedCategory + showNumpad.
 * CONSTITUTION: no var — immutable data class, updated via copy().
 */
data class QuickEntryScreenState(
    val activeTab: EntryType = EntryType.EXPENSE,
    val categories: List<CategoryUiModel> = emptyList(),
    val selectedCategory: CategoryUiModel? = null,
    val showNumpad: Boolean = false,
    val customAmountDigits: String = "",
    val note: String = "",
    val todayExpenseCount: Int = 0,
    val todayExpenseTotal: Int = 0,
    val todayIncomeCount: Int = 0,
    val todayIncomeTotal: Int = 0,
    val isSaving: Boolean = false,
    val lastSavedEntry: SavedEntryInfo? = null
) {
    val phase: InputPhase
        get() = when {
            selectedCategory == null -> InputPhase.CATEGORY_GRID
            showNumpad -> InputPhase.CUSTOM_NUMPAD
            else -> InputPhase.AMOUNT_PICKER
        }

    val filteredCategories: List<CategoryUiModel>
        get() = categories.filter { it.type == activeTab }

    val customAmountValue: Int
        get() = customAmountDigits.toIntOrNull() ?: 0

    val todayCount: Int
        get() = if (activeTab == EntryType.EXPENSE) todayExpenseCount else todayIncomeCount

    val todayTotal: Int
        get() = if (activeTab == EntryType.EXPENSE) todayExpenseTotal else todayIncomeTotal

    val todayLabel: String
        get() = if (activeTab == EntryType.EXPENSE) "pengeluaran" else "pemasukan"
}
