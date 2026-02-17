package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_target_cache",
    indices = [Index(value = ["target_date"], unique = true)]
)
data class DailyTargetCacheEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "target_date") val targetDate: String,
    @ColumnInfo(name = "target_amount") val targetAmount: Int,
    @ColumnInfo(name = "total_obligation") val totalObligation: Int,
    @ColumnInfo(name = "obligation_paid") val obligationPaid: Int,
    @ColumnInfo(name = "remaining_obligation") val remainingObligation: Int,
    @ColumnInfo(name = "profit_accumulated") val profitAccumulated: Int,
    @ColumnInfo(name = "profit_available") val profitAvailable: Int,
    @ColumnInfo(name = "remaining_work_days") val remainingWorkDays: Int,
    val status: String,
    @ColumnInfo(name = "urgent_deadline_name") val urgentDeadlineName: String?,
    @ColumnInfo(name = "urgent_deadline_date") val urgentDeadlineDate: Int?,
    @ColumnInfo(name = "urgent_deadline_gap") val urgentDeadlineGap: Int?,
    @ColumnInfo(name = "calculated_at") val calculatedAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
