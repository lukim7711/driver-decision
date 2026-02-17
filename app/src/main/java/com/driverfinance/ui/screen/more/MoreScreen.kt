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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.driverfinance.ui.theme.Spacing

/**
 * Tab "Lain" — More Hub screen.
 *
 * Central navigation hub for all non-tab features:
 * - F006: Hutang (Debt Management)
 * - F009: Biaya Tetap Bulanan (Fixed Expenses)
 * - F009: Jadwal Kerja (Work Schedule)
 * - Capture Manager (F001/F002 support)
 * - Data Review (F001/F002 support)
 * - Settings
 *
 * Layout: Section headers + menu items with emoji + arrow.
 */
@Composable
fun MoreScreen(
    onDebtClick: () -> Unit,
    onFixedExpenseClick: () -> Unit,
    onWorkScheduleClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCaptureManagerClick: () -> Unit,
    onDataReviewClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──
        Text(
            text = "Lainnya",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                horizontal = Spacing.screenPadding,
                vertical = Spacing.md
            )
        )

        // ══════════════════════════════════════════
        // Section: Keuangan
        // ══════════════════════════════════════════
        SectionHeader("Keuangan")

        MenuItem(
            emoji = "\uD83D\uDCB0",
            title = "Hutang",
            subtitle = "Kelola cicilan motor, pinjol, hutang personal",
            onClick = onDebtClick
        )

        MenuItem(
            emoji = "\uD83D\uDCCB",
            title = "Biaya Tetap Bulanan",
            subtitle = "Pulsa, listrik, kontrakan, dll",
            onClick = onFixedExpenseClick
        )

        // ══════════════════════════════════════════
        // Section: Jadwal & Target
        // ══════════════════════════════════════════
        Spacer(modifier = Modifier.height(Spacing.md))
        SectionHeader("Jadwal & Target")

        MenuItem(
            emoji = "\uD83D\uDCC5",
            title = "Jadwal Kerja",
            subtitle = "Atur hari narik dan libur minggu ini",
            onClick = onWorkScheduleClick
        )

        // ══════════════════════════════════════════
        // Section: Data & Capture
        // ══════════════════════════════════════════
        Spacer(modifier = Modifier.height(Spacing.md))
        SectionHeader("Data & Capture")

        MenuItem(
            emoji = "\uD83D\uDCF7",
            title = "Capture Manager",
            subtitle = "Kelola screenshot order yang di-capture",
            onClick = onCaptureManagerClick
        )

        MenuItem(
            emoji = "\uD83D\uDD0D",
            title = "Data Perlu Dicek",
            subtitle = "Review data dari OCR yang belum diverifikasi",
            onClick = onDataReviewClick
        )

        // ══════════════════════════════════════════
        // Section: App
        // ══════════════════════════════════════════
        Spacer(modifier = Modifier.height(Spacing.md))
        SectionHeader("App")

        MenuItem(
            emoji = "\u2699\uFE0F",
            title = "Pengaturan",
            subtitle = "Tema, bahasa, backup data",
            onClick = onSettingsClick
        )

        // ── App Version ──
        Spacer(modifier = Modifier.height(Spacing.xl))
        Text(
            text = "Driver Decision v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = Spacing.screenPadding)
                .padding(bottom = Spacing.xxl)
        )
    }
}

/**
 * Section header label.
 */
@Composable
private fun SectionHeader(text: String) {
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

/**
 * Menu item row: emoji + title/subtitle + arrow chevron.
 */
@Composable
private fun MenuItem(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Spacing.screenPadding,
                    vertical = Spacing.md
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji icon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(48.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            // Title + Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 76.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}
