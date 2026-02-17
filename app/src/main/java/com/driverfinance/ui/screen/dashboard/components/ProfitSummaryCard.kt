package com.driverfinance.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.driverfinance.domain.model.DashboardData
import com.driverfinance.ui.theme.LossRed
import com.driverfinance.ui.theme.Spacing
import com.driverfinance.util.toRupiah

/**
 * Profit summary section.
 * F005 spec:
 *   Profit Bersih       Rp 106.000
 *   Pendapatan Rp156.000 - Keluar Rp50.000
 *
 * Color: black if >= 0, red if negative (Section 6.3 #3).
 */
@Composable
fun ProfitSummaryCard(data: DashboardData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Profit Bersih",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = data.profitBersih.toRupiah(),
                style = MaterialTheme.typography.titleMedium,
                color = if (data.profitBersih >= 0) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    LossRed
                }
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xxs))
        Text(
            text = "Pendapatan ${data.shopeeEarnings.toRupiah()} \u2212 Keluar ${data.totalExpenses.toRupiah()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
