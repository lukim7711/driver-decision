package com.driverfinance.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.driverfinance.ui.navigation.AppNavigation
import com.driverfinance.ui.navigation.Screen

private data class TabItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val tabs = listOf(
    TabItem("Home", Icons.Default.Home, Screen.Dashboard.route),
    TabItem("Order", Icons.Outlined.Receipt, Screen.Order.route),
    TabItem("AI", Icons.Outlined.ChatBubbleOutline, Screen.Chat.route),
    TabItem("Input", Icons.Default.Add, Screen.QuickEntry.route),
    TabItem("Lain", Icons.Default.MoreVert, Screen.More.route)
)

private val detailRoutes = setOf(
    Screen.CaptureManager.route,
    Screen.TripDetail.route,
    Screen.HistoryTripDetail.route,
    Screen.DataReview.route
)

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val showBottomBar = currentRoute !in detailRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(modifier = Modifier.height(80.dp)) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == tab.route
                        } == true

                        if (index == 3) {
                            // Elevated Input button
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Box(contentAlignment = Alignment.Center) {
                                        SmallFloatingActionButton(
                                            onClick = {
                                                navController.navigate(tab.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            shape = CircleShape,
                                            containerColor = Color(0xFF2E7D32),
                                            contentColor = Color.White,
                                            elevation = FloatingActionButtonDefaults.elevation(
                                                defaultElevation = 4.dp
                                            ),
                                            modifier = Modifier
                                                .size(48.dp)
                                                .offset(y = (-8).dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = tab.label,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                )
                            )
                        } else {
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) }
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
