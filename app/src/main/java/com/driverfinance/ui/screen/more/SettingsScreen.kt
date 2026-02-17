package com.driverfinance.ui.screen.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.driverfinance.ui.theme.Spacing

/**
 * Settings screen.
 *
 * Placeholder settings menu with common options:
 * - Dark Mode toggle
 * - Data Backup
 * - About / Version info
 *
 * TODO: Implement actual settings logic with DataStore.
 */
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    var darkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Pengaturan",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                horizontal = Spacing.screenPadding,
                vertical = Spacing.md
            )
        )

        // ── Tampilan ──
        SettingSectionHeader("Tampilan")

        SettingToggle(
            emoji = "\uD83C\uDF19",
            title = "Mode Gelap",
            subtitle = "Ganti tema ke dark mode",
            isChecked = darkMode,
            onCheckedChange = { darkMode = it }
        )

        // ── Data ──
        Spacer(modifier = Modifier.height(Spacing.md))
        SettingSectionHeader("Data")

        SettingItem(
            emoji = "\uD83D\uDCBE",
            title = "Backup Data",
            subtitle = "Export semua data ke file",
            onClick = { /* TODO */ }
        )

        SettingItem(
            emoji = "\uD83D\uDCC2",
            title = "Restore Data",
            subtitle = "Import data dari file backup",
            onClick = { /* TODO */ }
        )

        SettingItem(
            emoji = "\uD83D\uDDD1\uFE0F",
            title = "Hapus Semua Data",
            subtitle = "Reset app ke kondisi awal",
            onClick = { /* TODO: Show confirmation dialog */ }
        )

        // ── Tentang ──
        Spacer(modifier = Modifier.height(Spacing.md))
        SettingSectionHeader("Tentang")

        SettingItem(
            emoji = "\u2139\uFE0F",
            title = "Versi Aplikasi",
            subtitle = "v1.0.0 (Build 1)",
            onClick = { }
        )

        SettingItem(
            emoji = "\uD83D\uDC68\u200D\uD83D\uDCBB",
            title = "Developer",
            subtitle = "Driver Decision Team",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}

@Composable
private fun SettingSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(
            horizontal = Spacing.screenPadding,
            vertical = Spacing.xs
        )
    )
}

@Composable
private fun SettingItem(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = Spacing.screenPadding,
                vertical = Spacing.md
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
private fun SettingToggle(
    emoji: String,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(
                horizontal = Spacing.screenPadding,
                vertical = Spacing.md
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}
