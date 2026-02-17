package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "matched_orders",
    indices = [Index("captured_order_id"), Index("history_detail_id"), Index("match_status")]
)
data class MatchedOrderEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "captured_order_id") val capturedOrderId: String? = null,
    @ColumnInfo(name = "history_detail_id") val historyDetailId: String? = null,
    @ColumnInfo(name = "match_status") val matchStatus: String,
    @ColumnInfo(name = "match_confidence") val matchConfidence: Double = 0.0,
    @ColumnInfo(name = "matched_at") val matchedAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
