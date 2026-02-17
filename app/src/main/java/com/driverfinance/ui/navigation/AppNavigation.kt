package com.driverfinance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.driverfinance.ui.screen.capture.CaptureManagerScreen
import com.driverfinance.ui.screen.capture.HistoryTripDetailScreen
import com.driverfinance.ui.screen.capture.TripDetailScreen
import com.driverfinance.ui.screen.chat.ChatScreen
import com.driverfinance.ui.screen.dashboard.DashboardScreen
import com.driverfinance.ui.screen.debt.AddDebtScreen
import com.driverfinance.ui.screen.debt.DebtDetailScreen
import com.driverfinance.ui.screen.debt.DebtListScreen
import com.driverfinance.ui.screen.more.MoreScreen
import com.driverfinance.ui.screen.obligation.FixedExpenseScreen
import com.driverfinance.ui.screen.obligation.WorkScheduleScreen
import com.driverfinance.ui.screen.order.OrderScreen
import com.driverfinance.ui.screen.quickentry.QuickEntryScreen
import com.driverfinance.ui.screen.review.DataReviewScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) { DashboardScreen() }
        composable(Screen.Order.route) { OrderScreen() }
        composable(Screen.Chat.route) { ChatScreen() }
        composable(Screen.QuickEntry.route) {
            QuickEntryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.More.route) {
            MoreScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(Screen.CaptureManager.route) {
            CaptureManagerScreen(
                onBack = { navController.popBackStack() },
                onTripClick = { tripId ->
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                },
                onHistoryTripClick = { historyTripId ->
                    navController.navigate(Screen.HistoryTripDetail.createRoute(historyTripId))
                }
            )
        }
        composable(
            route = Screen.TripDetail.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            TripDetailScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.HistoryTripDetail.route,
            arguments = listOf(navArgument("historyTripId") { type = NavType.StringType })
        ) {
            HistoryTripDetailScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.DataReview.route) {
            DataReviewScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.DebtList.route) {
            DebtListScreen(
                onBack = { navController.popBackStack() },
                onDebtClick = { debtId ->
                    navController.navigate(Screen.DebtDetail.createRoute(debtId))
                },
                onAddDebt = { navController.navigate(Screen.AddDebt.route) }
            )
        }
        composable(
            route = Screen.DebtDetail.route,
            arguments = listOf(navArgument("debtId") { type = NavType.StringType })
        ) {
            DebtDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { /* TODO: edit mode */ }
            )
        }
        composable(Screen.AddDebt.route) {
            AddDebtScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.FixedExpense.route) {
            FixedExpenseScreen(
                onBack = { navController.popBackStack() },
                onAddExpense = { /* TODO: add expense form */ }
            )
        }
        composable(Screen.WorkSchedule.route) {
            WorkScheduleScreen(onBack = { navController.popBackStack() })
        }
    }
}
