package com.driverfinance.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.driverfinance.ui.navigation.Screen
import com.driverfinance.ui.theme.Gray500
import com.driverfinance.ui.theme.Green600
import com.driverfinance.ui.theme.Green700
import com.driverfinance.ui.theme.Green800
import com.driverfinance.ui.theme.Spacing

/**
 * Bottom tab bar with 5 tabs.
 * Ref: ARCHITECTURE.md Section 6.4
 *
 * Order: Home | Order | AI | Input (elevated) | Lain
 * - Tab aktif = warna primer
 * - Tab Input lebih besar 8dp, shadow, hijau
 * - Minimum touch target 48dp
 */

private data class TabItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val isElevated: Boolean = false
)

private val tabs = listOf(
    TabItem(Screen.Dashboard, "Home", Icons.Default.Home),
    TabItem(Screen.Order, "Order", Icons.Default.LocalShipping),
    TabItem(Screen.Chat, "AI", Icons.Default.AutoAwesome),
    TabItem(Screen.QuickEntry, "Input", Icons.Default.Add, isElevated = true),
    TabItem(Screen.More, "Lain", Icons.Default.MoreHoriz)
)

@Composable
fun DriverFinanceTabBar(
    currentRoute: String?,
    onTabSelected: (Screen) -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Spacing.tabBarHeight)
                .padding(horizontal = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = currentRoute == tab.screen.route

                if (tab.isElevated) {
                    ElevatedTabItem(
                        tab = tab,
                        isSelected = isSelected,
                        onClick = { onTabSelected(tab.screen) }
                    )
                } else {
                    RegularTabItem(
                        tab = tab,
                        isSelected = isSelected,
                        onClick = { onTabSelected(tab.screen) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RegularTabItem(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Gray500,
        label = "tabIconColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Gray500,
        label = "tabTextColor"
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
            .widthIn(min = Spacing.minTouchTarget),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(Spacing.xxs))
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
private fun ElevatedTabItem(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Green800 else Green600,
        label = "elevatedBg"
    )

    Column(
        modifier = Modifier
            .offset(y = -Spacing.tabBarElevatedOffset)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(Spacing.minTouchTarget + Spacing.tabBarElevatedOffset)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xxs))
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Green700 else Gray500
        )
    }
}
