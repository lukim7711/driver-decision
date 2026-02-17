package com.driverfinance.data.repository

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Collects driver data from all feature tables to build LLM context.
 * Per F008 §6.3.2: context injection with data from F001-F007, F009.
 */
@Singleton
class ChatContextBuilder @Inject constructor(
    private val debtRepository: DebtRepository,
    private val fixedExpenseRepository: FixedExpenseRepository,
    private val workScheduleRepository: WorkScheduleRepository,
    private val ambitiousModeRepository: AmbitiousModeRepository,
    private val dailyTargetRepository: DailyTargetRepository
) {

    suspend fun buildContext(): String {
        val today = LocalDate.now()
        val now = OffsetDateTime.now()
        val sb = StringBuilder()

        // === DATA HARI INI ===
        sb.appendLine("DATA HARI INI:")
        sb.appendLine("- Tanggal: ${today}")
        sb.appendLine("- Jam sekarang: ${now.format(DateTimeFormatter.ofPattern("HH:mm"))}")

        // Profit data from daily_target_cache
        val targetCache = dailyTargetRepository.getTodayCacheSync()
        if (targetCache != null) {
            sb.appendLine("- Target harian: Rp${formatRp(targetCache.targetAmount)}")
            sb.appendLine("- Status target: ${targetCache.status}")
            if (targetCache.urgentDeadlineName != null) {
                sb.appendLine("- Deadline mendesak: ${targetCache.urgentDeadlineName} (tgl ${targetCache.urgentDeadlineDate})")
            }
            sb.appendLine()
            sb.appendLine("DATA BULAN INI:")
            sb.appendLine("- Profit terkumpul: Rp${formatRp(targetCache.profitAccumulated)}")
            sb.appendLine("- Profit tersedia (setelah bayar cicilan): Rp${formatRp(targetCache.profitAvailable)}")
            sb.appendLine("- Kewajiban bulan ini: Rp${formatRp(targetCache.totalObligation)}")
            sb.appendLine("- Sudah dibayar: Rp${formatRp(targetCache.obligationPaid)}")
            sb.appendLine("- Sisa kewajiban: Rp${formatRp(targetCache.remainingObligation)}")
            sb.appendLine("- Sisa hari kerja: ${targetCache.remainingWorkDays} hari")
        } else {
            sb.appendLine("- Belum ada data target hari ini")
        }

        // === HUTANG AKTIF ===
        sb.appendLine()
        sb.appendLine("HUTANG AKTIF:")
        val activeDebts = debtRepository.getActiveDebtsSync()
        if (activeDebts.isEmpty()) {
            sb.appendLine("- Tidak ada hutang aktif")
        } else {
            activeDebts.forEach { debt ->
                sb.append("- ${debt.name}: sisa Rp${formatRp(debt.remainingAmount)}")
                if (debt.monthlyInstallment != null) {
                    sb.append(", cicilan Rp${formatRp(debt.monthlyInstallment)}/bln")
                }
                if (debt.dueDateDay != null) {
                    sb.append(", jatuh tempo tgl ${debt.dueDateDay}")
                }
                sb.append(", tipe ${debt.type}")
                if (debt.hasPenalty == 1) {
                    sb.append(", ADA DENDA")
                }
                sb.appendLine()
            }
        }

        // === BIAYA TETAP ===
        sb.appendLine()
        sb.appendLine("BIAYA TETAP BULANAN:")
        val totalFixed = fixedExpenseRepository.getTotalActive()
        if (totalFixed == 0) {
            sb.appendLine("- Belum ada biaya tetap")
        } else {
            sb.appendLine("- Total: Rp${formatRp(totalFixed)}/bulan")
        }

        // === JADWAL KERJA ===
        sb.appendLine()
        sb.appendLine("JADWAL KERJA:")
        val remainingWorkDays = workScheduleRepository.getRemainingWorkDays()
        sb.appendLine("- Sisa hari kerja bulan ini: $remainingWorkDays hari")

        // === MODE AMBISIUS ===
        val ambitiousMode = ambitiousModeRepository.getAmbitiousModeSync()
        if (ambitiousMode != null && ambitiousMode.isActive == 1) {
            sb.appendLine()
            sb.appendLine("MODE AMBISIUS: AKTIF")
            sb.appendLine("- Target lunas semua hutang dalam ${ambitiousMode.targetMonths} bulan")
        }

        return sb.toString()
    }

    private fun formatRp(amount: Int): String {
        return String.format("%,d", amount).replace(',', '.')
    }
}
