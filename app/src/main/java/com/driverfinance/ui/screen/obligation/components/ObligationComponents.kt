package com.driverfinance.ui.screen.obligation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.driverfinance.domain.model.AmbitiousModeScreenState
import com.driverfinance.domain.model.FixedExpense
import com.driverfinance.domain.model.WeekSchedule
import com.driverfinance.domain.model.WorkScheduleDay
import com.driverfinance.ui.theme.Spacing
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

private val rupiahFmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

// ==================== FIXED EXPENSE COMPONENTS ====================

/**
 * Total amount header for fixed expense list.
 * F009 spec mockup A: "Total: RpXXX/bulan".
 */
@Composable
fun FixedExpenseTotalHeader(totalAmount: Long) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            Text(
                text = "Total:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Rp${rupiahFmt.format(totalAmount)}/bulan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Card for a single fixed expense.
 * F009 spec mockup A: emoji + name + amount, edit/delete.
 */
@Composable
fun FixedExpenseCard(
    expense: FixedExpense,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditClick() }
                .padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = expense.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Rp${rupiahFmt.format(expense.amount)}/bulan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!expense.note.isNullOrBlank()) {
                    Text(
                        text = expense.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Text("\uD83D\uDDD1\uFE0F")
            }
        }
    }
}

/**
 * Empty state for fixed expenses.
 */
@Composable
fun FixedExpenseEmptyState(
    onAddClick: () -> Unit,
    onTemplateClick: (() -> Unit)?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\uD83D\uDCCB",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = "Belum ada biaya tetap",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Tambahkan biaya tetap untuk target lebih akurat",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.lg))

        if (onTemplateClick != null) {
            Button(onClick = onTemplateClick) {
                Text("\uD83D\uDCC4 Pilih dari Template")
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        Button(onClick = onAddClick) {
            Text("\u2795 Tambah Manual")
        }
    }
}

// ==================== WORK SCHEDULE COMPONENTS ====================

/**
 * A week section with 7-day toggle row.
 * F009 spec mockup D: label + date range + day toggles + summary.
 */
@Composable
fun WeekScheduleSection(
    week: WeekSchedule,
    onDayToggle: (LocalDate) -> Unit
) {
    Column {
        // Section header
        Text(
            text = "\u2500\u2500\u2500\u2500 ${week.label} \u2500\u2500\u2500\u2500",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = week.dateRange,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.md))

        // 7-day toggle row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            week.days.forEach { day ->
                DayToggle(
                    day = day,
                    onToggle = { onDayToggle(day.date) }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Summary
        Text(
            text = "${week.workingDays} hari narik \u00B7 ${week.offDays} hari libur",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Single day toggle button.
 * F009 spec: ✅ = narik, ❌ = libur.
 */
@Composable
private fun DayToggle(
    day: WorkScheduleDay,
    onToggle: () -> Unit
) {
    val isToday = day.date == LocalDate.now()

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = when {
            day.isWorking -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.errorContainer
        },
        border = if (isToday) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        modifier = Modifier
            .size(44.dp)
            .clickable { onToggle() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.dayLabel,
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    day.isWorking -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onErrorContainer
                }
            )
            Text(
                text = if (day.isWorking) "\u2705" else "\u274C",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ==================== AMBITIOUS MODE COMPONENTS ====================

/**
 * Ambitious mode section.
 * F009 spec mockup E: toggle + month picker + calculation summary.
 * This is embedded in F007 Detail Target screen.
 */
@Composable
fun AmbitiousModeSection(
    state: AmbitiousModeScreenState,
    onToggle: () -> Unit,
    onSelectPresetMonths: (Int) -> Unit,
    onCustomMonthsChange: (String) -> Unit
) {
    val fmt = rupiahFmt

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg)
        ) {
            // Header + Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\uD83D\uDE80 Mode Ambisius",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = state.isActive,
                    onCheckedChange = { onToggle() },
                    enabled = state.canActivate || state.isActive
                )
            }

            if (!state.canActivate && !state.isActive) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "Tidak ada hutang aktif untuk dipercepat",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            if (!state.isActive) return@Column

            Spacer(modifier = Modifier.height(Spacing.md))

            // Total debt
            Text(
                text = "Total sisa hutang: Rp${fmt.format(state.totalDebtRemaining)}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Month selector
            Text(
                text = "Lunas dalam:",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                state.presetMonths.forEach { months ->
                    FilterChip(
                        selected = state.targetMonths == months && state.customMonths.isEmpty(),
                        onClick = { onSelectPresetMonths(months) },
                        label = { Text("$months") }
                    )
                }
                Text(
                    text = "bulan",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            OutlinedTextField(
                value = state.customMonths,
                onValueChange = onCustomMonthsChange,
                label = { Text("atau ketik:") },
                suffix = { Text("bulan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Spacing.md))

            // Calculation summary
            SummaryRow("Cicilan normal:", "Rp${fmt.format(state.normalMonthlyInstallment)}/bln")
            SummaryRow(
                "Mode ambisius:",
                "Rp${fmt.format(state.ambitiousMonthlyInstallment)}/bln",
                isBold = true
            )
            SummaryRow(
                "Tambahan/bulan:",
                "+Rp${fmt.format(state.additionalPerMonth)}",
                isHighlight = true
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            SummaryRow("Target harian (normal):", "Rp${fmt.format(state.normalDailyTarget)}")
            SummaryRow(
                "Target harian (ambisius):",
                "Rp${fmt.format(state.ambitiousDailyTarget)}",
                isBold = true
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Info
            Text(
                text = "\u2139\uFE0F Mode ini menambah target harian untuk percepat pelunasan hutang. Biaya tetap tidak berubah.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isHighlight -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
