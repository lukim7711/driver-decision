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
import com.driverfinance.ui.theme.Spacing
import com.driverfinance.util.toRupiah

/**
 * Pemasukan Lain section.
 * F005 spec Section 6.3 #6:
 * - Only show if > 0
 * - Breakdown if <= 3 items, else "X pemasukan"
 */
@Composable
fun OtherIncomeCard(data: DashboardData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Pemasukan Lain",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = data.otherIncome.toRupiah(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (data.otherIncomeItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.xxs))
            if (data.otherIncomeItems.size <= 3) {
                val detail = data.otherIncomeItems.joinToString(" + ") {
                    "${it.name} ${it.amount.toRupiah()}"
                }
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "${data.otherIncomeItems.size} pemasukan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
