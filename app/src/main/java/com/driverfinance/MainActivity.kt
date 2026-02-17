package com.driverfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.driverfinance.ui.components.AppTabBar
import com.driverfinance.ui.navigation.AppNavigation
import com.driverfinance.ui.navigation.Screen
import com.driverfinance.ui.theme.DriverFinanceTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriverFinanceTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val mainTabRoutes = listOf(
                    Screen.Dashboard.route,
                    Screen.Order.route,
                    Screen.Chat.route,
                    Screen.QuickEntry.route,
                    Screen.More.route
                )
                val showBottomBar = currentRoute == null || currentRoute in mainTabRoutes

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            AppTabBar(
                                currentRoute = currentRoute,
                                onTabSelected = { screen ->
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
