package com.driverfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "screen_snapshots",
    indices = [Index("screen_type"), Index("is_processed")]
)
data class ScreenSnapshotEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "screen_type") val screenType: String,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "raw_text") val rawText: String? = null,
    @ColumnInfo(name = "node_tree_json") val nodeTreeJson: String? = null,
    @ColumnInfo(name = "is_processed") val isProcessed: Int = 0,
    @ColumnInfo(name = "captured_at") val capturedAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
