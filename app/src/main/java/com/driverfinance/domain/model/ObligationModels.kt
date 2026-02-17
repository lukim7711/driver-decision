package com.driverfinance.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain models for F009 Kewajiban & Jadwal.
 * Covers: Fixed Expenses, Work Schedule, Ambitious Mode.
 */

// ==================== FIXED EXPENSES ====================

/**
 * Domain model for a fixed monthly expense.
 * F009 spec 6.2 table: fixed_expenses.
 */
data class FixedExpense(
    val id: String,
    val emoji: String,
    val name: String,
    val amount: Long,
    val note: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * Default template item for first-time setup.
 * F009 spec mockup C: 7 default templates.
 */
data class FixedExpenseTemplate(
    val emoji: String,
    val name: String,
    val isSelected: Boolean = false
)

/** Predefined templates per F009 spec. */
val DEFAULT_FIXED_EXPENSE_TEMPLATES = listOf(
    FixedExpenseTemplate("\uD83D\uDCF1", "Pulsa / Paket Data"),
    FixedExpenseTemplate("\u26A1", "Listrik"),
    FixedExpenseTemplate("\uD83D\uDCA7", "Air (PDAM)"),
    FixedExpenseTemplate("\uD83C\uDFE0", "Kontrakan / Kos"),
    FixedExpenseTemplate("\uD83D\uDCFA", "Internet / WiFi"),
    FixedExpenseTemplate("\uD83C\uDF93", "Uang Sekolah Anak"),
    FixedExpenseTemplate("\uD83D\uDD27", "Servis Kendaraan Rutin")
)

/**
 * Screen state for fixed expense list.
 */
data class FixedExpenseListScreenState(
    val expenses: List<FixedExpense> = emptyList(),
    val totalAmount: Long = 0,
    val isLoading: Boolean = true,
    val showTemplateSetup: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = expenses.isEmpty() && !isLoading && !showTemplateSetup
}

/**
 * Screen state for add/edit fixed expense form.
 */
data class FixedExpenseFormScreenState(
    val isEditMode: Boolean = false,
    val expenseId: String? = null,
    val emoji: String = "\uD83D\uDCB0",
    val name: String = "",
    val amount: String = "",
    val note: String = "",
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val errorMessage: String? = null
) {
    val canSave: Boolean
        get() = name.isNotBlank() && amount.isNotBlank() && !isSaving
}

/**
 * Template setup screen state.
 */
data class TemplateSetupScreenState(
    val templates: List<FixedExpenseTemplate> = DEFAULT_FIXED_EXPENSE_TEMPLATES,
    val isSaving: Boolean = false
) {
    val selectedCount: Int
        get() = templates.count { it.isSelected }

    val hasSelection: Boolean
        get() = selectedCount > 0
}

// ==================== WORK SCHEDULE ====================

/**
 * Domain model for a single day's work schedule.
 * F009 spec 6.2 table: work_schedules.
 */
data class WorkScheduleDay(
    val date: LocalDate,
    val isWorking: Boolean,
    val dayOfWeek: DayOfWeek = date.dayOfWeek
) {
    val dayLabel: String
        get() = when (dayOfWeek) {
            DayOfWeek.SUNDAY -> "Min"
            DayOfWeek.MONDAY -> "Sen"
            DayOfWeek.TUESDAY -> "Sel"
            DayOfWeek.WEDNESDAY -> "Rab"
            DayOfWeek.THURSDAY -> "Kam"
            DayOfWeek.FRIDAY -> "Jum"
            DayOfWeek.SATURDAY -> "Sab"
        }
}

/**
 * A week of schedule data.
 */
data class WeekSchedule(
    val label: String,
    val dateRange: String,
    val days: List<WorkScheduleDay>,
    val workingDays: Int,
    val offDays: Int
)

/**
 * Screen state for work schedule.
 * F009 spec mockup D: 2 weeks (this + next).
 */
data class WorkScheduleScreenState(
    val thisWeek: WeekSchedule? = null,
    val nextWeek: WeekSchedule? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

// ==================== AMBITIOUS MODE ====================

/** Reason for deactivation. F009 spec 6.2 ambitious_mode. */
enum class DeactivatedReason {
    MANUAL,
    AUTO_ALL_PAID_OFF
}

/**
 * Domain model for ambitious mode.
 * F009 spec 6.2 table: ambitious_mode (singleton).
 */
data class AmbitiousMode(
    val id: String,
    val isActive: Boolean,
    val targetMonths: Int,
    val activatedAt: LocalDateTime?,
    val deactivatedReason: DeactivatedReason?,
    val updatedAt: LocalDateTime
)

/**
 * Screen state for ambitious mode section.
 * F009 spec mockup E: toggle + month picker + calculation summary.
 */
data class AmbitiousModeScreenState(
    val isActive: Boolean = false,
    val targetMonths: Int = 6,
    val customMonths: String = "",
    val totalDebtRemaining: Long = 0,
    val normalMonthlyInstallment: Long = 0,
    val ambitiousMonthlyInstallment: Long = 0,
    val additionalPerMonth: Long = 0,
    val normalDailyTarget: Long = 0,
    val ambitiousDailyTarget: Long = 0,
    val hasActiveDebts: Boolean = false,
    val isLoading: Boolean = true,
    val showDeactivatedMessage: Boolean = false,
    val errorMessage: String? = null
) {
    val canActivate: Boolean
        get() = hasActiveDebts

    val presetMonths: List<Int>
        get() = listOf(3, 6, 9, 12)
}
