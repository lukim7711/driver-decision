package com.driverfinance.ui.screen.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.driverfinance.domain.model.DashboardData
import com.driverfinance.ui.theme.Spacing

/**
 * Trip, order, and bonus points summary.
 * F005 spec:
 *   8 trip \u00B7 15 pesanan
 *     5 SPX Instant \u00B7 2 ShopeeFood \u00B7 1 Sameday
 *   450 poin hari ini
 *
 * Tap → navigate to tab Order.
 */
@Composable
fun TripSummaryCard(
    data: DashboardData,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = "${data.tripCount} trip \u00B7 ${data.orderCount} pesanan",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (data.hasTrips) {
            val breakdown = data.serviceBreakdownText()
            if (breakdown.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = breakdown,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xxs))
        Text(
            text = "${data.bonusPoints} poin hari ini",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
