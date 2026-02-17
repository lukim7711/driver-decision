package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quick_entries",
    indices = [Index("entry_date"), Index("type"), Index("category_id")]
)
data class QuickEntryEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "note") val note: String? = null,
    @ColumnInfo(name = "entry_date") val entryDate: String,
    @ColumnInfo(name = "entry_time") val entryTime: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
