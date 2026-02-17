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
import com.driverfinance.ui.screen.more.MoreScreen
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
        composable(Screen.QuickEntry.route) { QuickEntryScreen() }
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
    }
}
