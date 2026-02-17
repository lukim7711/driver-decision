package com.driverfinance.domain.model

/**
 * Domain model for F005 Dashboard.
 * All values computed real-time — nothing stored in DB.
 *
 * Sources:
 * - shopeeEarnings, trip/order counts, poin → history_trips / history_details (F002)
 * - otherIncome, totalExpenses → quick_entries (F004)
 * - dailyTarget → computed by F007 (reads F006 + F009)
 * - pendingReviewCount → data_reviews (F003)
 */
data class DashboardData(
    val dailyTarget: Int? = null,
    val shopeeEarnings: Int = 0,
    val otherIncome: Int = 0,
    val totalExpenses: Int = 0,
    val tripCount: Int = 0,
    val orderCount: Int = 0,
    val bonusPoints: Int = 0,
    val spxInstantCount: Int = 0,
    val shopeeFoodCount: Int = 0,
    val samedayCount: Int = 0,
    val otherIncomeItems: List<IncomeItem> = emptyList(),
    val pendingReviewCount: Int = 0,
    val notificationCount: Int = 0
) {
    val totalRevenue: Int get() = shopeeEarnings + otherIncome
    val profitBersih: Int get() = totalRevenue - totalExpenses
    val isTargetAvailable: Boolean get() = dailyTarget != null
    val isTargetMet: Boolean get() = dailyTarget?.let { profitBersih >= it } ?: false
    val sisaTarget: Int get() = dailyTarget?.let { (it - profitBersih).coerceAtLeast(0) } ?: 0
    val lebihTarget: Int get() = dailyTarget?.let { (profitBersih - it).coerceAtLeast(0) } ?: 0
    val progressPercent: Float
        get() = dailyTarget?.let { target ->
            if (target > 0) (profitBersih.toFloat() / target).coerceIn(0f, 1f) else 0f
        } ?: 0f
    val progressPercentInt: Int get() = (progressPercent * 100).toInt()
    val hasOtherIncome: Boolean get() = otherIncome > 0
    val hasPendingReviews: Boolean get() = pendingReviewCount > 0
    val hasTrips: Boolean get() = tripCount > 0

    fun serviceBreakdownText(): String {
        val parts = mutableListOf<String>()
        if (spxInstantCount > 0) parts.add("$spxInstantCount SPX Instant")
        if (shopeeFoodCount > 0) parts.add("$shopeeFoodCount ShopeeFood")
        if (samedayCount > 0) parts.add("$samedayCount Sameday")
        return parts.joinToString(" \u00B7 ")
    }
}

data class IncomeItem(
    val name: String,
    val amount: Int
)
