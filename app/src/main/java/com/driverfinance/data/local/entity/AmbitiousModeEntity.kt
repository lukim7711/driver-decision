package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ambitious_mode")
data class AmbitiousModeEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "is_active") val isActive: Int = 0,
    @ColumnInfo(name = "target_months") val targetMonths: Int = 0,
    @ColumnInfo(name = "activated_at") val activatedAt: String? = null,
    @ColumnInfo(name = "deactivated_reason") val deactivatedReason: String? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)
