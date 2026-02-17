package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fixed_expenses",
    indices = [Index("is_active")]
)
data class FixedExpenseEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "emoji") val emoji: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "is_active") val isActive: Int = 1,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)
