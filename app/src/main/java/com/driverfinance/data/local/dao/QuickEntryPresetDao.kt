package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.driverfinance.data.local.entity.QuickEntryPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickEntryPresetDao {

    @Query("SELECT * FROM quick_entry_presets WHERE category_id = :categoryId ORDER BY sort_order ASC")
    fun getByCategoryId(categoryId: String): Flow<List<QuickEntryPresetEntity>>

    @Query("SELECT * FROM quick_entry_presets WHERE category_id = :categoryId ORDER BY sort_order ASC")
    suspend fun getByCategoryIdOnce(categoryId: String): List<QuickEntryPresetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: QuickEntryPresetEntity)

    @Query("DELETE FROM quick_entry_presets WHERE id = :presetId")
    suspend fun deleteById(presetId: String)
}
