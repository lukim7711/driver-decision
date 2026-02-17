package com.driverfinance.ui.screen.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.driverfinance.ui.theme.Spacing

/**
 * Offline state view.
 * F008 spec mockup D:
 *   \uD83D\uDCE1
 *   Butuh koneksi internet untuk chat dengan AI
 *   Fitur lain tetap bisa dipakai secara offline
 */
@Composable
fun OfflineState() {
    Column(
        modifier = Modifier.padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\uD83D\uDCE1",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        Text(
            text = "Butuh koneksi internet\nuntuk chat dengan AI",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = "Fitur lain tetap bisa dipakai secara offline",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
