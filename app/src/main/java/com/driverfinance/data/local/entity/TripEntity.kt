package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trips",
    indices = [Index("trip_date"), Index("source_snapshot_id")]
)
data class TripEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "trip_date") val tripDate: String,
    @ColumnInfo(name = "trip_code") val tripCode: String? = null,
    @ColumnInfo(name = "service_type") val serviceType: String,
    @ColumnInfo(name = "source_snapshot_id") val sourceSnapshotId: String,
    @ColumnInfo(name = "captured_at") val capturedAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
