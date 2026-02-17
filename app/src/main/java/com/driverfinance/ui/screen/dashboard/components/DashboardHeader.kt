package com.driverfinance.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.driverfinance.ui.components.NotificationBell
import com.driverfinance.util.DateFormatter

/**
 * Dashboard top row: date (left) + notification bell (right).
 * F005 spec: "Selasa, 17 Feb 2026  \uD83D\uDD14"
 */
@Composable
fun DashboardHeader(
    notificationCount: Int,
    onBellClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = DateFormatter.formatDashboardDate(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        NotificationBell(
            count = notificationCount,
            onClick = onBellClick
        )
    }
}
