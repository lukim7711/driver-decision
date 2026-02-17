package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history_details",
    indices = [Index("history_trip_id"), Index("order_sn"), Index("linked_order_id")]
)
data class HistoryDetailEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "history_trip_id") val historyTripId: String,
    @ColumnInfo(name = "linked_order_id") val linkedOrderId: String? = null,
    @ColumnInfo(name = "order_sn") val orderSn: String,
    @ColumnInfo(name = "order_id_long") val orderIdLong: String? = null,
    @ColumnInfo(name = "delivery_fee") val deliveryFee: Int,
    @ColumnInfo(name = "total_earning") val totalEarning: Int,
    @ColumnInfo(name = "bonus_type") val bonusType: String? = null,
    @ColumnInfo(name = "bonus_points") val bonusPoints: Int = 0,
    @ColumnInfo(name = "cash_compensation") val cashCompensation: Int = 0,
    @ColumnInfo(name = "cash_collected") val cashCollected: Int = 0,
    @ColumnInfo(name = "order_adjustment") val orderAdjustment: Int = 0,
    @ColumnInfo(name = "time_accepted") val timeAccepted: String? = null,
    @ColumnInfo(name = "time_arrived") val timeArrived: String? = null,
    @ColumnInfo(name = "time_picked_up") val timePickedUp: String? = null,
    @ColumnInfo(name = "time_completed") val timeCompleted: String? = null,
    @ColumnInfo(name = "raw_text") val rawText: String,
    @ColumnInfo(name = "parse_confidence") val parseConfidence: Double = 0.0,
    @ColumnInfo(name = "source_snapshot_id") val sourceSnapshotId: String,
    @ColumnInfo(name = "captured_at") val capturedAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
