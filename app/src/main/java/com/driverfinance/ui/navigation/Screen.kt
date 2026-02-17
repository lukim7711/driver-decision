package com.driverfinance.ui.navigation

/**
 * All app routes as a sealed class.
 * Ref: CONSTITUTION Section 2 — must use Navigation Compose.
 */
sealed class Screen(val route: String, val title: String) {

    // ── Main Tabs (always visible in tab bar) ──
    data object Dashboard : Screen("dashboard", "Home")
    data object Order : Screen("order", "Order")
    data object Chat : Screen("chat", "AI")
    data object QuickEntry : Screen("quick_entry", "Input")
    data object More : Screen("more", "Lain")

    // ── F006: Debt Management ──
    data object DebtList : Screen("debt_list", "Hutang")
    data object DebtAdd : Screen("debt_add", "Tambah Hutang")
    data object DebtDetail : Screen("debt_detail/{debtId}", "Detail Hutang") {
        fun createRoute(debtId: String) = "debt_detail/$debtId"
    }
    data object DebtEdit : Screen("debt_edit/{debtId}", "Edit Hutang") {
        fun createRoute(debtId: String) = "debt_edit/$debtId"
    }

    // ── F009: Fixed Expenses ──
    data object FixedExpenseList : Screen("fixed_expense_list", "Biaya Tetap Bulanan")
    data object FixedExpenseAdd : Screen("fixed_expense_add", "Tambah Biaya Tetap")
    data object FixedExpenseEdit : Screen("fixed_expense_edit/{expenseId}", "Edit Biaya Tetap") {
        fun createRoute(expenseId: String) = "fixed_expense_edit/$expenseId"
    }
    data object FixedExpenseTemplate : Screen("fixed_expense_template", "Template Biaya Tetap")

    // ── F009: Work Schedule ──
    data object WorkSchedule : Screen("work_schedule", "Jadwal Kerja")

    // ── F007: Target ──
    data object TargetDetail : Screen("target_detail", "Detail Target")

    // ── Other Detail Screens ──
    data object CaptureManager : Screen("capture_manager", "Capture Manager")
    data object DataReview : Screen("data_review", "Data Perlu Dicek")
    data object Settings : Screen("settings", "Pengaturan")
    data object Setup : Screen("setup", "Setup Awal")

    companion object {
        /** Routes where the bottom tab bar should be visible. */
        val mainTabRoutes = setOf(
            Dashboard.route,
            Order.route,
            Chat.route,
            QuickEntry.route,
            More.route
        )
    }
}
