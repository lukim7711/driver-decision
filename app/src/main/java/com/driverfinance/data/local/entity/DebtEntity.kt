package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debts",
    indices = [Index("status"), Index("debt_type")]
)
data class DebtEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "debt_type") val debtType: String,
    @ColumnInfo(name = "original_amount") val originalAmount: Int,
    @ColumnInfo(name = "remaining_amount") val remainingAmount: Int,
    @ColumnInfo(name = "monthly_installment") val monthlyInstallment: Int? = null,
    @ColumnInfo(name = "due_date_day") val dueDateDay: Int? = null,
    @ColumnInfo(name = "penalty_type") val penaltyType: String = "NONE",
    @ColumnInfo(name = "penalty_amount") val penaltyAmount: Int = 0,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "status") val status: String = "ACTIVE",
    @ColumnInfo(name = "paid_off_at") val paidOffAt: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)
