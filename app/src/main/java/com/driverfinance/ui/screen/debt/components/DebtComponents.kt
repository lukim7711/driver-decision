package com.driverfinance.ui.screen.debt.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.driverfinance.domain.model.Debt
import com.driverfinance.domain.model.DebtPayment
import com.driverfinance.domain.model.DebtStatus
import com.driverfinance.domain.model.DebtType
import com.driverfinance.domain.model.DebtVisualStatus
import com.driverfinance.domain.model.PaymentType
import com.driverfinance.domain.model.PenaltyType
import com.driverfinance.ui.theme.Spacing
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

private val rupiahFmt = NumberFormat.getNumberInstance(Locale("id", "ID"))
private val dateFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))

/**
 * Summary header showing total remaining and monthly installment.
 * F006 spec mockup A: top of debt list.
 */
@Composable
fun DebtSummaryHeader(
    totalRemaining: Long,
    totalMonthlyInstallment: Long
) {
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
                text = "Total Sisa Hutang:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Rp${rupiahFmt.format(totalRemaining)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Cicilan Per Bulan: Rp${rupiahFmt.format(totalMonthlyInstallment)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Debt card for list view.
 * F006 spec mockup A: shows name, remaining, installment, due date, status.
 */
@Composable
fun DebtCard(
    debt: Debt,
    onClick: () -> Unit
) {
    val emoji = when (debt.debtType) {
        DebtType.FIXED_INSTALLMENT -> "\uD83C\uDFCD\uFE0F"
        DebtType.PINJOL_PAYLATER -> "\uD83D\uDCF1"
        DebtType.PERSONAL -> "\uD83D\uDC64"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md)
        ) {
            Text(
                text = "$emoji ${debt.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (debt.status == DebtStatus.PAID_OFF) {
                Text(
                    text = "\u2705 Lunas \u00B7 ${debt.paidOffAt?.format(dateFmt) ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = "Sisa: Rp${rupiahFmt.format(debt.remainingAmount)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (debt.monthlyInstallment != null) {
                    Text(
                        text = "Cicilan: Rp${rupiahFmt.format(debt.monthlyInstallment)}/bln",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (debt.dueDateDay != null) {
                    Text(
                        text = "Jatuh tempo: tgl ${debt.dueDateDay}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                // Visual status
                DebtCardStatus(debt = debt)
            }
        }
    }
}

/**
 * Inline status for debt card.
 * F006 spec: \uD83D\uDFE2 On-track, \uD83D\uDFE1 approaching, \uD83D\uDD34 overdue.
 */
@Composable
private fun DebtCardStatus(debt: Debt) {
    val today = java.time.LocalDate.now()

    if (debt.dueDateDay == null) {
        Text(
            text = "\uD83D\uDFE2 Tidak ada deadline",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val dueDate = today.withDayOfMonth(debt.dueDateDay.coerceAtMost(today.lengthOfMonth()))
    val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate).toInt()

    when {
        daysUntil < 0 -> {
            val daysOverdue = -daysUntil
            val text = if (debt.penaltyType != PenaltyType.NONE && debt.penaltyAmount > 0) {
                val penalty = debt.penaltyAmount * daysOverdue
                "\uD83D\uDD34 Telat $daysOverdue hari \u00B7 Denda Rp${rupiahFmt.format(penalty)}"
            } else {
                "\uD83D\uDD34 Telat $daysOverdue hari"
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        daysUntil <= 7 -> {
            Text(
                text = "\uD83D\uDFE1 $daysUntil hari lagi",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        else -> {
            Text(
                text = "\uD83D\uDFE2 On-track",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Status badge for detail screen.
 * F006 spec Section 6.3 #2.
 */
@Composable
fun StatusBadge(status: DebtVisualStatus) {
    val (text, color) = when (status) {
        is DebtVisualStatus.OnTrack -> "\uD83D\uDFE2 On-track" to MaterialTheme.colorScheme.onSurfaceVariant
        is DebtVisualStatus.Approaching -> "\uD83D\uDFE1 ${status.daysLeft} hari lagi" to MaterialTheme.colorScheme.tertiary
        is DebtVisualStatus.Overdue -> "\uD83D\uDD34 Telat ${status.daysOverdue} hari" to MaterialTheme.colorScheme.error
        is DebtVisualStatus.OverdueMonths -> "\uD83D\uDD34 Telat ${status.monthsOverdue} bulan" to MaterialTheme.colorScheme.error
        is DebtVisualStatus.NoDeadline -> "\uD83D\uDFE2 Tidak ada deadline" to MaterialTheme.colorScheme.onSurfaceVariant
        is DebtVisualStatus.PaidOff -> "\u2705 Lunas \u00B7 ${status.paidOffAt.format(dateFmt)}" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = "Status: $text",
        style = MaterialTheme.typography.bodyMedium,
        color = color
    )
}

/**
 * Payment history row.
 * F006 spec mockup B/C/D: date + amount + \u2705.
 */
@Composable
fun PaymentHistoryRow(payment: DebtPayment) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = payment.paymentDate.format(dateFmt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Rp${rupiahFmt.format(payment.amount)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = when (payment.paymentType) {
                    PaymentType.PENALTY -> "\u26A0\uFE0F"
                    else -> "\u2705"
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Empty state when no debts exist.
 * F006 spec Section 4: "Belum ada hutang \uD83C\uDF89"
 */
@Composable
fun DebtEmptyState(onAddClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\uD83C\uDF89",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        Text(
            text = "Belum ada hutang",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Tambah hutang untuk mulai tracking",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.lg))
        androidx.compose.material3.Button(onClick = onAddClick) {
            Text("\u2795 Tambah Hutang")
        }
    }
}
