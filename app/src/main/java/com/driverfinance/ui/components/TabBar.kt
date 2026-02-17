package com.driverfinance.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.driverfinance.ui.navigation.Screen
import com.driverfinance.ui.theme.Spacing

data class TabItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val isElevated: Boolean = false
)

val tabItems = listOf(
    TabItem(Screen.Dashboard, "Home", Icons.Filled.Home),
    TabItem(Screen.Order, "Order", Icons.Filled.LocalShipping),
    TabItem(Screen.Chat, "AI", Icons.Filled.SmartToy),
    TabItem(Screen.QuickEntry, "Input", Icons.Filled.AddCircle, isElevated = true),
    TabItem(Screen.More, "Lain", Icons.Filled.MoreHoriz)
)

/**
 * Bottom Tab Bar with 5 tabs.
 * Tab "Input" is elevated (+8dp, shadow, primary color) per ARCHITECTURE.md Section 6.4.
 * Minimum touch target 48dp per CONSTITUTION Section 2.
 */
@Composable
fun AppTabBar(
    currentRoute: String?,
    onTabSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        tabItems.forEach { item ->
            val selected = currentRoute == item.screen.route

            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(item.screen) },
                icon = {
                    if (item.isElevated) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(Spacing.xxl + 8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    } else {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    }
                },
                label = { Text(item.label) }
            )
        }
    }
}
