package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.QuickEntryCategoryDao
import com.driverfinance.data.local.dao.QuickEntryDao
import com.driverfinance.data.local.dao.QuickEntryPresetDao
import com.driverfinance.data.local.entity.QuickEntryCategoryEntity
import com.driverfinance.data.local.entity.QuickEntryEntity
import com.driverfinance.data.local.entity.QuickEntryPresetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickEntryRepository @Inject constructor(
    private val entryDao: QuickEntryDao,
    private val categoryDao: QuickEntryCategoryDao,
    private val presetDao: QuickEntryPresetDao
) {

    // --- Quick Entries ---

    suspend fun saveEntry(entry: QuickEntryEntity) = entryDao.insert(entry)

    suspend fun updateEntry(entry: QuickEntryEntity) = entryDao.update(entry)

    suspend fun getEntryById(id: String): QuickEntryEntity? = entryDao.getById(id)

    fun getTodayEntries(date: String, type: String): Flow<List<QuickEntryEntity>> =
        entryDao.getByDateAndType(date, type)

    fun getTodayAllEntries(date: String): Flow<List<QuickEntryEntity>> =
        entryDao.getByDate(date)

    suspend fun getTodaySummary(date: String, type: String): Int =
        entryDao.getSumByDateAndType(date, type) ?: 0

    suspend fun getTodayCount(date: String, type: String): Int =
        entryDao.getCountByDateAndType(date, type)

    // --- Categories ---

    fun getActiveCategories(type: String): Flow<List<QuickEntryCategoryEntity>> =
        categoryDao.getActiveByType(type)

    fun getActiveNonSystemCategories(type: String): Flow<List<QuickEntryCategoryEntity>> =
        categoryDao.getActiveNonSystemByType(type)

    suspend fun getCategoryById(id: String): QuickEntryCategoryEntity? =
        categoryDao.getById(id)

    suspend fun saveCategory(category: QuickEntryCategoryEntity) = categoryDao.insert(category)

    suspend fun updateCategory(category: QuickEntryCategoryEntity) = categoryDao.update(category)

    suspend fun getActiveCategoryCount(type: String): Int =
        categoryDao.getActiveNonSystemCount(type)

    // --- Presets ---

    fun getPresetsByCategory(categoryId: String): Flow<List<QuickEntryPresetEntity>> =
        presetDao.getByCategoryId(categoryId)

    suspend fun getPresetsByCategoryOnce(categoryId: String): List<QuickEntryPresetEntity> =
        presetDao.getByCategoryIdOnce(categoryId)

    suspend fun savePreset(preset: QuickEntryPresetEntity) = presetDao.insert(preset)

    suspend fun deletePreset(presetId: String) = presetDao.deleteById(presetId)
}
