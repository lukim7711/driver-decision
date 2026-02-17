package com.driverfinance.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Notification bell with badge count.
 * Ref: ARCHITECTURE.md Section 6.4 — NotificationBell shared component.
 *
 * Sources: F001 (capture mati), F002 (pengingat riwayat), F003 (data perlu dicek).
 * Tap → dropdown list (TODO: dropdown implementation in future PR).
 */
@Composable
fun NotificationBell(
    count: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (count > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(text = if (count > 99) "99+" else "$count")
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifikasi",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
