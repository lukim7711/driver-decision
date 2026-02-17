package com.driverfinance.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.driverfinance.ui.theme.Amber50
import com.driverfinance.ui.theme.Spacing

/**
 * Warning banner for pending data reviews.
 * F005 spec Section 6.3 #4:
 * "\u26A0\uFE0F X pesanan belum masuk hitungan"
 * Tap → navigate to Data Review (F003).
 */
@Composable
fun DataWarningBanner(
    count: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Amber50,
        shape = RoundedCornerShape(Spacing.sm)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\u26A0\uFE0F",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = "$count pesanan belum masuk hitungan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
