package com.driverfinance.util

import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

/**
 * Currency formatter.
 * ARCHITECTURE.md Section 6.6: semua nominal Rupiah format "Rp50.000".
 */
object CurrencyFormatter {
    private val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

    fun format(amount: Int): String = "Rp${numberFormat.format(amount)}"
    fun format(amount: Long): String = "Rp${numberFormat.format(amount)}"
}

fun Int.toRupiah(): String = CurrencyFormatter.format(this)
fun Long.toRupiah(): String = CurrencyFormatter.format(this)

/**
 * Indonesian date formatter for dashboard header.
 * Output: "Selasa, 17 Feb 2026"
 */
object DateFormatter {
    private val dayNames = mapOf(
        1 to "Senin", 2 to "Selasa", 3 to "Rabu",
        4 to "Kamis", 5 to "Jumat", 6 to "Sabtu", 7 to "Minggu"
    )
    private val monthNames = mapOf(
        1 to "Jan", 2 to "Feb", 3 to "Mar", 4 to "Apr",
        5 to "Mei", 6 to "Jun", 7 to "Jul", 8 to "Agu",
        9 to "Sep", 10 to "Okt", 11 to "Nov", 12 to "Des"
    )

    fun formatDashboardDate(date: LocalDate = LocalDate.now()): String {
        val day = dayNames[date.dayOfWeek.value].orEmpty()
        val month = monthNames[date.monthValue].orEmpty()
        return "$day, ${date.dayOfMonth} $month ${date.year}"
    }
}
