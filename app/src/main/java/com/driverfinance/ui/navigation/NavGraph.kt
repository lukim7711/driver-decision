package com.driverfinance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.driverfinance.ui.screen.chat.ChatScreen
import com.driverfinance.ui.screen.dashboard.DashboardScreen
import com.driverfinance.ui.screen.debt.DebtDetailScreen
import com.driverfinance.ui.screen.debt.DebtFormScreen
import com.driverfinance.ui.screen.debt.DebtListScreen
import com.driverfinance.ui.screen.more.MoreScreen
import com.driverfinance.ui.screen.more.SettingsScreen
import com.driverfinance.ui.screen.obligation.FixedExpenseFormScreen
import com.driverfinance.ui.screen.obligation.FixedExpenseListScreen
import com.driverfinance.ui.screen.obligation.TemplateSetupScreen
import com.driverfinance.ui.screen.obligation.WorkScheduleScreen
import com.driverfinance.ui.screen.order.OrderScreen
import com.driverfinance.ui.screen.quickentry.QuickEntryScreen
import com.driverfinance.ui.screen.target.TargetDetailScreen

/**
 * Main navigation graph.
 * Wires all feature screens with proper nav arguments.
 *
 * Ref: ARCHITECTURE.md Section 2 — ui/navigation/
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        // ══════════════════════════════════════════════
        // MAIN TABS
        // ══════════════════════════════════════════════

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        composable(Screen.Order.route) {
            OrderScreen(navController = navController)
        }

        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }

        composable(Screen.QuickEntry.route) {
            QuickEntryScreen(navController = navController)
        }

        composable(Screen.More.route) {
            MoreScreen(
                onDebtClick = { navController.navigate(Screen.DebtList.route) },
                onFixedExpenseClick = { navController.navigate(Screen.FixedExpenseList.route) },
                onWorkScheduleClick = { navController.navigate(Screen.WorkSchedule.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onCaptureManagerClick = { navController.navigate(Screen.CaptureManager.route) },
                onDataReviewClick = { navController.navigate(Screen.DataReview.route) }
            )
        }

        // ══════════════════════════════════════════════
        // F007: TARGET DETAIL
        // ══════════════════════════════════════════════

        composable(Screen.TargetDetail.route) {
            TargetDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ══════════════════════════════════════════════
        // F006: DEBT MANAGEMENT
        // ══════════════════════════════════════════════

        composable(Screen.DebtList.route) {
            DebtListScreen(
                onDebtClick = { debtId ->
                    navController.navigate(Screen.DebtDetail.createRoute(debtId))
                },
                onAddDebtClick = { navController.navigate(Screen.DebtAdd.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.DebtAdd.route) {
            DebtFormScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DebtDetail.route,
            arguments = listOf(navArgument("debtId") { type = NavType.StringType })
        ) {
            DebtDetailScreen(
                onEditClick = { debtId ->
                    navController.navigate(Screen.DebtEdit.createRoute(debtId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DebtEdit.route,
            arguments = listOf(navArgument("debtId") { type = NavType.StringType })
        ) {
            DebtFormScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        // ══════════════════════════════════════════════
        // F009: FIXED EXPENSES
        // ══════════════════════════════════════════════

        composable(Screen.FixedExpenseList.route) {
            FixedExpenseListScreen(
                onAddClick = { navController.navigate(Screen.FixedExpenseAdd.route) },
                onEditClick = { expenseId ->
                    navController.navigate(Screen.FixedExpenseEdit.createRoute(expenseId))
                },
                onTemplateSetup = { navController.navigate(Screen.FixedExpenseTemplate.route) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.FixedExpenseAdd.route) {
            FixedExpenseFormScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FixedExpenseEdit.route,
            arguments = listOf(navArgument("expenseId") { type = NavType.StringType })
        ) {
            FixedExpenseFormScreen(
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.FixedExpenseTemplate.route) {
            TemplateSetupScreen(
                onComplete = {
                    navController.popBackStack(Screen.FixedExpenseList.route, inclusive = false)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // ══════════════════════════════════════════════
        // F009: WORK SCHEDULE
        // ══════════════════════════════════════════════

        composable(Screen.WorkSchedule.route) {
            WorkScheduleScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ══════════════════════════════════════════════
        // SETTINGS
        // ══════════════════════════════════════════════

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ══════════════════════════════════════════════
        // PLACEHOLDERS (detail screens to be built)
        // ══════════════════════════════════════════════

        composable(Screen.CaptureManager.route) {
            // TODO: Build CaptureManager screen
        }

        composable(Screen.DataReview.route) {
            // TODO: Build DataReview screen
        }
    }
}
