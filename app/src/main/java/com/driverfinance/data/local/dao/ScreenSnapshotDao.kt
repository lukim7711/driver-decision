package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.ScreenSnapshotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenSnapshotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(snapshot: ScreenSnapshotEntity)

    @Update
    suspend fun update(snapshot: ScreenSnapshotEntity)

    @Query("SELECT * FROM screen_snapshots WHERE id = :id")
    suspend fun getById(id: String): ScreenSnapshotEntity?

    @Query("SELECT * FROM screen_snapshots WHERE screen_type = :type ORDER BY captured_at DESC")
    fun getByType(type: String): Flow<List<ScreenSnapshotEntity>>

    @Query("SELECT COUNT(*) FROM screen_snapshots WHERE screen_type = :type")
    suspend fun getByTypeCount(type: String): Int

    @Query("SELECT * FROM screen_snapshots WHERE is_processed = 0 ORDER BY captured_at ASC")
    suspend fun getUnprocessed(): List<ScreenSnapshotEntity>

    @Query("UPDATE screen_snapshots SET is_processed = 1 WHERE id = :id")
    suspend fun markProcessed(id: String)
}
