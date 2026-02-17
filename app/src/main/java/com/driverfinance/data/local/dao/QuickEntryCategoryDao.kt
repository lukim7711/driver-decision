package com.driverfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.driverfinance.data.local.entity.QuickEntryCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickEntryCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: QuickEntryCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<QuickEntryCategoryEntity>)

    @Update
    suspend fun update(category: QuickEntryCategoryEntity)

    @Query("SELECT * FROM quick_entry_categories WHERE id = :id")
    suspend fun getById(id: String): QuickEntryCategoryEntity?

    @Query("SELECT * FROM quick_entry_categories WHERE is_active = 1 ORDER BY sort_order ASC")
    fun getActive(): Flow<List<QuickEntryCategoryEntity>>

    @Query("SELECT * FROM quick_entry_categories WHERE is_active = 1 AND type = :type ORDER BY sort_order ASC")
    fun getActiveByType(type: String): Flow<List<QuickEntryCategoryEntity>>

    @Query("SELECT * FROM quick_entry_categories WHERE is_active = 1 AND is_system = 0 AND type = :type ORDER BY sort_order ASC")
    fun getActiveNonSystemByType(type: String): Flow<List<QuickEntryCategoryEntity>>

    @Query("SELECT COUNT(*) FROM quick_entry_categories WHERE is_active = 1 AND is_system = 0 AND type = :type")
    suspend fun getActiveNonSystemCount(type: String): Int

    @Query("UPDATE quick_entry_categories SET is_active = 0, updated_at = :updatedAt WHERE id = :id AND is_system = 0")
    suspend fun softDelete(id: String, updatedAt: String)
}
