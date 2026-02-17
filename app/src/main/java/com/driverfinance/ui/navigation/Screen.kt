package com.driverfinance.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Order : Screen("order")
    data object Chat : Screen("chat")
    data object QuickEntry : Screen("quick_entry")
    data object More : Screen("more")
    data object CaptureManager : Screen("capture_manager")
    data object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: String) = "trip_detail/$tripId"
    }
    data object HistoryTripDetail : Screen("history_trip_detail/{historyTripId}") {
        fun createRoute(historyTripId: String) = "history_trip_detail/$historyTripId"
    }
}
