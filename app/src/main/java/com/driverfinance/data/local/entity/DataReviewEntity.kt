package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "data_reviews",
    indices = [Index("review_status"), Index("source_table"), Index("source_id")]
)
data class DataReviewEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "source_table") val sourceTable: String,
    @ColumnInfo(name = "source_id") val sourceId: String,
    @ColumnInfo(name = "field_name") val fieldName: String,
    @ColumnInfo(name = "original_value") val originalValue: String,
    @ColumnInfo(name = "suggested_value") val suggestedValue: String? = null,
    @ColumnInfo(name = "corrected_value") val correctedValue: String? = null,
    @ColumnInfo(name = "confidence") val confidence: Double = 0.0,
    @ColumnInfo(name = "review_status") val reviewStatus: String = "PENDING",
    @ColumnInfo(name = "reviewed_at") val reviewedAt: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: String
)
