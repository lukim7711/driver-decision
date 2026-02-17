package com.driverfinance.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain models for F007 Target Harian Otomatis.
 * Core calculation engine + UI states for daily target.
 */

// ==================== TARGET STATUS ====================

/** Overall target status for the day. F007 spec 6.2: status field. */
enum class TargetStatus {
    /** Profit belum cukup, masih harus narik. */
    ON_TRACK,
    /** Kurang dari target & deadline mendekat. */
    BEHIND,
    /** Target hari ini tercapai. */
    ACHIEVED,
    /** Semua kewajiban bulan ini tercukupi. */
    ALL_COVERED,
    /** Belum ada kewajiban (F009 belum di-setup). */
    NO_OBLIGATION
}

// ==================== HERO NUMBER ====================

/**
 * State for the hero number displayed on Dashboard (F005).
 * F007 spec mockup A: multiple variations.
 */
data class HeroTargetState(
    val status: TargetStatus = TargetStatus.NO_OBLIGATION,
    val targetAmount: Long = 0,
    val profitToday: Long = 0,
    val remainingAmount: Long = 0,
    val surplusAmount: Long = 0,
    val progressPercent: Float = 0f
) {
    /** Primary display text for hero number. */
    val heroText: String
        get() = when (status) {
            TargetStatus.NO_OBLIGATION -> "Belum ada kewajiban bulan ini"
            TargetStatus.ALL_COVERED -> "\uD83C\uDF89 Kewajiban bulan ini tercukupi!"
            TargetStatus.ACHIEVED -> "\u2705 Target tercapai! Lebih Rp${formatRupiah(surplusAmount)}"
            TargetStatus.BEHIND -> "Kurang Rp${formatRupiah(remainingAmount)} lagi"
            TargetStatus.ON_TRACK -> {
                if (profitToday == 0L) "Target hari ini: Rp${formatRupiah(targetAmount)}"
                else "Kurang Rp${formatRupiah(remainingAmount)} lagi"
            }
        }

    /** Sub text below hero number. */
    val subText: String?
        get() = when (status) {
            TargetStatus.ALL_COVERED -> "Sisa hari ini = bonus"
            TargetStatus.ACHIEVED -> null
            TargetStatus.NO_OBLIGATION -> "Tambahkan hutang atau biaya tetap"
            else -> "Target: Rp${formatRupiah(targetAmount)}"
        }

    val showProgressBar: Boolean
        get() = status != TargetStatus.NO_OBLIGATION
}

private fun formatRupiah(amount: Long): String {
    return java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(amount)
}

// ==================== OBLIGATION BREAKDOWN ====================

/** A single obligation item (debt installment or fixed expense). */
data class ObligationItem(
    val emoji: String,
    val name: String,
    val amount: Long,
    val dueDateDay: Int?,
    val isPaid: Boolean = false,
    val source: ObligationSource
)

enum class ObligationSource {
    DEBT_INSTALLMENT,
    FIXED_EXPENSE
}

// ==================== DEADLINE WARNING ====================

/**
 * Urgent deadline warning.
 * F007 spec mockup C: peringatan deadline.
 */
data class DeadlineWarning(
    val name: String,
    val emoji: String,
    val dueDate: LocalDate,
    val daysUntilDue: Int,
    val amountNeeded: Long,
    val amountAvailable: Long,
    val amountShort: Long,
    val isOverdue: Boolean
) {
    val urgencyText: String
        get() = when {
            isOverdue -> "\uD83D\uDD34 Sudah lewat jatuh tempo!"
            daysUntilDue == 0 -> "\u26A0\uFE0F Jatuh tempo hari ini!"
            daysUntilDue == 1 -> "\u26A0\uFE0F Jatuh tempo besok!"
            else -> "\u26A0\uFE0F Jatuh tempo ${daysUntilDue} hari lagi (tgl ${dueDate.dayOfMonth})"
        }
}

// ==================== DETAIL SCREEN STATE ====================

/**
 * Full state for Target Detail screen.
 * F007 spec mockup B: breakdown + progress + schedule + ambitious.
 */
data class TargetDetailScreenState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // -- Hero section --
    val hero: HeroTargetState = HeroTargetState(),

    // -- Obligation breakdown (Kewajiban Bulan Ini) --
    val debtInstallments: List<ObligationItem> = emptyList(),
    val fixedExpenses: List<ObligationItem> = emptyList(),
    val subtotalDebt: Long = 0,
    val subtotalFixedExpense: Long = 0,
    val totalObligation: Long = 0,

    // -- Progress Bulan Ini --
    val profitAccumulated: Long = 0,
    val debtPaymentsMade: Long = 0,
    val profitAvailable: Long = 0,
    val remainingObligation: Long = 0,
    val amountToCollect: Long = 0,
    val remainingWorkDays: Int = 0,
    val targetPerDay: Long = 0,

    // -- Deadline warnings --
    val deadlineWarnings: List<DeadlineWarning> = emptyList(),
    val noWorkDaysWarning: Boolean = false,

    // -- F009 embedded sections --
    val ambitiousModeState: AmbitiousModeScreenState = AmbitiousModeScreenState(),
    val workScheduleThisWeek: WeekSchedule? = null
) {
    val hasObligations: Boolean
        get() = totalObligation > 0

    val hasDeadlineWarnings: Boolean
        get() = deadlineWarnings.isNotEmpty()
}

// ==================== DAILY TARGET CACHE ====================

/**
 * Domain model for cached daily target.
 * F007 spec 6.2: daily_target_cache table.
 */
data class DailyTargetCache(
    val id: String,
    val targetDate: LocalDate,
    val targetAmount: Long,
    val totalObligation: Long,
    val obligationPaid: Long,
    val remainingObligation: Long,
    val profitAccumulated: Long,
    val profitAvailable: Long,
    val remainingWorkDays: Int,
    val status: TargetStatus,
    val urgentDeadlineName: String?,
    val urgentDeadlineDate: Int?,
    val urgentDeadlineGap: Long?,
    val calculatedAt: LocalDateTime,
    val createdAt: LocalDateTime
)

// ==================== ALGORITHM INPUT/OUTPUT ====================

/**
 * Input for deadline-aware target algorithm.
 * F007 spec 6.3 #5.
 */
data class DeadlineObligation(
    val name: String,
    val emoji: String,
    val amount: Long,
    val dueDateDay: Int,
    val isPaid: Boolean
)

/**
 * Output from deadline-aware target algorithm per deadline.
 */
data class DeadlineTargetResult(
    val deadline: DeadlineObligation,
    val cumulativeObligation: Long,
    val workDaysUntil: Int,
    val needed: Long,
    val targetPerDay: Long,
    val isUrgent: Boolean
)
