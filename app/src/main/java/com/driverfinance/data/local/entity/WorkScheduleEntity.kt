package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_schedules",
    indices = [Index(value = ["date"], unique = true)]
)
data class WorkScheduleEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "is_working") val isWorking: Int = 1,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)
