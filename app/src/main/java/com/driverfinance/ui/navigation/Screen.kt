package com.driverfinance.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Order : Screen("order")
    data object Chat : Screen("chat")
    data object QuickEntry : Screen("quick_entry")
    data object More : Screen("more")
}
