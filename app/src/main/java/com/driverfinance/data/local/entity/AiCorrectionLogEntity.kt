package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ai_corrections_log",
    indices = [Index("data_review_id"), Index("pattern_id")]
)
data class AiCorrectionLogEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "data_review_id") val dataReviewId: String,
    @ColumnInfo(name = "pattern_id") val patternId: String? = null,
    @ColumnInfo(name = "original_value") val originalValue: String,
    @ColumnInfo(name = "corrected_value") val correctedValue: String,
    @ColumnInfo(name = "correction_type") val correctionType: String,
    @ColumnInfo(name = "applied_to_pattern") val appliedToPattern: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: String
)
