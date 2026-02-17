package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history_trips",
    indices = [Index("trip_date"), Index("linked_trip_id"), Index("source_snapshot_id")]
)
data class HistoryTripEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "linked_trip_id") val linkedTripId: String? = null,
    @ColumnInfo(name = "trip_date") val tripDate: String,
    @ColumnInfo(name = "trip_time") val tripTime: String,
    @ColumnInfo(name = "service_type") val serviceType: String,
    @ColumnInfo(name = "total_earning") val totalEarning: Int,
    @ColumnInfo(name = "is_combined") val isCombined: Int = 0,
    @ColumnInfo(name = "restaurant_name") val restaurantName: String? = null,
    @ColumnInfo(name = "raw_text") val rawText: String,
    @ColumnInfo(name = "source_snapshot_id") val sourceSnapshotId: String,
    @ColumnInfo(name = "captured_at") val capturedAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
