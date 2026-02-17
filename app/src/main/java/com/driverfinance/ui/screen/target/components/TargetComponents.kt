package com.driverfinance.ui.screen.target.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.driverfinance.domain.model.DeadlineWarning
import com.driverfinance.domain.model.HeroTargetState
import com.driverfinance.domain.model.ObligationItem
import com.driverfinance.domain.model.ObligationSource
import com.driverfinance.domain.model.TargetStatus
import com.driverfinance.ui.theme.Spacing
import java.text.NumberFormat
import java.util.Locale

private val rupiahFmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

// ==================== HERO SECTION ====================

/**
 * Hero target display with progress bar.
 * F007 spec mockup A/B: target amount + progress + status text.
 */
@Composable
fun HeroTargetSection(
    hero: HeroTargetState,
    modifier: Modifier = Modifier
) {
    val statusColor = when (hero.status) {
        TargetStatus.ACHIEVED, TargetStatus.ALL_COVERED -> MaterialTheme.colorScheme.primary
        TargetStatus.BEHIND -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero text
            Text(
                text = hero.heroText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            // Progress bar
            if (hero.showProgressBar) {
                Spacer(modifier = Modifier.height(Spacing.md))
                LinearProgressIndicator(
                    progress = { (hero.progressPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = when (hero.status) {
                        TargetStatus.ACHIEVED, TargetStatus.ALL_COVERED ->
                            MaterialTheme.colorScheme.primary
                        TargetStatus.BEHIND -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "${hero.progressPercent.toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Sub text
            hero.subText?.let {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Profit today
            if (hero.profitToday > 0 && hero.status != TargetStatus.NO_OBLIGATION) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Profit hari ini: Rp${rupiahFmt.format(hero.profitToday)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// ==================== OBLIGATION BREAKDOWN ====================

/**
 * Obligation breakdown section.
 * F007 spec mockup B: "Kewajiban Bulan Ini" with debt + fixed expense subtotals.
 */
@Composable
fun ObligationBreakdownSection(
    debtInstallments: List<ObligationItem>,
    fixedExpenses: List<ObligationItem>,
    subtotalDebt: Long,
    subtotalFixedExpense: Long,
    totalObligation: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionDivider("Kewajiban Bulan Ini")
        Spacer(modifier = Modifier.height(Spacing.md))

        // Debt installments
        if (debtInstallments.isNotEmpty()) {
            Text(
                text = "Cicilan Hutang:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.xs))

            debtInstallments.forEach { item ->
                ObligationRow(
                    item = item,
                    showStrikethrough = item.isPaid
                )
            }

            SubtotalRow("Subtotal Cicilan:", subtotalDebt)
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        // Fixed expenses
        if (fixedExpenses.isNotEmpty()) {
            Text(
                text = "Biaya Tetap:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.xs))

            fixedExpenses.forEach { item ->
                ObligationRow(item = item)
            }

            SubtotalRow("Subtotal Biaya Tetap:", subtotalFixedExpense)
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        // Total
        HorizontalDivider(thickness = 2.dp)
        Spacer(modifier = Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Kewajiban:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Rp${rupiahFmt.format(totalObligation)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ObligationRow(
    item: ObligationItem,
    showStrikethrough: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(text = item.emoji, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = item.name + if (item.isPaid) " \u2705" else "",
                style = MaterialTheme.typography.bodyMedium,
                color = if (showStrikethrough) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "Rp${rupiahFmt.format(item.amount)}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (showStrikethrough) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SubtotalRow(label: String, amount: Long) {
    Spacer(modifier = Modifier.height(Spacing.xs))
    HorizontalDivider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Rp${rupiahFmt.format(amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ==================== CALCULATION BREAKDOWN ====================

/**
 * Progress & calculation breakdown.
 * F007 spec mockup B: profit → available → remaining → work days → target/day.
 */
@Composable
fun CalculationBreakdownSection(
    profitAccumulated: Long,
    debtPaymentsMade: Long,
    profitAvailable: Long,
    remainingObligation: Long,
    amountToCollect: Long,
    remainingWorkDays: Int,
    targetPerDay: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionDivider("Progress Bulan Ini")
        Spacer(modifier = Modifier.height(Spacing.md))

        CalcRow("Profit terkumpul:", "Rp${rupiahFmt.format(profitAccumulated)}")
        CalcRow("Sudah bayar cicilan:", "-Rp${rupiahFmt.format(debtPaymentsMade)}")
        CalcRow("Profit tersedia:", "Rp${rupiahFmt.format(profitAvailable)}", isBold = true)

        Spacer(modifier = Modifier.height(Spacing.md))

        CalcRow("Sisa kewajiban:", "Rp${rupiahFmt.format(remainingObligation)}")
        CalcRow("Profit tersedia:", "-Rp${rupiahFmt.format(profitAvailable)}")

        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.xs))

        CalcRow("Harus dikumpulkan:", "Rp${rupiahFmt.format(amountToCollect)}", isBold = true)
        CalcRow("Sisa hari kerja:", "\u00F7 $remainingWorkDays hari")

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Spacing.xs),
            thickness = 2.dp
        )

        CalcRow(
            "= Target per hari:",
            "Rp${rupiahFmt.format(targetPerDay)}",
            isBold = true,
            isHighlight = true
        )
    }
}

@Composable
private fun CalcRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ==================== DEADLINE WARNING ====================

/**
 * Deadline warning card.
 * F007 spec mockup C: urgent deadline with amounts.
 */
@Composable
fun DeadlineWarningCard(
    warning: DeadlineWarning,
    modifier: Modifier = Modifier
) {
    val bgColor = if (warning.isOverdue) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    val textColor = if (warning.isOverdue) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            // Title
            Text(
                text = warning.urgencyText,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(Spacing.xs))

            // Item name
            Text(
                text = "${warning.emoji} ${warning.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Spacer(modifier = Modifier.height(Spacing.sm))

            // Amounts
            CalcRowWarning("Butuh:", "Rp${rupiahFmt.format(warning.amountNeeded)}", textColor)
            CalcRowWarning("Tersedia:", "Rp${rupiahFmt.format(warning.amountAvailable)}", textColor)

            if (warning.amountShort > 0) {
                CalcRowWarning(
                    "Kurang:",
                    "Rp${rupiahFmt.format(warning.amountShort)}",
                    textColor,
                    isBold = true
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "\u2139\uFE0F Target harian sudah disesuaikan",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

@Composable
private fun CalcRowWarning(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}

// ==================== NO WORK DAYS WARNING ====================

/**
 * Warning when no work days remain but obligations exist.
 * F007 spec case: sisa hari kerja = 0.
 */
@Composable
fun NoWorkDaysWarning(
    remainingObligation: Long,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Text(
                text = "\u26A0\uFE0F Tidak ada sisa hari kerja bulan ini",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Kewajiban belum tercukupi Rp${rupiahFmt.format(remainingObligation)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

// ==================== SHARED ====================

@Composable
private fun SectionDivider(text: String) {
    Text(
        text = "\u2500\u2500\u2500\u2500 $text \u2500\u2500\u2500\u2500",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
