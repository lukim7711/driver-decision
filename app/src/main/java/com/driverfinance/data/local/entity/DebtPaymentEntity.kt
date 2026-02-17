package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debt_payments",
    indices = [Index("debt_id"), Index("payment_date")]
)
data class DebtPaymentEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "debt_id") val debtId: String,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "payment_type") val paymentType: String,
    @ColumnInfo(name = "include_as_expense") val includeAsExpense: Int = 0,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "linked_expense_id") val linkedExpenseId: String? = null,
    @ColumnInfo(name = "payment_date") val paymentDate: String,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String
)
