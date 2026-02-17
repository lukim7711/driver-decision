package com.driverfinance.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Order : Screen("order")
    data object Chat : Screen("chat")
    data object QuickEntry : Screen("quick_entry")
    data object More : Screen("more")
    data object CaptureManager : Screen("capture_manager")
    data object DataReview : Screen("data_review")
    data object DebtList : Screen("debt_list")
    data object AddDebt : Screen("add_debt")
    data object FixedExpense : Screen("fixed_expense")
    data object WorkSchedule : Screen("work_schedule")
    data object TargetDetail : Screen("target_detail")

    data object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: String): String = "trip_detail/$tripId"
    }

    data object HistoryTripDetail : Screen("history_trip_detail/{historyTripId}") {
        fun createRoute(historyTripId: String): String = "history_trip_detail/$historyTripId"
    }

    data object DebtDetail : Screen("debt_detail/{debtId}") {
        fun createRoute(debtId: String): String = "debt_detail/$debtId"
    }
}
