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

    // ── Detail Screens (tab bar hidden) ──
    data object DebtManagement : Screen("debt_management", "Hutang")
    data object DebtAdd : Screen("debt_add", "Tambah Hutang")
    data object DebtDetail : Screen("debt_detail/{debtId}", "Detail Hutang") {
        fun createRoute(debtId: String) = "debt_detail/$debtId"
    }
    data object Obligation : Screen("obligation", "Kewajiban & Jadwal")
    data object CaptureManager : Screen("capture_manager", "Capture Manager")
    data object DataReview : Screen("data_review", "Data Perlu Dicek")
    data object TargetDetail : Screen("target_detail", "Detail Target")
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
