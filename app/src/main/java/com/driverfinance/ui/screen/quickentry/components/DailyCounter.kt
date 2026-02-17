package com.driverfinance.ui.screen.quickentry.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.driverfinance.ui.theme.Spacing
import com.driverfinance.util.toRupiah

/**
 * Daily summary counter at the bottom of Quick Entry screen.
 * F004 spec Section 6.3 #1:
 *   "Hari ini: X pengeluaran"
 *   "Rp70.000"
 * Updates real-time after each save.
 */
@Composable
fun DailyCounter(
    count: Int,
    total: Int,
    label: String
) {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = Spacing.sm),
        color = MaterialTheme.colorScheme.outlineVariant
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs)
    ) {
        Text(
            text = "Hari ini: $count $label",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (total > 0) {
            Text(
                text = total.toRupiah(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
