package com.driverfinance.data.local.seed

import com.driverfinance.domain.model.CategoryUiModel
import com.driverfinance.domain.model.EntryType

/**
 * Default categories + presets for F004.
 * Ref: F004 spec Section 6.2 (Data seed default saat install).
 *
 * Note: System categories (Denda Hutang, Pembayaran Hutang) are NOT included here.
 * They are seeded separately in Room and never shown in the input grid.
 */
object QuickEntrySeedData {

    private val defaultPresets = listOf(5_000, 10_000, 15_000, 20_000, 25_000, 30_000, 50_000)

    fun getDefaultCategories(): List<CategoryUiModel> = listOf(
        // ---- Expense categories ----
        CategoryUiModel("cat_bensin", "Bensin", "\u26FD", EntryType.EXPENSE, defaultPresets),
        CategoryUiModel("cat_makan", "Makan", "\uD83C\uDF5A", EntryType.EXPENSE, defaultPresets),
        CategoryUiModel("cat_rokok", "Rokok", "\uD83D\uDEAC", EntryType.EXPENSE, defaultPresets),
        CategoryUiModel("cat_parkir", "Parkir", "\uD83C\uDD7F\uFE0F", EntryType.EXPENSE, defaultPresets),
        CategoryUiModel("cat_minum", "Minum", "\uD83E\uDD64", EntryType.EXPENSE, defaultPresets),
        CategoryUiModel("cat_lainnya_e", "Lainnya", "\uD83D\uDCE6", EntryType.EXPENSE, defaultPresets),
        // ---- Income categories ----
        CategoryUiModel("cat_tip", "Tip Cash", "\uD83D\uDCB5", EntryType.INCOME, defaultPresets),
        CategoryUiModel(
            "cat_transfer", "Transfer Masuk", "\uD83D\uDCB8", EntryType.INCOME,
            listOf(50_000, 100_000, 200_000, 500_000, 1_000_000)
        ),
        CategoryUiModel(
            "cat_sampingan", "Kerja Sampingan", "\uD83D\uDD27", EntryType.INCOME,
            listOf(50_000, 100_000, 150_000, 200_000, 300_000, 500_000)
        ),
        CategoryUiModel(
            "cat_lainnya_i", "Lainnya", "\uD83D\uDCB0", EntryType.INCOME,
            listOf(5_000, 10_000, 20_000, 50_000, 100_000)
        )
    )
}
