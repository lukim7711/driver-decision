package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "captured_orders",
    indices = [Index("trip_id"), Index("order_sn"), Index("source_snapshot_id")]
)
data class CapturedOrderEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "trip_id") val tripId: String,
    @ColumnInfo(name = "order_sn") val orderSn: String,
    @ColumnInfo(name = "order_id") val orderId: String? = null,
    @ColumnInfo(name = "pickup_address") val pickupAddress: String? = null,
    @ColumnInfo(name = "delivery_address") val deliveryAddress: String? = null,
    @ColumnInfo(name = "seller_name") val sellerName: String? = null,
    @ColumnInfo(name = "parcel_weight") val parcelWeight: String? = null,
    @ColumnInfo(name = "parcel_dimensions") val parcelDimensions: String? = null,
    @ColumnInfo(name = "payment_method") val paymentMethod: String? = null,
    @ColumnInfo(name = "payment_amount") val paymentAmount: Int? = null,
    @ColumnInfo(name = "raw_text") val rawText: String,
    @ColumnInfo(name = "parse_confidence") val parseConfidence: Double = 0.0,
    @ColumnInfo(name = "source_snapshot_id") val sourceSnapshotId: String,
    @ColumnInfo(name = "captured_at") val capturedAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
