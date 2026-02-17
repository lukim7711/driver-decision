package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parsing_patterns",
    indices = [Index("screen_type"), Index("is_active")]
)
data class ParsingPatternEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "screen_type") val screenType: String,
    @ColumnInfo(name = "field_name") val fieldName: String,
    @ColumnInfo(name = "pattern_type") val patternType: String,
    @ColumnInfo(name = "pattern_value") val patternValue: String,
    @ColumnInfo(name = "accuracy") val accuracy: Double = 0.0,
    @ColumnInfo(name = "is_active") val isActive: Int = 1,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)
